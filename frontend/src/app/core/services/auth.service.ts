import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap } from 'rxjs';
import { Router } from '@angular/router';
import { TokenService } from './token.service';
import { User, LoginRequest, RegisterRequest, JwtResponse, UserRole } from '../models/user.model';
import { environment } from '../../../environments/environment';

@Injectable({ providedIn: 'root' })
export class AuthService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  public currentUser$ = this.currentUserSubject.asObservable();

  constructor(
    private http: HttpClient,
    private tokenService: TokenService,
    private router: Router
  ) {
    this.loadUserFromToken();
  }

  private loadUserFromToken(): void {
    const token = this.tokenService.getAccessToken();
    if (token) {
      try {
        const payload = this.decodeToken(token);
        if (payload) {
          this.currentUserSubject.next({
            id: payload.userId,
            email: payload.sub,
            nom: payload.nom || '',
            prenom: payload.prenom || '',
            role: payload.role as UserRole,
            active: true
          });
        }
      } catch (e) {
        this.tokenService.clearTokens();
      }
    }
  }

  private decodeToken(token: string): any {
    const payload = token.split('.')[1];
    if (!payload) return null;
    const decoded = atob(payload);
    return JSON.parse(decoded);
  }

  login(credentials: LoginRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/login`, credentials).pipe(
      tap(response => {
        this.tokenService.setTokens(response.accessToken, response.refreshToken);
        this.loadUserFromToken();
      })
    );
  }

  register(data: RegisterRequest): Observable<JwtResponse> {
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/register`, data).pipe(
      tap(response => {
        this.tokenService.setTokens(response.accessToken, response.refreshToken);
        this.loadUserFromToken();
      })
    );
  }

  refreshToken(): Observable<JwtResponse> {
    const refreshToken = this.tokenService.getRefreshToken();
    return this.http.post<JwtResponse>(`${this.apiUrl}/auth/refresh`, { refreshToken });
  }

  logout(): void {
    this.http.post(`${this.apiUrl}/auth/logout`, {}).subscribe({
      complete: () => {
        this.tokenService.clearTokens();
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
      },
      error: () => {
        this.tokenService.clearTokens();
        this.currentUserSubject.next(null);
        this.router.navigate(['/auth/login']);
      }
    });
  }

  isAuthenticated(): boolean {
    return this.tokenService.hasToken();
  }

  hasRole(roles: UserRole[]): boolean {
    const user = this.currentUserSubject.value;
    return user ? roles.includes(user.role) : false;
  }

  getCurrentUser(): User | null {
    return this.currentUserSubject.value;
  }
}
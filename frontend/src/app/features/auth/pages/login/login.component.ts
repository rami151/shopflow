import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormBuilder, FormGroup, Validators, ReactiveFormsModule } from '@angular/forms';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, RouterLink, ReactiveFormsModule],
  template: `
    <div class="min-h-screen flex items-center justify-center bg-gradient-to-br from-dark-50 to-dark-100 py-12 px-4">
      <div class="max-w-md w-full">
        <div class="text-center mb-8">
          <div class="w-16 h-16 bg-gradient-to-br from-primary-500 to-primary-700 rounded-2xl flex items-center justify-center mx-auto mb-4">
            <svg class="w-8 h-8 text-white" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
            </svg>
          </div>
          <h1 class="text-2xl font-bold text-dark-900">Welcome Back</h1>
          <p class="text-dark-500 mt-2">Sign in to your account</p>
        </div>

        <div class="bg-white rounded-2xl shadow-lg p-8">
          <form [formGroup]="loginForm" (ngSubmit)="onSubmit()">
            <div class="space-y-5">
              <div>
                <label for="email" class="block text-sm font-medium text-dark-700 mb-1">Email</label>
                <input
                  type="email"
                  id="email"
                  formControlName="email"
                  class="input-field"
                  placeholder="you@example.com"
                />
                @if (loginForm.get('email')?.invalid && loginForm.get('email')?.touched) {
                  <p class="text-red-500 text-sm mt-1">Please enter a valid email</p>
                }
              </div>

              <div>
                <label for="password" class="block text-sm font-medium text-dark-700 mb-1">Password</label>
                <input
                  type="password"
                  id="password"
                  formControlName="password"
                  class="input-field"
                  placeholder="••••••••"
                />
                @if (loginForm.get('password')?.invalid && loginForm.get('password')?.touched) {
                  <p class="text-red-500 text-sm mt-1">Password is required</p>
                }
              </div>

              <div class="flex items-center justify-between">
                <label class="flex items-center">
                  <input type="checkbox" class="rounded border-dark-300 text-primary-600 focus:ring-primary-500" />
                  <span class="ml-2 text-sm text-dark-600">Remember me</span>
                </label>
                <a href="#" class="text-sm text-primary-600 hover:text-primary-700">Forgot password?</a>
              </div>

              <button
                type="submit"
                [disabled]="loading()"
                class="w-full btn-primary py-3 flex items-center justify-center"
              >
                @if (loading()) {
                  <svg class="w-5 h-5 animate-spin mr-2" fill="none" viewBox="0 0 24 24">
                    <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                    <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"></path>
                  </svg>
                }
                Sign In
              </button>
            </div>
          </form>

          <div class="mt-6 text-center">
            <p class="text-dark-600">
              Don't have an account?
              <a routerLink="/auth/register" class="text-primary-600 hover:text-primary-700 font-medium">Create one</a>
            </p>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class LoginComponent {
  loginForm: FormGroup;
  loading = signal(false);

  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private toastService: ToastService,
    private router: Router
  ) {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }

  onSubmit(): void {
    if (this.loginForm.invalid) {
      this.loginForm.markAllAsTouched();
      return;
    }

    this.loading.set(true);
    this.authService.login(this.loginForm.value).subscribe({
      next: () => {
        this.toastService.success('Welcome back!');
        const user = this.authService.getCurrentUser();
        this.redirectByRole(user?.role);
      },
      error: (err) => {
        this.loading.set(false);
        this.toastService.error(err.error?.message || 'Login failed. Please try again.');
      }
    });
  }

  private redirectByRole(role?: string): void {
    switch (role) {
      case 'ADMIN':
        this.router.navigate(['/admin']);
        break;
      case 'SELLER':
        this.router.navigate(['/seller']);
        break;
      default:
        this.router.navigate(['/dashboard']);
    }
  }
}
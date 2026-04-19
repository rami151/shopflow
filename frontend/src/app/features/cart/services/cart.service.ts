import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, tap, map } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface CartItem {
  id: number;
  productId: number;
  productName: string;
  productImage: string;
  variantId?: number;
  variantName?: string;
  quantite: number;
  prix: number;
}

export interface Cart {
  id: number;
  userId: number;
  items: CartItem[];
  couponCode?: string;
  discount: number;
  subtotal: number;
  shipping: number;
  total: number;
}

@Injectable({ providedIn: 'root' })
export class CartService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;
  private cartSubject = new BehaviorSubject<Cart | null>(null);
  public cart$ = this.cartSubject.asObservable();

  public cartItemCount$ = this.cart$.pipe(
    map(cart => cart?.items.reduce((sum, item) => sum + item.quantite, 0) || 0)
  );

  constructor(private http: HttpClient) {}

  loadCart(): void {
    this.http.get<Cart>(`${this.apiUrl}/cart`).subscribe({
      next: (cart) => this.cartSubject.next(cart),
      error: () => this.cartSubject.next(null)
    });
  }

  getCart(): Observable<Cart | null> {
    return this.http.get<Cart>(`${this.apiUrl}/cart`);
  }

  addItem(productId: number, quantity: number, variantId?: number): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/cart/items`, { productId, quantity, variantId }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  updateItemQuantity(itemId: number, quantity: number): Observable<Cart> {
    return this.http.put<Cart>(`${this.apiUrl}/cart/items/${itemId}`, { quantity }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  removeItem(itemId: number): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/cart/items/${itemId}`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  applyCoupon(code: string): Observable<Cart> {
    return this.http.post<Cart>(`${this.apiUrl}/cart/coupon`, { code }).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  removeCoupon(): Observable<Cart> {
    return this.http.delete<Cart>(`${this.apiUrl}/cart/coupon`).pipe(
      tap(cart => this.cartSubject.next(cart))
    );
  }

  clearCart(): void {
    this.cartSubject.next(null);
  }
}
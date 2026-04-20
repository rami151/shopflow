import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, Cart } from '../../services/cart.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-cart-page',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Your Cart</h1>

        @if (loading()) {
          <div class="bg-white rounded-lg shadow p-8 animate-pulse">
            <div class="space-y-4">
              @for (i of [1,2,3]; track i) {
                <div class="flex gap-4">
                  <div class="w-24 h-24 bg-dark-200 rounded"></div>
                  <div class="flex-1">
                    <div class="h-4 bg-dark-200 rounded w-3/4 mb-2"></div>
                    <div class="h-4 bg-dark-200 rounded w-1/2"></div>
                  </div>
                </div>
              }
            </div>
          </div>
        } @else if (!cart() || cart()!.items.length === 0) {
          <div class="bg-white rounded-lg shadow p-12 text-center">
            <svg class="w-16 h-16 mx-auto text-dark-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
            </svg>
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Your cart is empty</h3>
            <p class="text-dark-500 mb-6">Looks like you haven't added anything to your cart yet.</p>
            <a routerLink="/products" class="btn-primary px-6 py-3 inline-block">Start Shopping</a>
          </div>
        } @else {
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <!-- Cart Items -->
            <div class="lg:col-span-2 space-y-4">
              @for (item of cart()!.items; track item.id) {
                <div class="bg-white rounded-lg shadow p-4 flex gap-4">
                  <div class="w-24 h-24 bg-dark-100 rounded-lg overflow-hidden flex-shrink-0">
                    @if (item.productImage) {
                      <img [src]="item.productImage" [alt]="item.productName" (error)="onImageError($event)" class="w-full h-full object-cover" />
                    } @else {
                      <div class="w-full h-full flex items-center justify-center">
                        <svg class="w-8 h-8 text-dark-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10m-8 4l-8-4m8 4l8-4m-8 4v10m-8-4l8 4" />
                        </svg>
                      </div>
                    }
                  </div>
                  <div class="flex-1">
                    <h3 class="font-semibold text-dark-900">{{ item.productName }}</h3>
                    @if (item.variantName) {
                      <p class="text-sm text-dark-500">{{ item.variantName }}</p>
                    }
                    <p class="text-lg font-bold text-primary-600 mt-1">{{ item.prix | currency:'TND' }}</p>
                  </div>
                  <div class="flex flex-col items-end justify-between">
                    <button (click)="removeItem(item.id)" class="text-dark-400 hover:text-red-500">
                      <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M6 18L18 6M6 6l12 12" />
                      </svg>
                    </button>
                    <div class="flex items-center gap-2">
                      <button
                        (click)="updateQuantity(item.id, item.quantite - 1)"
                        class="w-8 h-8 rounded border border-dark-200 flex items-center justify-center hover:bg-dark-50"
                      >
                        -
                      </button>
                      <span class="w-8 text-center">{{ item.quantite }}</span>
                      <button
                        (click)="updateQuantity(item.id, item.quantite + 1)"
                        class="w-8 h-8 rounded border border-dark-200 flex items-center justify-center hover:bg-dark-50"
                      >
                        +
                      </button>
                    </div>
                  </div>
                </div>
              }
            </div>

            <!-- Order Summary -->
            <div class="lg:col-span-1">
              <div class="bg-white rounded-lg shadow p-6 sticky top-24">
                <h2 class="text-lg font-semibold text-dark-900 mb-4">Order Summary</h2>

                <!-- Coupon -->
                <div class="mb-4">
                  <label class="block text-sm font-medium text-dark-700 mb-1">Coupon Code</label>
                  <div class="flex gap-2">
                    <input
                      type="text"
                      [(ngModel)]="couponCode"
                      placeholder="Enter code"
                      class="input-field"
                    />
                    <button
                      (click)="applyCoupon()"
                      [disabled]="!couponCode || applyingCoupon()"
                      class="btn-secondary"
                    >
                      Apply
                    </button>
                  </div>
                  @if (cart()!.couponCode) {
                    <p class="text-sm text-green-600 mt-1">
                      Coupon applied: {{ cart()!.couponCode }}
                      <button (click)="removeCoupon()" class="ml-2 text-red-500">Remove</button>
                    </p>
                  }
                </div>

                <div class="border-t border-dark-100 pt-4 space-y-2">
                  <div class="flex justify-between text-dark-600">
                    <span>Subtotal</span>
                    <span>{{ cart()!.subtotal | currency:'TND' }}</span>
                  </div>
                  <div class="flex justify-between text-dark-600">
                    <span>Shipping</span>
                    <span>{{ cart()!.shipping | currency:'TND' }}</span>
                  </div>
                  @if (cart()!.discount > 0) {
                    <div class="flex justify-between text-green-600">
                      <span>Discount</span>
                      <span>-{{ cart()!.discount | currency:'TND' }}</span>
                    </div>
                  }
                  <div class="flex justify-between text-lg font-bold text-dark-900 pt-2 border-t border-dark-100">
                    <span>Total</span>
                    <span>{{ cart()!.total | currency:'TND' }}</span>
                  </div>
                </div>

                <a
                  routerLink="/checkout"
                  class="block w-full btn-primary text-center mt-6 py-3"
                >
                  Proceed to Checkout
                </a>
                <a
                  routerLink="/products"
                  class="block w-full btn-secondary text-center mt-2 py-3"
                >
                  Continue Shopping
                </a>
              </div>
            </div>
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class CartPageComponent implements OnInit {
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  cart = signal<Cart | null>(null);
  loading = signal(true);
  couponCode = '';
  applyingCoupon = signal(false);

  constructor(
    private cartService: CartService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadCart();
  }

  private loadCart(): void {
    this.loading.set(true);
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.loading.set(false);
      },
      error: () => {
        this.cart.set(null);
        this.loading.set(false);
      }
    });
  }

  updateQuantity(itemId: number, quantity: number): void {
    if (quantity < 1) return;
    this.cartService.updateItemQuantity(itemId, quantity).subscribe({
      next: (cart) => this.cart.set(cart),
      error: () => this.toastService.error('Failed to update quantity')
    });
  }

  removeItem(itemId: number): void {
    this.cartService.removeItem(itemId).subscribe({
      next: (cart) => this.cart.set(cart),
      error: () => this.toastService.error('Failed to remove item')
    });
  }

  applyCoupon(): void {
    if (!this.couponCode) return;
    this.applyingCoupon.set(true);
    this.cartService.applyCoupon(this.couponCode).subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.toastService.success('Coupon applied!');
        this.applyingCoupon.set(false);
      },
      error: () => {
        this.toastService.error('Invalid coupon code');
        this.applyingCoupon.set(false);
      }
    });
  }

  removeCoupon(): void {
    this.cartService.removeCoupon().subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.couponCode = '';
      }
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement | null;
    if (img && img.src !== this.fallbackImage) {
      img.src = this.fallbackImage;
    }
  }
}
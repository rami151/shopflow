import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { CartService, Cart } from '../../../cart/services/cart.service';
import { OrderService, Address } from '../../services/order.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-checkout-page',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Checkout</h1>

        @if (loading()) {
          <div class="bg-white rounded-lg shadow p-8 animate-pulse">
            <div class="h-8 bg-dark-200 rounded w-1/2 mb-4"></div>
            <div class="h-4 bg-dark-200 rounded w-3/4"></div>
          </div>
        } @else if (cart() && cart()!.items.length > 0) {
          <div class="grid grid-cols-1 lg:grid-cols-3 gap-8">
            <div class="lg:col-span-2 space-y-6">
              <!-- Address Selection -->
              <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-lg font-semibold text-dark-900 mb-4">Delivery Address</h2>
                @if (addresses().length > 0) {
                  <div class="space-y-3">
                    @for (address of addresses(); track address.id) {
                      <label
                        class="flex items-start gap-3 p-4 rounded-lg border cursor-pointer"
                        [class]="selectedAddress() === address.id ? 'border-primary-600 bg-primary-50' : 'border-dark-200 hover:border-dark-300'"
                      >
                        <input
                          type="radio"
                          name="address"
                          [value]="address.id"
                          [checked]="selectedAddress() === address.id"
                          (change)="selectedAddress.set(address.id)"
                          class="mt-1 text-primary-600"
                        />
                        <div>
                          <p class="font-medium text-dark-900">{{ address.rue }}</p>
                          <p class="text-sm text-dark-500">{{ address.ville }} {{ address.codePostal }}, {{ address.pays }}</p>
                        </div>
                      </label>
                    }
                  </div>
                } @else {
                  <p class="text-dark-500">No addresses found. Please add one in your dashboard.</p>
                }
              </div>

              <!-- Order Items -->
              <div class="bg-white rounded-lg shadow p-6">
                <h2 class="text-lg font-semibold text-dark-900 mb-4">Order Items</h2>
                <div class="space-y-4">
                  @for (item of cart()!.items; track item.id) {
                    <div class="flex gap-4">
                      <div class="w-16 h-16 bg-dark-100 rounded-lg overflow-hidden flex-shrink-0">
                        <img [src]="item.productImageUrl" [alt]="item.productName" (error)="onImageError($event)" class="w-full h-full object-cover" />
                      </div>
                      <div class="flex-1">
                        <h3 class="font-medium text-dark-900">{{ item.productName }}</h3>
                        <p class="text-sm text-dark-500">Qty: {{ item.quantite }}</p>
                      </div>
                      <p class="font-medium text-dark-900">{{ item.sousTotal | currency:'TND' }}</p>
                    </div>
                  }
                </div>
              </div>
            </div>

            <!-- Order Summary -->
            <div class="lg:col-span-1">
              <div class="bg-white rounded-lg shadow p-6 sticky top-24">
                <h2 class="text-lg font-semibold text-dark-900 mb-4">Order Summary</h2>
                <div class="space-y-2">
                  <div class="flex justify-between text-dark-600">
                    <span>Subtotal</span>
                    <span>{{ cart()!.sousTotal | currency:'TND' }}</span>
                  </div>
                  <div class="flex justify-between text-dark-600">
                    <span>Shipping</span>
                    <span>{{ cart()!.fraisLivraison | currency:'TND' }}</span>
                  </div>
                  @if (cart()!.couponCode) {
                    <div class="flex justify-between text-green-600">
                      <span>Coupon ({{ cart()!.couponCode }})</span>
                      <span>Applied</span>
                    </div>
                  }
                  <div class="flex justify-between text-lg font-bold text-dark-900 pt-2 border-t border-dark-100">
                    <span>Total</span>
                    <span>{{ cart()!.totalTTC | currency:'TND' }}</span>
                  </div>
                </div>

                <button
                  (click)="placeOrder()"
                  [disabled]="!selectedAddress() || placing()"
                  class="w-full btn-primary text-center mt-6 py-3"
                >
                  @if (placing()) {
                    <svg class="w-5 h-5 animate-spin mr-2 inline" fill="none" viewBox="0 0 24 24">
                      <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                      <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                    </svg>
                  }
                  Place Order
                </button>
              </div>
            </div>
          </div>
        } @else {
          <div class="bg-white rounded-lg shadow p-12 text-center">
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Your cart is empty</h3>
            <a routerLink="/products" class="text-primary-600 hover:text-primary-700">Continue shopping</a>
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class CheckoutPageComponent implements OnInit {
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  cart = signal<Cart | null>(null);
  addresses = signal<Address[]>([]);
  loading = signal(true);
  placing = signal(false);
  selectedAddress = signal<number | null>(null);

  constructor(
    private cartService: CartService,
    private orderService: OrderService,
    private toastService: ToastService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.cartService.getCart().subscribe({
      next: (cart) => {
        this.cart.set(cart);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    this.orderService.getAddresses().subscribe({
      next: (addresses) => {
        this.addresses.set(addresses);
        if (addresses.length > 0) {
          const defaultAddr = addresses.find(a => a.principale);
          this.selectedAddress.set(defaultAddr ? defaultAddr.id : addresses[0].id);
        }
      }
    });
  }

  placeOrder(): void {
    const addressId = this.selectedAddress();
    if (!addressId) {
      this.toastService.error('Please select a delivery address');
      return;
    }

    this.placing.set(true);
    this.orderService.placeOrder(addressId).subscribe({
      next: (order) => {
        this.toastService.success('Order placed successfully!');
        this.cartService.clearCart();
        this.router.navigate(['/order-confirmation', order.id]);
      },
      error: () => {
        this.toastService.error('Failed to place order');
        this.placing.set(false);
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
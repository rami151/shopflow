import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { OrderService, Order } from '../../services/order.service';

@Component({
  selector: 'app-order-confirmation',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8">
        @if (loading()) {
          <div class="bg-white rounded-lg shadow p-8 animate-pulse">
            <div class="h-8 bg-dark-200 rounded w-1/2 mb-4"></div>
          </div>
        } @else if (order()) {
          <div class="bg-white rounded-lg shadow p-8 text-center">
            <div class="w-16 h-16 mx-auto bg-green-100 rounded-full flex items-center justify-center mb-4">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h1 class="text-2xl font-bold text-dark-900 mb-2">Order Placed Successfully!</h1>
            <p class="text-dark-500 mb-6">Thank you for your purchase.</p>

            <div class="bg-dark-50 rounded-lg p-4 mb-6 text-left">
              <div class="flex justify-between mb-2">
                <span class="text-dark-500">Order Number</span>
                <span class="font-medium text-dark-900">{{ order()!.numeroCommande }}</span>
              </div>
              <div class="flex justify-between mb-2">
                <span class="text-dark-500">Status</span>
                <span class="font-medium text-dark-900">{{ order()!.statut }}</span>
              </div>
              <div class="flex justify-between">
                <span class="text-dark-500">Total</span>
                <span class="font-medium text-dark-900">{{ order()!.totalTTC | currency:'TND' }}</span>
              </div>
            </div>

            <div class="flex flex-col sm:flex-row gap-4">
              <a routerLink="/dashboard/orders" class="flex-1 btn-primary py-3 text-center">View Order Details</a>
              <a routerLink="/products" class="flex-1 btn-secondary py-3 text-center">Continue Shopping</a>
            </div>
          </div>
        } @else {
          <div class="bg-white rounded-lg shadow p-8 text-center">
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Order not found</h3>
            <a routerLink="/dashboard/orders" class="text-primary-600 hover:text-primary-700">View all orders</a>
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class OrderConfirmationComponent implements OnInit {
  order = signal<Order | null>(null);
  loading = signal(true);

  constructor(
    private route: ActivatedRoute,
    private orderService: OrderService
  ) {}

  ngOnInit(): void {
    const orderId = this.route.snapshot.paramMap.get('orderId');
    if (orderId) {
      this.orderService.getOrderById(+orderId).subscribe({
        next: (order) => {
          this.order.set(order);
          this.loading.set(false);
        },
        error: () => this.loading.set(false)
      });
    } else {
      this.loading.set(false);
    }
  }
}
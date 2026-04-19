import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { OrderService, Order } from '../../../checkout/services/order.service';

@Component({
  selector: 'app-my-orders',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">My Orders</h1>
        @if (loading()) {
          <div class="bg-white rounded-lg shadow p-8 animate-pulse">
            <div class="space-y-4">
              @for (i of [1,2,3]; track i) {
                <div class="h-16 bg-dark-100 rounded"></div>
              }
            </div>
          </div>
        } @else if (orders().length === 0) {
          <div class="bg-white rounded-lg shadow p-12 text-center">
            <p class="text-dark-500">No orders yet.</p>
            <a routerLink="/products" class="text-primary-600 hover:text-primary-700 mt-2 inline-block">Start shopping</a>
          </div>
        } @else {
          <div class="bg-white rounded-lg shadow overflow-hidden">
            <div class="divide-y divide-dark-100">
              @for (order of orders(); track order.id) {
                <div class="p-6 flex flex-col md:flex-row md:items-center justify-between gap-4">
                  <div>
                    <p class="font-medium text-dark-900">{{ order.orderNumber }}</p>
                    <p class="text-sm text-dark-500">{{ order.items.length }} items</p>
                  </div>
                  <div class="flex items-center gap-4">
                    <span
                      class="px-3 py-1 rounded-full text-xs font-medium"
                      [class]="{
                        'bg-yellow-100 text-yellow-800': order.status === 'PENDING',
                        'bg-blue-100 text-blue-800': order.status === 'PAID' || order.status === 'SHIPPED',
                        'bg-green-100 text-green-800': order.status === 'DELIVERED',
                        'bg-red-100 text-red-800': order.status === 'CANCELLED'
                      }"
                    >
                      {{ order.status }}
                    </span>
                    <p class="font-medium text-dark-900">{{ order.total | currency:'TND' }}</p>
                  </div>
                </div>
              }
            </div>
          </div>
        }
      </div>
    </div>
  `
})
export class MyOrdersComponent implements OnInit {
  orders = signal<Order[]>([]);
  loading = signal(true);

  constructor(private orderService: OrderService) {}

  ngOnInit(): void {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => { this.orders.set(orders); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}
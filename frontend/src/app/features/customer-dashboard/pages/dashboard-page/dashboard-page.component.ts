import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, Router } from '@angular/router';
import { OrderService, Order } from '../../../checkout/services/order.service';
import { AuthService } from '../../../../core/services/auth.service';
import { User } from '../../../../core/models/user.model';

@Component({
  selector: 'app-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">My Dashboard</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Total Orders</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ orders().length }}</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Pending Orders</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ pendingCount() }}</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Delivered</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ deliveredCount() }}</p>
          </div>
        </div>

        <div class="bg-white rounded-lg shadow">
          <div class="p-6 border-b border-dark-100 flex justify-between items-center">
            <h2 class="text-lg font-semibold text-dark-900">Recent Orders</h2>
            <a routerLink="/dashboard/orders" class="text-primary-600 hover:text-primary-700 text-sm">View All</a>
          </div>
          @if (loading()) {
            <div class="p-6">
              <div class="animate-pulse space-y-4">
                @for (i of [1,2,3]; track i) {
                  <div class="h-16 bg-dark-100 rounded"></div>
                }
              </div>
            </div>
          } @else if (orders().length === 0) {
            <div class="p-12 text-center">
              <p class="text-dark-500">No orders yet.</p>
              <a routerLink="/products" class="text-primary-600 hover:text-primary-700 mt-2 inline-block">Start shopping</a>
            </div>
          } @else {
            <div class="divide-y divide-dark-100">
              @for (order of orders().slice(0, 5); track order.id) {
                <div class="p-6 flex items-center justify-between">
                  <div>
                    <p class="font-medium text-dark-900">{{ order.numeroCommande }}</p>
                    <p class="text-sm text-dark-500">{{ order.items.length }} items - {{ order.totalTTC | currency:'TND' }}</p>
                  </div>
                  <div class="text-right">
                    <span
                      class="px-3 py-1 rounded-full text-xs font-medium"
                      [class]="{
                        'bg-yellow-100 text-yellow-800': order.statut === 'PENDING',
                        'bg-blue-100 text-blue-800': order.statut === 'PAID' || order.statut === 'PROCESSING' || order.statut === 'SHIPPED',
                        'bg-green-100 text-green-800': order.statut === 'DELIVERED',
                        'bg-red-100 text-red-800': order.statut === 'CANCELLED'
                      }"
                    >
                      {{ order.statut }}
                    </span>
                    <p class="text-xs text-dark-500 mt-1">{{ order.dateCommande | date:'shortDate' }}</p>
                  </div>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class DashboardPageComponent implements OnInit {
  orders = signal<Order[]>([]);
  loading = signal(true);
  user: User | null = null;

  constructor(
    private orderService: OrderService,
    private authService: AuthService
  ) {}

  ngOnInit(): void {
    this.user = this.authService.getCurrentUser();
    this.loadOrders();
  }

  private loadOrders(): void {
    this.orderService.getMyOrders().subscribe({
      next: (orders) => {
        this.orders.set(orders);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  pendingCount(): number {
    return this.orders().filter(o => o.statut === 'PENDING' || o.statut === 'PAID').length;
  }

  deliveredCount(): number {
    return this.orders().filter(o => o.statut === 'DELIVERED').length;
  }
}
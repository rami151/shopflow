import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-seller-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Seller Dashboard</h1>

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Total Products</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ stats().products }}</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Total Orders</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ stats().orders }}</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Revenue</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ stats().revenue | currency:'TND' }}</p>
          </div>
        </div>

        <div class="grid grid-cols-1 md:grid-cols-2 gap-6">
          <div class="bg-white rounded-lg shadow p-6">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-lg font-semibold text-dark-900">My Products</h2>
              <a routerLink="/seller/products" class="text-primary-600 hover:text-primary-700 text-sm">Manage</a>
            </div>
            <p class="text-dark-500">View and manage your product inventory.</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <div class="flex justify-between items-center mb-4">
              <h2 class="text-lg font-semibold text-dark-900">Received Orders</h2>
              <a routerLink="/seller/orders" class="text-primary-600 hover:text-primary-700 text-sm">View All</a>
            </div>
            <p class="text-dark-500">Track orders containing your products.</p>
          </div>
        </div>
      </div>
    </div>
  `
})
export class SellerDashboardPageComponent implements OnInit {
  stats = signal({ products: 0, orders: 0, revenue: 0 });
  loading = signal(true);

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any>(`${environment.apiUrl}${environment.apiPrefix}/dashboard/seller`).subscribe({
      next: (data) => {
        this.stats.set(data);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
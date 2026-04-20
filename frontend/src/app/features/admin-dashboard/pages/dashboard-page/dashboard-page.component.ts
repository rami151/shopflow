import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-admin-dashboard-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h1 class="text-2xl font-bold text-dark-900 mb-8">Admin Dashboard</h1>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-6 mb-8">
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Total Users</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ stats().users }}</p>
          </div>
          <div class="bg-white rounded-lg shadow p-6">
            <h3 class="text-sm font-medium text-dark-500">Total Sellers</h3>
            <p class="text-3xl font-bold text-dark-900 mt-2">{{ stats().sellers }}</p>
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

        <div class="grid grid-cols-1 md:grid-cols-3 gap-6">
          <a routerLink="/admin/categories" class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <h2 class="text-lg font-semibold text-dark-900">Categories</h2>
            <p class="text-dark-500 mt-2">Manage product categories</p>
          </a>
          <a routerLink="/admin/coupons" class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <h2 class="text-lg font-semibold text-dark-900">Coupons</h2>
            <p class="text-dark-500 mt-2">Manage discount coupons</p>
          </a>
          <a routerLink="/admin/users" class="bg-white rounded-lg shadow p-6 hover:shadow-lg transition">
            <h2 class="text-lg font-semibold text-dark-900">Users</h2>
            <p class="text-dark-500 mt-2">Manage user accounts</p>
          </a>
        </div>
      </div>
    </div>
  `
})
export class AdminDashboardPageComponent implements OnInit {
  stats = signal({ users: 0, sellers: 0, orders: 0, revenue: 0 });
  loading = signal(true);

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any>(`${environment.apiUrl}${environment.apiPrefix}/dashboard`).subscribe({
      next: (data) => {
        this.stats.set({
          users: data.totalClients || 0,
          sellers: data.totalVendeurs || 0,
          orders: data.totalCommandes || 0,
          revenue: data.chiffreAffairesGlobal || 0
        });
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }
}
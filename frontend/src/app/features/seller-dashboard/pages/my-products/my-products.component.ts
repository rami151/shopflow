import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { environment } from '../../../../../environments/environment';

@Component({
  selector: 'app-my-products',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-2xl font-bold text-dark-900">My Products</h1>
          <button class="btn-primary py-2 px-4">Add Product</button>
        </div>
        <div class="bg-white rounded-lg shadow overflow-hidden">
          @if (loading()) {
            <div class="p-8 animate-pulse">
              <div class="h-16 bg-dark-100 rounded"></div>
            </div>
          } @else if (products().length === 0) {
            <div class="p-12 text-center">
              <p class="text-dark-500">No products yet.</p>
            </div>
          } @else {
            <div class="divide-y divide-dark-100">
              @for (product of products(); track product.id) {
                <div class="p-6 flex items-center justify-between">
                  <div class="flex items-center gap-4">
                    <div class="w-16 h-16 bg-dark-100 rounded-lg overflow-hidden">
                      <img [src]="product.imageUrl" [alt]="product.nom" class="w-full h-full object-cover" />
                    </div>
                    <div>
                      <p class="font-medium text-dark-900">{{ product.nom }}</p>
                      <p class="text-sm text-dark-500">{{ product.prix | currency:'TND' }} - Stock: {{ product.stock }}</p>
                    </div>
                  </div>
                  <span [class]="product.active ? 'text-green-600' : 'text-red-500'">{{ product.active ? 'Active' : 'Inactive' }}</span>
                </div>
              }
            </div>
          }
        </div>
      </div>
    </div>
  `
})
export class MyProductsComponent implements OnInit {
  products = signal<any[]>([]);
  loading = signal(true);

  constructor(private http: HttpClient) {}

  ngOnInit(): void {
    this.http.get<any[]>(`${environment.apiUrl}${environment.apiPrefix}/products?sellerId=me`).subscribe({
      next: (products) => { this.products.set(products); this.loading.set(false); },
      error: () => this.loading.set(false)
    });
  }
}
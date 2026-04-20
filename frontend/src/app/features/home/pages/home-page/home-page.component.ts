import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { ProductService, Product, Category } from '../../../products/services/product.service';

@Component({
  selector: 'app-home-page',
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <div>
      <!-- Hero Section -->
      <section class="relative bg-gradient-to-r from-primary-600 to-primary-800 text-white py-20 lg:py-32">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="max-w-2xl">
            <h1 class="text-4xl lg:text-6xl font-bold mb-6">Discover Amazing Products</h1>
            <p class="text-xl lg:text-2xl text-primary-100 mb-8">Shop the latest trends with unbeatable prices and fast delivery.</p>
            <div class="flex flex-col sm:flex-row gap-4">
              <a routerLink="/products" class="btn bg-white text-primary-600 hover:bg-primary-50 px-8 py-3 rounded-lg font-semibold text-center">Shop Now</a>
              <a routerLink="/products" class="btn bg-primary-700 hover:bg-primary-600 px-8 py-3 rounded-lg font-semibold text-center border border-primary-500">View Categories</a>
            </div>
          </div>
        </div>
      </section>

      <!-- Categories Section -->
      <section class="py-16 bg-white">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <h2 class="text-2xl font-bold text-dark-900 mb-8 text-center">Shop by Category</h2>
          <div class="grid grid-cols-2 md:grid-cols-4 gap-6">
            @for (category of categories(); track category.id) {
              <a [routerLink]="['/products']" [queryParams]="{categoryId: category.id}" class="group block text-center p-6 rounded-xl border border-dark-100 hover:border-primary-300 hover:shadow-lg transition duration-300">
                <div class="w-16 h-16 mx-auto mb-4 bg-primary-50 rounded-full flex items-center justify-center group-hover:bg-primary-100 transition">
                  <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10m-8 4l-8-4m8 4l8-4m-8 4v10m-8-4l8 4" />
                  </svg>
                </div>
                <h3 class="font-semibold text-dark-900">{{ category.nom }}</h3>
              </a>
            }
          </div>
        </div>
      </section>

      <!-- Featured Products -->
      <section class="py-16 bg-dark-50">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="flex items-center justify-between mb-8">
            <h2 class="text-2xl font-bold text-dark-900">Featured Products</h2>
            <a routerLink="/products" class="text-primary-600 hover:text-primary-700 font-medium">View All →</a>
          </div>

          @if (loading()) {
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              @for (i of [1,2,3,4]; track i) {
                <div class="bg-white rounded-lg shadow p-4 animate-pulse">
                  <div class="bg-dark-200 h-48 rounded-md mb-4"></div>
                  <div class="h-4 bg-dark-200 rounded w-3/4 mb-2"></div>
                  <div class="h-4 bg-dark-200 rounded w-1/2"></div>
                </div>
              }
            </div>
          } @else {
            <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
              @for (product of featuredProducts(); track product.id) {
                <a [routerLink]="['/products', product.id]" class="group bg-white rounded-lg shadow-sm hover:shadow-lg transition duration-300 overflow-hidden">
                  <div class="relative aspect-square bg-dark-100 overflow-hidden">
                    @if (product.imageUrl) {
                      <img [src]="product.imageUrl" [alt]="product.nom" (error)="onImageError($event)" class="w-full h-full object-cover group-hover:scale-105 transition duration-300" />
                    } @else {
                      <div class="w-full h-full flex items-center justify-center">
                        <svg class="w-16 h-16 text-dark-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10m-8 4l-8-4m8 4l8-4m-8 4v10m-8-4l8 4" />
                        </svg>
                      </div>
                    }
                  </div>
                  <div class="p-4">
                    <h3 class="font-semibold text-dark-900 group-hover:text-primary-600 transition">{{ product.nom }}</h3>
                    <p class="text-xl font-bold text-primary-600 mt-2">{{ product.prix | currency:'TND' }}</p>
                  </div>
                </a>
              }
            </div>
          }
        </div>
      </section>

      <!-- Features -->
      <section class="py-16 bg-white">
        <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div class="grid grid-cols-1 md:grid-cols-3 gap-8">
            <div class="text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-primary-100 rounded-full flex items-center justify-center">
                <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 8h14M5 8a2 2 0 110-4h14a2 2 0 110 4M5 8v10a2 2 0 002 2h10a2 2 0 002-2V8m-9 4h4" />
                </svg>
              </div>
              <h3 class="font-semibold text-dark-900 mb-2">Free Shipping</h3>
              <p class="text-dark-500">On orders over 50 TND</p>
            </div>
            <div class="text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-primary-100 rounded-full flex items-center justify-center">
                <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m5.618-4.016A11.955 11.955 0 0112 2.944a11.955 11.955 0 01-8.618 3.04A12.02 12.02 0 003 9c0 5.591 3.824 10.29 9 11.622 5.176-1.332 9-6.03 9-11.622 0-1.042-.133-2.052-.382-3.016z" />
                </svg>
              </div>
              <h3 class="font-semibold text-dark-900 mb-2">Secure Payment</h3>
              <p class="text-dark-500">100% secure checkout</p>
            </div>
            <div class="text-center">
              <div class="w-16 h-16 mx-auto mb-4 bg-primary-100 rounded-full flex items-center justify-center">
                <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                </svg>
              </div>
              <h3 class="font-semibold text-dark-900 mb-2">Easy Returns</h3>
              <p class="text-dark-500">30-day return policy</p>
            </div>
          </div>
        </div>
      </section>
    </div>
  `,
  styles: []
})
export class HomePageComponent implements OnInit {
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  featuredProducts = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal(true);

  constructor(private productService: ProductService) {}

  ngOnInit(): void {
    this.loadData();
  }

  private loadData(): void {
    this.productService.getTopSelling(8).subscribe({
      next: (products) => {
        this.featuredProducts.set(products);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });

    this.productService.getCategories().subscribe({
      next: (categories) => this.categories.set(categories.slice(0, 8)),
      error: () => {
        this.categories.set([
          { id: 1, nom: 'Electronics' },
          { id: 2, nom: 'Fashion' },
          { id: 3, nom: 'Home' },
          { id: 4, nom: 'Beauty' }
        ]);
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
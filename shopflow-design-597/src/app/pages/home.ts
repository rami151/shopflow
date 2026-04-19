import { Component, signal } from "@angular/core";
import { CommonModule } from "@angular/common";
import { RouterLink } from "@angular/router";

interface Product {
  id: number;
  name: string;
  price: number;
  originalPrice?: number;
  image: string;
  rating: number;
  reviews: number;
  badge?: string;
}

interface Category {
  id: number;
  name: string;
  icon: string;
  color: string;
  count: number;
}

@Component({
  selector: "app-home",
  standalone: true,
  imports: [CommonModule, RouterLink],
  template: `
    <!-- Hero Section -->
    <section class="relative bg-gradient-to-r from-primary-600 via-primary-500 to-primary-700 overflow-hidden">
      <div class="absolute inset-0 opacity-10">
        <div class="absolute top-20 right-20 w-72 h-72 bg-white rounded-full blur-3xl"></div>
        <div class="absolute bottom-0 left-0 w-96 h-96 bg-primary-300 rounded-full blur-3xl"></div>
      </div>

      <div class="relative max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 md:py-32">
        <div class="grid grid-cols-1 md:grid-cols-2 gap-12 items-center">
          <!-- Left Content -->
          <div class="space-y-8 animate-slide-up">
            <div>
              <h1 class="text-5xl md:text-6xl font-bold text-white mb-4 leading-tight">
                Shop with <span class="text-accent-400">Confidence</span>
              </h1>
              <p class="text-xl text-primary-100">Discover millions of products from trusted sellers. Fast shipping, secure payments, and 30-day returns.</p>
            </div>

            <div class="flex flex-col sm:flex-row gap-4">
              <button routerLink="/products" class="px-8 py-4 bg-accent-500 hover:bg-accent-600 text-white font-semibold rounded-lg transition transform hover:scale-105 shadow-lg">
                Start Shopping
              </button>
              <button class="px-8 py-4 bg-white/10 hover:bg-white/20 text-white font-semibold rounded-lg backdrop-blur transition border border-white/20">
                Learn More
              </button>
            </div>

            <!-- Trust Badges -->
            <div class="flex items-center gap-8 text-white text-sm">
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-accent-400" fill="currentColor" viewBox="0 0 20 20">
                  <path fill-rule="evenodd" d="M6.267 3.455a3.066 3.066 0 001.745-.723 3.066 3.066 0 013.976 0 3.066 3.066 0 001.745.723 3.066 3.066 0 012.812 2.812c.051.643.304 1.254.723 1.745a3.066 3.066 0 010 3.976 3.066 3.066 0 00-.723 1.745 3.066 3.066 0 01-2.812 2.812 3.066 3.066 0 00-1.745.723 3.066 3.066 0 01-3.976 0 3.066 3.066 0 00-1.745-.723 3.066 3.066 0 01-2.812-2.812 3.066 3.066 0 00-.723-1.745 3.066 3.066 0 010-3.976 3.066 3.066 0 00.723-1.745 3.066 3.066 0 012.812-2.812zm7.44 5.252a1 1 0 00-1.414-1.414L9 10.586 7.707 9.293a1 1 0 00-1.414 1.414l2 2a1 1 0 001.414 0l4-4z"/>
                </svg>
                Verified Sellers
              </div>
              <div class="flex items-center gap-2">
                <svg class="w-5 h-5 text-accent-400" fill="currentColor" viewBox="0 0 20 20">
                  <path d="M3 1a1 1 0 000 2h1.22l.305 1.222a.997.997 0 00.01.042l1.358 5.43-.893.892C3.74 11.846 4.632 14 6.414 14H15a1 1 0 000-2H6.414l1-1H14a1 1 0 00.894-.553l3-6A1 1 0 0017 6H6.28l-.31-1.243A1 1 0 005 4H3z"/>
                  <path d="M16 16a2 2 0 11-4 0 2 2 0 014 0z"/>
                  <path d="M4 12a2 2 0 11-4 0 2 2 0 014 0z"/>
                </svg>
                Fast Delivery
              </div>
            </div>
          </div>

          <!-- Right Image -->
          <div class="hidden md:block relative h-96">
            <div class="absolute inset-0 bg-gradient-to-br from-white/10 to-transparent rounded-2xl border border-white/20 backdrop-blur-sm flex items-center justify-center">
              <svg class="w-64 h-64 text-white/30" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="0.5" d="M16 11V7a4 4 0 00-8 0v4M5 9h14l1 12H4L5 9z" />
              </svg>
            </div>
          </div>
        </div>
      </div>
    </section>

    <!-- Categories Section -->
    <section class="py-16 md:py-24 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="mb-12">
          <h2 class="text-4xl font-bold text-dark-900 mb-4">Shop by Category</h2>
          <p class="text-lg text-dark-600">Browse our curated collections of products</p>
        </div>

        <div class="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-6 gap-4">
          <ng-container *ngFor="let category of categories()">
            <button 
              class="group p-6 rounded-xl border-2 transition-all duration-300 hover:shadow-lg"
              [ngClass]="category.color"
            >
              <div class="text-4xl mb-3 group-hover:scale-110 transition-transform">{{ category.icon }}</div>
              <h3 class="font-semibold text-dark-900 mb-1">{{ category.name }}</h3>
              <p class="text-xs text-dark-600">{{ category.count }}+ items</p>
            </button>
          </ng-container>
        </div>
      </div>
    </section>

    <!-- Featured Products Section -->
    <section class="py-16 md:py-24 bg-dark-50">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex items-center justify-between mb-12">
          <div>
            <h2 class="text-4xl font-bold text-dark-900 mb-4">Trending Now</h2>
            <p class="text-lg text-dark-600">Most popular products this week</p>
          </div>
          <a routerLink="/products" class="text-primary-600 hover:text-primary-700 font-semibold flex items-center gap-2 transition">
            View All
            <svg class="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 5l7 7-7 7" />
            </svg>
          </a>
        </div>

        <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-6">
          <ng-container *ngFor="let product of featuredProducts()">
            <div class="group bg-white rounded-xl overflow-hidden border border-dark-100 hover:border-primary-300 hover:shadow-lg transition-all duration-300">
              <!-- Product Image -->
              <div class="relative overflow-hidden bg-dark-50 h-56">
                <div class="w-full h-full flex items-center justify-center text-dark-300 group-hover:scale-105 transition-transform duration-300">
                  <svg class="w-24 h-24" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="1.5" d="M4 16l4.586-4.586a2 2 0 012.828 0L16 16m-2-2l1.586-1.586a2 2 0 012.828 0L20 14m-6-6h.01M6 20h12a2 2 0 002-2V6a2 2 0 00-2-2H6a2 2 0 00-2 2v12a2 2 0 002 2z" />
                  </svg>
                </div>
                <ng-container *ngIf="product.badge">
                  <div class="absolute top-3 right-3 bg-accent-500 text-white text-xs font-bold px-3 py-1 rounded-full">
                    {{ product.badge }}
                  </div>
                </ng-container>
                <button class="absolute bottom-3 right-3 w-10 h-10 bg-white rounded-full flex items-center justify-center shadow-md hover:bg-primary-50 transition opacity-0 group-hover:opacity-100">
                  <svg class="w-5 h-5 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                    <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M4.318 6.318a4.5 4.5 0 000 6.364L12 20.364l7.682-7.682a4.5 4.5 0 00-6.364-6.364L12 7.636l-1.318-1.318a4.5 4.5 0 00-6.364 0z" />
                  </svg>
                </button>
              </div>

              <!-- Product Info -->
              <div class="p-4">
                <h3 class="font-semibold text-dark-900 mb-2 line-clamp-2 group-hover:text-primary-600 transition">
                  {{ product.name }}
                </h3>

                <!-- Rating -->
                <div class="flex items-center gap-2 mb-3">
                  <div class="flex items-center gap-1">
                    <ng-container *ngFor="let i of [1,2,3,4,5]">
                      <svg class="w-4 h-4" [ngClass]="i <= product.rating ? 'text-accent-400' : 'text-dark-200'" fill="currentColor" viewBox="0 0 20 20">
                        <path d="M9.049 2.927c.3-.921 1.603-.921 1.902 0l1.07 3.292a1 1 0 00.95.69h3.462c.969 0 1.371 1.24.588 1.81l-2.8 2.034a1 1 0 00-.364 1.118l1.07 3.292c.3.921-.755 1.688-1.54 1.118l-2.8-2.034a1 1 0 00-1.175 0l-2.8 2.034c-.784.57-1.838-.197-1.539-1.118l1.07-3.292a1 1 0 00-.364-1.118L2.98 8.72c-.783-.57-.38-1.81.588-1.81h3.461a1 1 0 00.951-.69l1.07-3.292z" />
                      </svg>
                    </ng-container>
                  </div>
                  <span class="text-sm text-dark-600">({{ product.reviews }})</span>
                </div>

                <!-- Price -->
                <div class="flex items-baseline gap-2 mb-4">
                  <span class="text-2xl font-bold text-primary-600">{{ '$' + product.price }}</span>
                  <ng-container *ngIf="product.originalPrice">
                    <span class="text-sm text-dark-500 line-through">{{ '$' + product.originalPrice }}</span>
                    <span class="text-xs font-semibold text-accent-500">{{ Math.round((1 - product.price / product.originalPrice!) * 100) }}% off</span>
                  </ng-container>
                </div>

                <!-- Add to Cart Button -->
                <button class="w-full py-2 bg-primary-50 text-primary-600 font-semibold rounded-lg hover:bg-primary-100 transition">
                  Add to Cart
                </button>
              </div>
            </div>
          </ng-container>
        </div>
      </div>
    </section>

    <!-- Promotional Banner -->
    <section class="py-16 md:py-20 bg-gradient-to-r from-dark-900 via-dark-800 to-dark-900">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="bg-gradient-to-r from-accent-400 to-accent-600 rounded-2xl p-8 md:p-16 text-center relative overflow-hidden">
          <div class="absolute inset-0 opacity-10">
            <div class="absolute top-0 right-0 w-96 h-96 bg-white rounded-full blur-3xl"></div>
          </div>
          <div class="relative">
            <h2 class="text-4xl md:text-5xl font-bold text-white mb-4">Summer Sale</h2>
            <p class="text-xl text-white/90 mb-8 max-w-2xl mx-auto">Get up to 50% off on selected items. Limited time offer!</p>
            <button class="px-10 py-4 bg-white text-accent-600 font-bold rounded-lg hover:bg-dark-50 transition transform hover:scale-105 shadow-lg">
              Shop Now
            </button>
          </div>
        </div>
      </div>
    </section>

    <!-- Why Choose Us -->
    <section class="py-16 md:py-24 bg-white">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <h2 class="text-4xl font-bold text-dark-900 text-center mb-4">Why Choose ShopFlow</h2>
        <p class="text-lg text-dark-600 text-center mb-12 max-w-2xl mx-auto">We're committed to providing the best online shopping experience</p>

        <div class="grid grid-cols-1 md:grid-cols-4 gap-8">
          <!-- Feature 1 -->
          <div class="text-center">
            <div class="w-16 h-16 bg-primary-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-primary-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13 10V3L4 14h7v7l9-11h-7z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Lightning Fast</h3>
            <p class="text-dark-600">Experience super-fast checkout and instant order confirmation</p>
          </div>

          <!-- Feature 2 -->
          <div class="text-center">
            <div class="w-16 h-16 bg-accent-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-accent-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M9 12l2 2 4-4m7 0a9 9 0 11-18 0 9 9 0 0118 0z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Secure & Safe</h3>
            <p class="text-dark-600">Your payments are protected with industry-leading encryption</p>
          </div>

          <!-- Feature 3 -->
          <div class="text-center">
            <div class="w-16 h-16 bg-green-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-green-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Easy Returns</h3>
            <p class="text-dark-600">30-day money-back guarantee on all purchases</p>
          </div>

          <!-- Feature 4 -->
          <div class="text-center">
            <div class="w-16 h-16 bg-blue-100 rounded-full flex items-center justify-center mx-auto mb-4">
              <svg class="w-8 h-8 text-blue-600" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M18.364 5.636l-3.536 3.536m0 5.656l3.536 3.536M9.172 9.172L5.636 5.636m3.536 9.192l-3.536 3.536M21 12a9 9 0 11-18 0 9 9 0 0118 0zm-5-4a2 2 0 11-4 0 2 2 0 014 0z" />
              </svg>
            </div>
            <h3 class="text-lg font-semibold text-dark-900 mb-2">24/7 Support</h3>
            <p class="text-dark-600">Our customer service team is always ready to help</p>
          </div>
        </div>
      </div>
    </section>

    <!-- Newsletter Section -->
    <section class="py-16 md:py-20 bg-dark-50">
      <div class="max-w-2xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
        <h2 class="text-4xl font-bold text-dark-900 mb-4">Stay Updated</h2>
        <p class="text-lg text-dark-600 mb-8">Subscribe to our newsletter and get exclusive deals delivered to your inbox</p>
        <div class="flex flex-col sm:flex-row gap-3">
          <input 
            type="email" 
            placeholder="Enter your email" 
            class="flex-1 px-4 py-3 rounded-lg border border-dark-200 focus:border-primary-500 focus:outline-none focus:ring-2 focus:ring-primary-200"
          />
          <button class="px-8 py-3 bg-primary-600 hover:bg-primary-700 text-white font-semibold rounded-lg transition">
            Subscribe
          </button>
        </div>
      </div>
    </section>
  `,
  styles: [],
})
export class HomeComponent {
  Math = Math;

  categories = signal<Category[]>([
    { id: 1, name: "Electronics", icon: "📱", color: "border-blue-200 hover:bg-blue-50", count: 5420 },
    { id: 2, name: "Fashion", icon: "👕", color: "border-pink-200 hover:bg-pink-50", count: 8930 },
    { id: 3, name: "Home", icon: "🛋️", color: "border-orange-200 hover:bg-orange-50", count: 3210 },
    { id: 4, name: "Sports", icon: "⚽", color: "border-green-200 hover:bg-green-50", count: 2150 },
    { id: 5, name: "Beauty", icon: "💄", color: "border-red-200 hover:bg-red-50", count: 4520 },
    { id: 6, name: "Books", icon: "📚", color: "border-purple-200 hover:bg-purple-50", count: 6890 },
  ]);

  featuredProducts = signal<Product[]>([
    {
      id: 1,
      name: "Premium Wireless Headphones",
      price: 129.99,
      originalPrice: 199.99,
      image: "product1.jpg",
      rating: 5,
      reviews: 342,
      badge: "Best Seller",
    },
    {
      id: 2,
      name: "Ultra Soft Cotton T-Shirt",
      price: 24.99,
      image: "product2.jpg",
      rating: 4,
      reviews: 156,
      badge: "New",
    },
    {
      id: 3,
      name: "Modern LED Desk Lamp",
      price: 45.50,
      originalPrice: 69.99,
      image: "product3.jpg",
      rating: 5,
      reviews: 289,
    },
    {
      id: 4,
      name: "Professional Yoga Mat",
      price: 35.99,
      image: "product4.jpg",
      rating: 4,
      reviews: 178,
      badge: "Top Rated",
    },
  ]);
}

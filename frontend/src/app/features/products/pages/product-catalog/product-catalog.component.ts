import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService, Product, ProductsResponse, Category, ProductFilters } from '../../services/product.service';
import { RatingStarsComponent } from '../../../../shared/components/rating-stars/rating-stars.component';

@Component({
  selector: 'app-product-catalog',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, RatingStarsComponent],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <!-- Search & Filters -->
        <div class="mb-8">
          <div class="flex flex-col md:flex-row gap-4">
            <div class="flex-1">
              <input
                type="text"
                [(ngModel)]="filters.search"
                (ngModelChange)="onSearch()"
                placeholder="Search products..."
                class="input-field"
              />
            </div>
          </div>
        </div>

        <div class="flex flex-col lg:flex-row gap-8">
          <!-- Filters Sidebar -->
          <aside class="w-full lg:w-64 flex-shrink-0">
            <div class="bg-white rounded-lg shadow p-6">
              <h3 class="font-semibold text-dark-900 mb-4">Categories</h3>
              <div class="space-y-2">
                <label class="flex items-center">
                  <input type="radio" name="category" [(ngModel)]="filters.categoryId" [value]="undefined" (ngModelChange)="onFiltersChanged()" class="text-primary-600" />
                  <span class="ml-2 text-dark-600">All</span>
                </label>
                @for (category of categories(); track category.id) {
                  <label class="flex items-center">
                    <input type="radio" name="category" [(ngModel)]="filters.categoryId" [value]="category.id" (ngModelChange)="onFiltersChanged()" class="text-primary-600" />
                    <span class="ml-2 text-dark-600">{{ category.nom }}</span>
                  </label>
                }
              </div>

              <h3 class="font-semibold text-dark-900 mb-4 mt-6">Price Range</h3>
              <div class="flex items-center gap-2">
                <input
                  type="number"
                  [(ngModel)]="filters.minPrice"
                  (ngModelChange)="onFiltersChanged()"
                  placeholder="Min"
                  class="input-field"
                />
                <span class="text-dark-400">-</span>
                <input
                  type="number"
                  [(ngModel)]="filters.maxPrice"
                  (ngModelChange)="onFiltersChanged()"
                  placeholder="Max"
                  class="input-field"
                />
              </div>
            </div>
          </aside>

          <!-- Products Grid -->
          <div class="flex-1">
            @if (loading()) {
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                @for (i of [1,2,3,4,5,6]; track i) {
                  <div class="bg-white rounded-lg shadow p-4 animate-pulse">
                    <div class="bg-dark-200 h-64 rounded-md mb-4"></div>
                    <div class="h-4 bg-dark-200 rounded w-3/4 mb-2"></div>
                    <div class="h-4 bg-dark-200 rounded w-1/2"></div>
                  </div>
                }
              </div>
            } @else if (products().length === 0) {
              <div class="bg-white rounded-lg shadow p-12 text-center">
                <svg class="w-16 h-16 mx-auto text-dark-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                  <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10m-8 4l-8-4m8 4l8-4m-8 4v10m-8-4l8 4" />
                </svg>
                <h3 class="text-lg font-semibold text-dark-900 mb-2">No products found</h3>
                <p class="text-dark-500">Try adjusting your filters</p>
              </div>
            } @else {
              <div class="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-3 gap-6">
                @for (product of products(); track product.id) {
                  <div class="bg-white rounded-lg shadow-sm hover:shadow-lg transition duration-300 overflow-hidden group">
                    <a [routerLink]="['/products', product.id]" class="block">
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
                        @if (product.rating) {
                          <div class="mt-1">
                            <app-rating-stars [rating]="product.rating" [reviewCount]="product.reviewCount" [showCount]="true" />
                          </div>
                        }
                        <div class="flex items-center justify-between mt-2">
                          <span class="text-xl font-bold text-primary-600">{{ product.prix | currency:'TND' }}</span>
                          @if (product.stock > 0) {
                            <span class="text-xs text-green-600">{{ product.stock }} in stock</span>
                          } @else {
                            <span class="text-xs text-red-500">Out of stock</span>
                          }
                        </div>
                      </div>
                    </a>
                  </div>
                }
              </div>

              <!-- Pagination -->
              @if (totalPages() > 1) {
                <div class="mt-8 flex justify-center gap-2">
                  <button
                    (click)="goToPage(currentPage() - 1)"
                    [disabled]="currentPage() === 0"
                    class="px-4 py-2 rounded-lg border border-dark-200 hover:bg-dark-50 disabled:opacity-50"
                  >
                    Previous
                  </button>
                  @for (page of [].constructor(totalPages()); track $index) {
                    <button
                      (click)="goToPage($index)"
                      [class]="[$index === currentPage() ? 'bg-primary-600 text-white' : 'border border-dark-200 hover:bg-dark-50']"
                      class="px-4 py-2 rounded-lg"
                    >
                      {{ $index + 1 }}
                    </button>
                  }
                  <button
                    (click)="goToPage(currentPage() + 1)"
                    [disabled]="currentPage() === totalPages() - 1"
                    class="px-4 py-2 rounded-lg border border-dark-200 hover:bg-dark-50 disabled:opacity-50"
                  >
                    Next
                  </button>
                </div>
              }
            }
          </div>
        </div>
      </div>
    </div>
  `,
  styles: []
})
export class ProductCatalogComponent implements OnInit {
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  products = signal<Product[]>([]);
  categories = signal<Category[]>([]);
  loading = signal(true);
  currentPage = signal(0);
  totalPages = signal(0);

  filters: ProductFilters = {
    page: 0,
    size: 12
  };

  constructor(
    private productService: ProductService,
    private route: ActivatedRoute
  ) {}

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      if (params['categoryId']) {
        const categoryId = Number(params['categoryId']);
        this.filters.categoryId = Number.isNaN(categoryId) ? undefined : categoryId;
      }
    });

    this.loadCategories();
    this.loadProducts();
  }

  private loadCategories(): void {
    this.productService.getCategories().subscribe({
      next: (categories) => {
        this.categories.set(categories);
        // Backward compatibility for old links using category name.
        const categoryName = this.route.snapshot.queryParamMap.get('category');
        if (categoryName && this.filters.categoryId === undefined) {
          const matched = categories.find(c => c.nom === categoryName);
          if (matched) {
            this.filters.categoryId = matched.id;
            this.loadProducts();
          }
        }
      }
    });
  }

  loadProducts(): void {
    this.loading.set(true);
    this.productService.getProducts(this.filters).subscribe({
      next: (response) => {
        this.products.set(response.content);
        this.currentPage.set(response.number);
        this.totalPages.set(response.totalPages);
        this.loading.set(false);
      },
      error: () => this.loading.set(false)
    });
  }

  onSearch(): void {
    this.filters.page = 0;
    this.loadProducts();
  }

  onFiltersChanged(): void {
    this.filters.page = 0;
    this.loadProducts();
  }

  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages()) {
      this.filters.page = page;
      this.loadProducts();
    }
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement | null;
    if (img && img.src !== this.fallbackImage) {
      img.src = this.fallbackImage;
    }
  }
}
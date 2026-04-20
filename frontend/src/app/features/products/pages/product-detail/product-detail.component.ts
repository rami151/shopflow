import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, ActivatedRoute } from '@angular/router';
import { FormsModule } from '@angular/forms';
import { ProductService, Product, ProductVariant } from '../../services/product.service';
import { CartService } from '../../../cart/services/cart.service';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';
import { ReviewService, Review } from '../../../reviews/services/review.service';
import { RatingStarsComponent } from '../../../../shared/components/rating-stars/rating-stars.component';

@Component({
  selector: 'app-product-detail',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule, RatingStarsComponent],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        @if (loading()) {
          <div class="bg-white rounded-lg shadow p-8 animate-pulse">
            <div class="flex flex-col lg:flex-row gap-8">
              <div class="lg:w-1/2">
                <div class="bg-dark-200 aspect-square rounded-lg"></div>
              </div>
              <div class="lg:w-1/2">
                <div class="h-8 bg-dark-200 rounded w-3/4 mb-4"></div>
                <div class="h-4 bg-dark-200 rounded w-1/2"></div>
              </div>
            </div>
          </div>
        } @else if (product()) {
          <div class="bg-white rounded-lg shadow p-8">
            <div class="flex flex-col lg:flex-row gap-8">
              <!-- Images -->
              <div class="lg:w-1/2">
                <div class="aspect-square bg-dark-100 rounded-lg overflow-hidden mb-4">
                  @if (selectedImage()) {
                    <img [src]="selectedImage()" [alt]="product()!.nom" (error)="onImageError($event)" class="w-full h-full object-cover" />
                  } @else {
                    <div class="w-full h-full flex items-center justify-center">
                      <svg class="w-24 h-24 text-dark-300" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                        <path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M20 7l-8-4-8 4m16 0l-8 4m8-4v10m-8 4l-8-4m8 4l8-4m-8 4v10m-8-4l8 4" />
                      </svg>
                    </div>
                  }
                </div>
                @if (product()!.images && product()!.images!.length > 1) {
                  <div class="flex gap-2">
                    @for (image of product()!.images; track image) {
                      <button
                        (click)="selectedImage.set(image)"
                        [class]="['w-20 h-20 rounded-md overflow-hidden border-2', selectedImage() === image ? 'border-primary-600' : 'border-transparent']"
                      >
                        <img [src]="image" [alt]="product()!.nom" (error)="onImageError($event)" class="w-full h-full object-cover" />
                      </button>
                    }
                  </div>
                }
              </div>

              <!-- Product Info -->
              <div class="lg:w-1/2">
                <h1 class="text-3xl font-bold text-dark-900">{{ product()!.nom }}</h1>

                @if (product()!.averageRating) {
                  <div class="mt-2">
                    <app-rating-stars [rating]="product()!.averageRating!" [reviewCount]="product()!.reviewCount" [showCount]="true" />
                  </div>
                }

                <div class="mt-4">
                  <span class="text-3xl font-bold text-primary-600">{{ selectedVariant()?.prix || product()!.prix | currency:'TND' }}</span>
                </div>

                @if (product()!.variants && product()!.variants!.length > 0) {
                  <div class="mt-6">
                    <h3 class="text-sm font-medium text-dark-700 mb-2">Select Variant</h3>
                    <div class="flex flex-wrap gap-2">
                      @for (variant of product()!.variants; track variant.id) {
                        <button
                          (click)="selectedVariant.set(variant)"
                          [class]="['px-4 py-2 rounded-lg border', selectedVariant()?.id === variant.id ? 'border-primary-600 bg-primary-50 text-primary-600' : 'border-dark-200 hover:border-dark-300']"
                        >
                          {{ variant.nom }}
                        </button>
                      }
                    </div>
                  </div>
                }

                <div class="mt-6">
                  <h3 class="text-sm font-medium text-dark-700 mb-2">Quantity</h3>
                  <div class="flex items-center gap-4">
                    <button
                      (click)="decrementQuantity()"
                      class="w-10 h-10 rounded-lg border border-dark-200 flex items-center justify-center hover:bg-dark-50"
                    >
                      -
                    </button>
                    <span class="text-xl font-semibold w-8 text-center">{{ quantity() }}</span>
                    <button
                      (click)="incrementQuantity()"
                      class="w-10 h-10 rounded-lg border border-dark-200 flex items-center justify-center hover:bg-dark-50"
                    >
                      +
                    </button>
                  </div>
                </div>

                <div class="mt-4">
                  @if ((selectedVariant()?.stock ?? product()!.stock) > 0) {
                    <p class="text-sm" [class]="(selectedVariant()?.stock ?? product()!.stock) > 10 ? 'text-green-600' : 'text-orange-600'">
                      {{ selectedVariant()?.stock ?? product()!.stock }} available in stock
                    </p>
                  } @else {
                    <p class="text-sm text-red-500">Out of stock</p>
                  }
                </div>

                <div class="mt-6 flex gap-4">
                  @if (isAuthenticated && (selectedVariant()?.stock || product()!.stock > 0)) {
                    <button
                      (click)="addToCart()"
                      [disabled]="addingToCart()"
                      class="flex-1 btn-primary py-3"
                    >
                      @if (addingToCart()) {
                        <svg class="w-5 h-5 animate-spin mr-2 inline" fill="none" viewBox="0 0 24 24">
                          <circle class="opacity-25" cx="12" cy="12" r="10" stroke="currentColor" stroke-width="4"></circle>
                          <path class="opacity-75" fill="currentColor" d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"></path>
                        </svg>
                      }
                      Add to Cart
                    </button>
                  } @else if (!isAuthenticated) {
                    <a routerLink="/auth/login" class="flex-1 btn-primary py-3 text-center">Login to Buy</a>
                  } @else {
                    <button disabled class="flex-1 btn-primary py-3 opacity-50 cursor-not-allowed">Out of Stock</button>
                  }
                </div>
              </div>
            </div>

            <!-- Description -->
            @if (product()!.description) {
              <div class="mt-12 border-t border-dark-100 pt-8">
                <h2 class="text-xl font-semibold text-dark-900 mb-4">Description</h2>
                <p class="text-dark-600">{{ product()!.description }}</p>
              </div>
            }

            <!-- Reviews -->
            <div class="mt-12 border-t border-dark-100 pt-8">
              <div class="flex items-center justify-between gap-4 mb-4">
                <h2 class="text-xl font-semibold text-dark-900">Reviews</h2>
                @if (reviewStats().nombreAvis > 0) {
                  <div class="text-sm text-dark-600">
                    Average: <span class="font-semibold text-dark-900">{{ reviewStats().noteMoyenne ?? 0 }}</span>
                    ({{ reviewStats().nombreAvis }} reviews)
                  </div>
                }
              </div>

              @if (reviewsLoading()) {
                <div class="text-dark-500">Loading reviews…</div>
              } @else if (reviewsError()) {
                <div class="bg-red-50 border border-red-200 text-red-700 rounded-lg p-4">
                  {{ reviewsError() }}
                </div>
              } @else if (reviews().length === 0) {
                <p class="text-dark-500">No reviews yet.</p>
              } @else {
                <div class="space-y-4">
                  @for (review of reviews(); track review.id) {
                    <div class="bg-dark-50 rounded-lg p-4">
                      <div class="flex items-center justify-between">
                        <div class="font-medium text-dark-900">{{ review.userNom }}</div>
                        <div class="text-sm text-dark-500">{{ review.dateCreation | date:'short' }}</div>
                      </div>
                      <div class="mt-1 text-sm text-dark-700">Rating: <span class="font-semibold">{{ review.note }}/5</span></div>
                      @if (review.commentaire) {
                        <p class="mt-2 text-dark-700">{{ review.commentaire }}</p>
                      }
                    </div>
                  }
                </div>
              }
            </div>
          </div>
        } @else {
          <div class="bg-white rounded-lg shadow p-12 text-center">
            <h3 class="text-lg font-semibold text-dark-900 mb-2">Product not found</h3>
            <a routerLink="/products" class="text-primary-600 hover:text-primary-700">Back to products</a>
          </div>
        }
      </div>
    </div>
  `,
  styles: []
})
export class ProductDetailComponent implements OnInit {
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  product = signal<Product | null>(null);
  selectedImage = signal<string | null>(null);
  selectedVariant = signal<ProductVariant | null>(null);
  quantity = signal(1);
  loading = signal(true);
  addingToCart = signal(false);
  isAuthenticated = false;

  reviews = signal<Review[]>([]);
  reviewStats = signal<{ noteMoyenne: number | null; nombreAvis: number }>({ noteMoyenne: null, nombreAvis: 0 });
  reviewsLoading = signal(false);
  reviewsError = signal<string | null>(null);

  constructor(
    private route: ActivatedRoute,
    private productService: ProductService,
    private cartService: CartService,
    private authService: AuthService,
    private toastService: ToastService,
    private reviewService: ReviewService
  ) {}

  ngOnInit(): void {
    this.isAuthenticated = this.authService.isAuthenticated();

    const id = this.route.snapshot.paramMap.get('id');
    if (id) {
      this.loadProduct(+id);
    }
  }

  private loadProduct(id: number): void {
    this.productService.getProductById(id).subscribe({
      next: (product) => {
        this.product.set(product);
        if (product.images && product.images.length > 0) {
          this.selectedImage.set(product.images[0]);
        }
        this.loading.set(false);
        this.loadReviews(product.id);
      },
      error: () => {
        this.loading.set(false);
      }
    });
  }

  private loadReviews(productId: number): void {
    this.reviewsLoading.set(true);
    this.reviewsError.set(null);
    this.reviewService.getProductReviews(productId, 0, 10).subscribe({
      next: (res) => {
        this.reviews.set(res.avis ?? []);
        this.reviewStats.set({ noteMoyenne: res.noteMoyenne ?? null, nombreAvis: res.nombreAvis ?? 0 });
        this.reviewsLoading.set(false);
      },
      error: (err) => {
        this.reviewsError.set(err?.error?.message || 'Failed to load reviews');
        this.reviews.set([]);
        this.reviewStats.set({ noteMoyenne: null, nombreAvis: 0 });
        this.reviewsLoading.set(false);
      }
    });
  }

  incrementQuantity(): void {
    const max = this.selectedVariant()?.stock || this.product()?.stock || 1;
    if (this.quantity() < max) {
      this.quantity.update(q => q + 1);
    }
  }

  decrementQuantity(): void {
    if (this.quantity() > 1) {
      this.quantity.update(q => q - 1);
    }
  }

  addToCart(): void {
    const product = this.product();
    if (!product) return;

    this.addingToCart.set(true);
    this.cartService.addItem(product.id, this.quantity(), this.selectedVariant()?.id).subscribe({
      next: () => {
        this.toastService.success('Added to cart!');
        this.addingToCart.set(false);
      },
      error: () => {
        this.toastService.error('Failed to add to cart');
        this.addingToCart.set(false);
      }
    });
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement | null;
    if (img && img.src !== this.fallbackImage) {
      img.src = this.fallbackImage;
    }
    this.selectedImage.set(this.fallbackImage);
  }
}
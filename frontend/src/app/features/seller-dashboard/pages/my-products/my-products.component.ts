import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { HttpClient } from '@angular/common/http';
import { FormsModule } from '@angular/forms';
import { environment } from '../../../../../environments/environment';
import { AuthService } from '../../../../core/services/auth.service';
import { ToastService } from '../../../../core/services/toast.service';

@Component({
  selector: 'app-my-products',
  standalone: true,
  imports: [CommonModule, RouterLink, FormsModule],
  template: `
    <div class="min-h-screen bg-dark-50 py-8">
      <div class="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
        <div class="flex justify-between items-center mb-8">
          <h1 class="text-2xl font-bold text-dark-900">My Products</h1>
          <button (click)="showAddForm.set(true)" class="btn-primary py-2 px-4">Add Product</button>
        </div>

        @if (showAddForm()) {
          <div class="bg-white rounded-lg shadow p-6 mb-6">
            <h2 class="text-lg font-semibold text-dark-900 mb-4">Create New Product</h2>
            <form class="grid grid-cols-1 md:grid-cols-2 gap-4" (ngSubmit)="createProduct()">
              <div>
                <label class="block text-sm font-medium text-dark-700 mb-1">Name</label>
                <input
                  [(ngModel)]="newProduct.nom"
                  name="nom"
                  class="input-field"
                  placeholder="Product name"
                  required
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-dark-700 mb-1">Category</label>
                <select [(ngModel)]="newProduct.categoryId" name="categoryId" class="input-field">
                  <option [ngValue]="null">No category</option>
                  @for (category of categories(); track category.id) {
                    <option [ngValue]="category.id">{{ category.nom }}</option>
                  }
                </select>
              </div>

              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-dark-700 mb-1">Description</label>
                <textarea
                  [(ngModel)]="newProduct.description"
                  name="description"
                  class="input-field"
                  placeholder="Product description"
                  rows="3"
                  required
                ></textarea>
              </div>

              <div>
                <label class="block text-sm font-medium text-dark-700 mb-1">Price</label>
                <input
                  type="number"
                  min="0.01"
                  step="0.01"
                  [(ngModel)]="newProduct.prix"
                  name="prix"
                  class="input-field"
                  required
                />
              </div>

              <div>
                <label class="block text-sm font-medium text-dark-700 mb-1">Stock</label>
                <input
                  type="number"
                  min="0"
                  step="1"
                  [(ngModel)]="newProduct.stock"
                  name="stock"
                  class="input-field"
                  required
                />
              </div>

              <div class="md:col-span-2">
                <label class="block text-sm font-medium text-dark-700 mb-1">Image URL (optional)</label>
                <input
                  [(ngModel)]="newProduct.imageUrl"
                  name="imageUrl"
                  class="input-field"
                  placeholder="https://..."
                />
              </div>

              <div class="md:col-span-2 flex gap-3 pt-2">
                <button type="submit" [disabled]="saving()" class="btn-primary py-2 px-4">
                  {{ saving() ? 'Creating...' : 'Create Product' }}
                </button>
                <button type="button" (click)="cancelCreate()" class="btn-secondary py-2 px-4">
                  Cancel
                </button>
              </div>
            </form>
          </div>
        }

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
                      <img [src]="product.imageUrl" [alt]="product.nom" (error)="onImageError($event)" class="w-full h-full object-cover" />
                    </div>
                    <div>
                      <p class="font-medium text-dark-900">{{ product.nom }}</p>
                      <p class="text-sm text-dark-500">{{ product.prix | currency:'TND' }} - Stock: {{ product.stock }}</p>
                    </div>
                  </div>
                  <span [class]="product.actif ? 'text-green-600' : 'text-red-500'">{{ product.actif ? 'Active' : 'Inactive' }}</span>
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
  private readonly fallbackImage =
    'data:image/svg+xml;utf8,<svg xmlns="http://www.w3.org/2000/svg" width="400" height="400"><rect width="100%" height="100%" fill="%23f3f4f6"/><text x="50%" y="50%" dominant-baseline="middle" text-anchor="middle" fill="%239ca3af" font-family="Arial,sans-serif" font-size="24">No Image</text></svg>';

  products = signal<any[]>([]);
  categories = signal<Array<{ id: number; nom: string }>>([]);
  loading = signal(true);
  showAddForm = signal(false);
  saving = signal(false);

  newProduct: {
    nom: string;
    description: string;
    prix: number;
    stock: number;
    imageUrl: string;
    categoryId: number | null;
  } = this.getEmptyProductForm();

  constructor(
    private http: HttpClient,
    private authService: AuthService,
    private toastService: ToastService
  ) {}

  ngOnInit(): void {
    this.loadProducts();
    this.loadCategories();
  }

  private loadProducts(): void {
    this.loading.set(true);
    this.http
      .get<{ content: any[] }>(`${environment.apiUrl}${environment.apiPrefix}/products/mine`)
      .subscribe({
        next: (response) => {
          this.products.set(response?.content ?? []);
          this.loading.set(false);
        },
        error: () => {
          this.loading.set(false);
          this.toastService.error('Failed to load products');
        }
      });
  }

  private loadCategories(): void {
    this.http.get<Array<{ id: number; nom: string }>>(`${environment.apiUrl}${environment.apiPrefix}/categories`).subscribe({
      next: (categories) => this.categories.set(categories ?? []),
      error: () => this.categories.set([])
    });
  }

  createProduct(): void {
    if (!this.newProduct.nom || !this.newProduct.description || this.newProduct.prix <= 0 || this.newProduct.stock < 0) {
      this.toastService.error('Please fill all required fields correctly');
      return;
    }

    this.saving.set(true);
    const payload = {
      nom: this.newProduct.nom.trim(),
      description: this.newProduct.description.trim(),
      prix: this.newProduct.prix,
      stock: this.newProduct.stock,
      imageUrl: this.newProduct.imageUrl?.trim() || null,
      categoryIds: this.newProduct.categoryId ? [this.newProduct.categoryId] : []
    };

    this.http.post(`${environment.apiUrl}${environment.apiPrefix}/products`, payload).subscribe({
      next: () => {
        this.toastService.success('Product created successfully');
        this.saving.set(false);
        this.showAddForm.set(false);
        this.newProduct = this.getEmptyProductForm();
        this.loadProducts();
      },
      error: (err) => {
        this.saving.set(false);
        this.toastService.error(err?.error?.message || 'Failed to create product');
      }
    });
  }

  cancelCreate(): void {
    this.showAddForm.set(false);
    this.newProduct = this.getEmptyProductForm();
  }

  private getEmptyProductForm() {
    return {
      nom: '',
      description: '',
      prix: 0,
      stock: 0,
      imageUrl: '',
      categoryId: null
    };
  }

  onImageError(event: Event): void {
    const img = event.target as HTMLImageElement | null;
    if (img && img.src !== this.fallbackImage) {
      img.src = this.fallbackImage;
    }
  }
}
import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Product {
  id: number;
  nom: string;
  description?: string;
  prix: number;
  imageUrl?: string;
  images?: string[];
  stock: number;
  actif: boolean;
  sellerProfileId?: number;
  sellerName?: string;
  categories?: CategoryDto[];
  variants?: ProductVariant[];
  averageRating?: number;
  reviewCount?: number;
}

export interface ProductVariant {
  id: number;
  nom: string;
  valeur: string;
  prixSupplementaire: number;
  stockSupplementaire: number;
  prix?: number;
  stock?: number;
  actif: boolean;
}

export interface ProductsResponse {
  content: Product[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

export interface Category {
  id: number;
  nom: string;
  description?: string;
  parentId?: number;
  parentNom?: string;
  actif?: boolean;
}

export interface CategoryDto {
  id: number;
  nom: string;
  description?: string;
  parentId?: number;
  parentNom?: string;
  actif?: boolean;
}

export interface ProductFilters {
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: string;
  categoryId?: number;
  minPrice?: number;
  maxPrice?: number;
  sellerId?: number;
  search?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  constructor(private http: HttpClient) {}

  getProducts(filters: ProductFilters = {}): Observable<ProductsResponse> {
    if (filters.search && filters.search.trim().length > 0) {
      let searchParams = new HttpParams();
      searchParams = searchParams.set('q', filters.search.trim());
      if (filters.page !== undefined) searchParams = searchParams.set('page', filters.page.toString());
      if (filters.size) searchParams = searchParams.set('size', filters.size.toString());
      return this.http.get<ProductsResponse>(`${this.apiUrl}/products/search`, { params: searchParams });
    }

    let params = new HttpParams();
    if (filters.page !== undefined) params = params.set('page', filters.page.toString());
    if (filters.size) params = params.set('size', filters.size.toString());
    if (filters.sortBy) params = params.set('sortBy', filters.sortBy);
    if (filters.sortDir) params = params.set('sortDir', filters.sortDir);
    if (filters.categoryId !== undefined) params = params.set('categoryId', filters.categoryId.toString());
    if (filters.minPrice !== undefined && filters.minPrice !== null) params = params.set('minPrice', filters.minPrice.toString());
    if (filters.maxPrice !== undefined && filters.maxPrice !== null) params = params.set('maxPrice', filters.maxPrice.toString());
    if (filters.sellerId !== undefined) params = params.set('sellerId', filters.sellerId.toString());

    return this.http.get<ProductsResponse>(`${this.apiUrl}/products`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  getTopSelling(page: number = 0, size: number = 8): Observable<Product[]> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<Product[]>(`${this.apiUrl}/products/top-selling`, { params });
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  createProduct(payload: any): Observable<Product> {
    return this.http.post<Product>(`${this.apiUrl}/products`, payload);
  }

  updateProduct(id: number, payload: any): Observable<Product> {
    return this.http.put<Product>(`${this.apiUrl}/products/${id}`, payload);
  }

  deleteProduct(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/products/${id}`);
  }
}
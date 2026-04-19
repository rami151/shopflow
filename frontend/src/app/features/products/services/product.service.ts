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
  categoryId: number;
  categoryName?: string;
  stock: number;
  rating?: number;
  reviewCount?: number;
  variants?: ProductVariant[];
  active: boolean;
  createdAt?: string;
}

export interface ProductVariant {
  id: number;
  nom: string;
  prix: number;
  stock: number;
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
  imageUrl?: string;
  parentId?: number;
  children?: Category[];
}

export interface ProductFilters {
  page?: number;
  size?: number;
  category?: string;
  minPrice?: number;
  maxPrice?: number;
  minRating?: number;
  search?: string;
}

@Injectable({ providedIn: 'root' })
export class ProductService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  constructor(private http: HttpClient) {}

  getProducts(filters: ProductFilters = {}): Observable<ProductsResponse> {
    let params = new HttpParams();
    if (filters.page !== undefined) params = params.set('page', filters.page.toString());
    if (filters.size) params = params.set('size', filters.size.toString());
    if (filters.category) params = params.set('category', filters.category);
    if (filters.minPrice) params = params.set('minPrice', filters.minPrice.toString());
    if (filters.maxPrice) params = params.set('maxPrice', filters.maxPrice.toString());
    if (filters.minRating) params = params.set('minRating', filters.minRating.toString());
    if (filters.search) params = params.set('search', filters.search);

    return this.http.get<ProductsResponse>(`${this.apiUrl}/products`, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this.apiUrl}/products/${id}`);
  }

  getTopSelling(limit = 8): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/top-selling?limit=${limit}`);
  }

  getCategories(): Observable<Category[]> {
    return this.http.get<Category[]>(`${this.apiUrl}/categories`);
  }

  searchProducts(query: string): Observable<Product[]> {
    return this.http.get<Product[]>(`${this.apiUrl}/products/search?q=${query}`);
  }
}
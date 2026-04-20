import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Review {
  id: number;
  productId: number;
  productNom: string;
  userNom: string;
  note: number;
  commentaire: string;
  approuve: boolean;
  dateCreation: string;
}

export interface ProductReviewsResponse {
  avis: Review[];
  noteMoyenne: number | null;
  nombreAvis: number;
  page: number;
  totalPages: number;
  totalElements: number;
}

export interface CreateReviewRequest {
  productId: number;
  note: number;
  commentaire?: string | null;
}

@Injectable({ providedIn: 'root' })
export class ReviewService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  constructor(private http: HttpClient) {}

  createReview(payload: CreateReviewRequest): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/reviews`, payload);
  }

  getProductReviews(productId: number, page: number = 0, size: number = 10): Observable<ProductReviewsResponse> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get<ProductReviewsResponse>(`${this.apiUrl}/reviews/product/${productId}`, { params });
  }

  getPendingReviews(page: number = 0, size: number = 10): Observable<any> {
    const params = new HttpParams().set('page', page.toString()).set('size', size.toString());
    return this.http.get(`${this.apiUrl}/reviews/pending`, { params });
  }

  approve(reviewId: number): Observable<Review> {
    return this.http.post<Review>(`${this.apiUrl}/reviews/${reviewId}/approve`, {});
  }

  reject(reviewId: number): Observable<void> {
    return this.http.post<void>(`${this.apiUrl}/reviews/${reviewId}/reject`, {});
  }
}


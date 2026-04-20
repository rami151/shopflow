import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Address {
  id: number;
  rue: string;
  ville: string;
  codePostal: string;
  pays: string;
  principale: boolean;
}

export interface OrderItem {
  id: number;
  productId: number;
  productName: string;
  productImageUrl: string;
  variantNom: string;
  variantValeur: string;
  prixUnitaire: number;
  quantite: number;
  sousTotal: number;
}

export interface Order {
  id: number;
  numeroCommande: string;
  statut: OrderStatus;
  items: OrderItem[];
  subtotal: number;
  fraisLivraison: number;
  couponDiscount: number;
  couponCode: string;
  totalTTC: number;
  adresseLivraison: Address;
  dateCommande: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  PROCESSING = 'PROCESSING',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  constructor(private http: HttpClient) {}

  getAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.apiUrl}/orders/addresses`);
  }

  placeOrder(addressId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/orders`, { addressId });
  }

  getMyOrders(page: number = 0, size: number = 10): Observable<PageResponse<Order>> {
    let params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    return this.http.get<PageResponse<Order>>(`${this.apiUrl}/orders`, { params });
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/orders/${id}`);
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.delete<Order>(`${this.apiUrl}/orders/${id}`);
  }

  simulatePayment(orderId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/orders/${orderId}/pay`, {});
  }
}
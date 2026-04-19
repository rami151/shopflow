import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../../environments/environment';

export interface Address {
  id: number;
  nom: string;
  rue: string;
  ville: string;
  codePostal: string;
  pays: string;
  default: boolean;
}

export interface Order {
  id: number;
  orderNumber: string;
  status: OrderStatus;
  items: any[];
  subtotal: number;
  shipping: number;
  discount: number;
  total: number;
  address: Address;
  createdAt: string;
  estimatedDelivery?: string;
}

export enum OrderStatus {
  PENDING = 'PENDING',
  PAID = 'PAID',
  SHIPPED = 'SHIPPED',
  DELIVERED = 'DELIVERED',
  CANCELLED = 'CANCELLED'
}

@Injectable({ providedIn: 'root' })
export class OrderService {
  private apiUrl = `${environment.apiUrl}${environment.apiPrefix}`;

  constructor(private http: HttpClient) {}

  getAddresses(): Observable<Address[]> {
    return this.http.get<Address[]>(`${this.apiUrl}/addresses`);
  }

  placeOrder(addressId: number): Observable<Order> {
    return this.http.post<Order>(`${this.apiUrl}/orders`, { addressId });
  }

  getMyOrders(): Observable<Order[]> {
    return this.http.get<Order[]>(`${this.apiUrl}/orders/my`);
  }

  getOrderById(id: number): Observable<Order> {
    return this.http.get<Order>(`${this.apiUrl}/orders/${id}`);
  }

  cancelOrder(id: number): Observable<Order> {
    return this.http.put<Order>(`${this.apiUrl}/orders/${id}/cancel`, {});
  }
}
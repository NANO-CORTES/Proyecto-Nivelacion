import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface OrderItemRequest {
    productId: number;
    quantity: number;
}

export interface OrderRequest {
    customerName: string;
    userId: number;
    items: OrderItemRequest[];
}

export interface OrderItemResponse {
    id?: number;
    productId: number;
    quantity: number;
    price: number;
}

export interface OrderResponse {
    id: number;
    customerName: string;
    totalAmount: number;
    userId: number;
    items: OrderItemResponse[];
    status: string;
}

@Injectable({
    providedIn: 'root'
})
export class OrderService {

    private apiUrl = 'http://localhost:8080/orders';

    constructor(private http: HttpClient) { }

    getOrders(): Observable<OrderResponse[]> {
        return this.http.get<OrderResponse[]>(this.apiUrl);
    }

    createOrder(order: OrderRequest): Observable<OrderResponse> {
        return this.http.post<OrderResponse>(this.apiUrl, order);
    }

    cancelOrder(id: number): Observable<any> {
        return this.http.delete(`${this.apiUrl}/${id}`, { responseType: 'text' });
    }
}

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface LoginRequest {
    username?: string;
    password?: string;
}

export interface AuthResponse {
    token?: string;
}

export interface RegisterRequest {
    username?: string;
    email?: string;
    password?: string;
    role?: string;
}

@Injectable({
    providedIn: 'root'
})
export class AuthService {

    private apiUrl = 'http://localhost:8080/auth';

    constructor(private http: HttpClient) { }

    login(request: LoginRequest): Observable<AuthResponse> {
        return this.http.post<AuthResponse>(`${this.apiUrl}/login`, request);
    }

    register(request: RegisterRequest): Observable<any> {
        return this.http.post<any>(`${this.apiUrl}/register`, request);
    }

    saveToken(token: string): void {
        localStorage.setItem('auth_token', token);
    }

    getToken(): string | null {
        return localStorage.getItem('auth_token');
    }

    logout(): void {
        localStorage.removeItem('auth_token');
    }

    isLoggedIn(): boolean {
        return !!this.getToken();
    }

    getRole(): string {
        const token = this.getToken();
        if (!token) return 'USER';
        
        try {
            const payload = token.split('.')[1];
            if (!payload) return 'USER';
            
            // Base64Url decode to Base64
            const base64 = payload.replace(/-/g, '+').replace(/_/, '/');
            const decoded = JSON.parse(window.atob(base64));
            
            return decoded.role || 'USER';
        } catch (e) {
            console.error('Error parsing token payload:', e);
            return 'USER';
        }
    }

    getUsername(): string {
        const token = this.getToken();
        if (!token) return '';
        try {
            const payload = token.split('.')[1];
            if (!payload) return '';
            const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
            const decoded = JSON.parse(window.atob(base64));
            return decoded.sub || '';
        } catch (e) {
            return '';
        }
    }
}

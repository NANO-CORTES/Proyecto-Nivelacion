import { HttpInterceptorFn, HttpErrorResponse } from '@angular/common/http';
import { inject } from '@angular/core';
import { AuthService } from '../services/auth.service';
import { catchError, throwError } from 'rxjs';
export const authInterceptor: HttpInterceptorFn = (req, next) => {
    const authService = inject(AuthService);
    const token = authService.getToken();
    const correlationId = crypto.randomUUID ? crypto.randomUUID() : 'req-' + Math.random().toString(36).substring(2, 15);

    let modifiedReq = req.clone({
        headers: req.headers.set('X-Correlation-Id', correlationId)
    });

    if (token) {
        modifiedReq = modifiedReq.clone({
            headers: modifiedReq.headers.set('Authorization', `Bearer ${token}`)
        });
    }

    return next(modifiedReq).pipe(
        catchError((error: HttpErrorResponse) => {
            if (error.status === 409 && !req.url.includes('/auth/register')) {
                alert('Conflicto: ' + (error.error?.message || 'No hay stock suficiente para este producto.'));
            } else if (error.status === 401 || error.status === 403) {
                if (!req.url.includes('/auth/login')) {
                    alert('Sesión expirada o acceso denegado. Por favor, inicie sesión nuevamente.');
                    authService.logout();
                }
            } else if (error.status === 500 && !req.url.includes('/auth/register')) {
                alert('Error interno del servidor. Por favor, intente más tarde.');
            }
            return throwError(() => error);
        })
    );
};

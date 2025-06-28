import { Injectable } from '@angular/core';
import { HttpInterceptor, HttpRequest, HttpHandler, HttpEvent } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { catchError, switchMap } from 'rxjs/operators';
import { AuthService } from '../services/auth.service';
import { environment } from '../../../environments/environment';

/**
 * @class AuthInterceptor
 * @description Interceptor HTTP que maneja la autenticación automática de las peticiones API.
 * Añade el token JWT a las cabeceras de las peticiones y gestiona errores de autenticación.
 * Incluye lógica para refrescar automáticamente el token cuando es necesario.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  /**
   * @constructor
   * @param {AuthService} authService - Servicio de autenticación para obtener y gestionar tokens
   */
  constructor(private authService: AuthService) {}

  /**
   * @method intercept
   * @description Intercepta todas las peticiones HTTP y añade el token de autenticación cuando corresponde
   * @param {HttpRequest<any>} req - La petición HTTP original
   * @param {HttpHandler} next - Manejador para continuar con la petición
   * @returns {Observable<HttpEvent<any>>} La respuesta HTTP observable
   */
  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    // Agregar token a las requests que van al backend API
    const isApiRequest = req.url.startsWith(environment.apiUrl) || 
                        req.url.startsWith(environment.backendUrl) ||
                        req.url.includes('/api/');
    
    console.log('[AuthInterceptor] Request:', {
      url: req.url,
      isApiRequest,
      hasToken: !!this.authService.getToken()
    });
    
    if (isApiRequest) {
      const token = this.authService.getToken();
      
      if (token) {
        console.log('[AuthInterceptor] Adding Bearer token to request');
        req = req.clone({
          setHeaders: {
            Authorization: `Bearer ${token}`,
            'Content-Type': 'application/json'
          }
        });
      } else {
        console.log('[AuthInterceptor] No token available for API request');
      }
    }

    return next.handle(req).pipe(
      catchError(error => {
        // Si el token ha expirado (401), intentar refrescarlo
        if (error.status === 401 && this.authService.isAuthenticated()) {
          console.log('[AuthInterceptor] 401 error, attempting token refresh');
          return this.authService.refreshToken().pipe(
            switchMap(() => {
              // Retry la request original con el nuevo token
              const newToken = this.authService.getToken();
              console.log('[AuthInterceptor] Token refreshed, retrying request');
              const newReq = req.clone({
                setHeaders: {
                  Authorization: `Bearer ${newToken}`,
                  'Content-Type': 'application/json'
                }
              });
              return next.handle(newReq);
            }),
            catchError(refreshError => {
              // Si el refresh falla, logout
              console.log('[AuthInterceptor] Token refresh failed, logging out', refreshError);
              this.authService.logout();
              return throwError(refreshError);
            })
          );
        }

        console.log('[AuthInterceptor] HTTP Error:', error.status, error.message);
        return throwError(error);
      })
    );
  }
}

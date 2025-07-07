import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { BehaviorSubject, Observable, of } from 'rxjs';
import { map, tap, catchError } from 'rxjs/operators';
import { Router } from '@angular/router';
import { User, AuthResponse, LoginRequest, RegisterRequest } from '../models/user.model';
import { environment } from '../../../environments/environment';
import { ApiBaseService } from './api-base.service';

/**
 * @class AuthService
 * @description Servicio que maneja la autenticación, registro y gestión de sesiones de usuario.
 * Extiende ApiBaseService para aprovechar funcionalidades comunes de API.
 */
@Injectable({
  providedIn: 'root'
})
export class AuthService extends ApiBaseService {
  /** @property {string} API_URL - URL base para las operaciones de autenticación */
  private readonly API_URL = `${environment.apiUrl}/auth`;
  
  /** @property {BehaviorSubject<User|null>} currentUserSubject - Subject que mantiene el estado del usuario actual */
  private currentUserSubject = new BehaviorSubject<User | null>(null);
  
  /** @property {BehaviorSubject<boolean>} isLoggedInSubject - Subject que mantiene el estado de inicio de sesión */
  private isLoggedInSubject = new BehaviorSubject<boolean>(false);

  /** @property {Observable<User|null>} currentUser$ - Observable público del usuario actual */
  public currentUser$ = this.currentUserSubject.asObservable();
  
  /** @property {Observable<boolean>} isLoggedIn$ - Observable público del estado de inicio de sesión */
  public isLoggedIn$ = this.isLoggedInSubject.asObservable();

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   * @param {Router} router - Servicio de enrutamiento Angular
   */
  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    super();
    console.log('AuthService initialized with API URL:', this.API_URL);
    console.log('Environment config:', environment);
    this.initializeAuthState();
  }

  /**
   * @method login
   * @description Inicia sesión con credenciales de usuario
   * @param {LoginRequest} credentials - Credenciales del usuario (email y contraseña)
   * @returns {Observable<AuthResponse>} Observable con la respuesta de autenticación
   */
  login(credentials: LoginRequest): Observable<AuthResponse> {
    const url = `${this.API_URL}/login`;
    this.logRequest('POST', url, credentials);
    
    return this.http.post<AuthResponse>(url, credentials, this.getHttpOptions())
      .pipe(
        tap(response => {
          this.logResponse('POST', url, response);
          this.setSession(response);
        }),
        catchError(error => {
          this.logError('POST', url, error);
          throw error;
        })
      );
  }

  /**
   * @method register
   * @description Registra un nuevo usuario
   * @param {RegisterRequest} userData - Datos del nuevo usuario
   * @returns {Observable<AuthResponse>} Observable con la respuesta de registro
   */
  register(userData: RegisterRequest): Observable<AuthResponse> {
    const url = `${this.API_URL}/register`;
    this.logRequest('POST', url, userData);
    
    return this.http.post<AuthResponse>(url, userData, this.getHttpOptions())
      .pipe(
        tap(response => {
          this.logResponse('POST', url, response);
          this.setSession(response);
        }),
        catchError(error => {
          this.logError('POST', url, error);
          throw error;
        })
      );
  }

  /**
   * @method logout
   * @description Cierra la sesión del usuario actual
   */
  logout(): void {
    this.clearSession();
    this.router.navigate(['/']);
  }

  /**
   * @method clearSession
   * @description Limpia la sesión del usuario (sin redireccionar)
   * @private
   */
  private clearSession(): void {
    localStorage.removeItem('token');
    localStorage.removeItem('tokenExpiry');
    localStorage.removeItem('currentUser');
    this.currentUserSubject.next(null);
    this.isLoggedInSubject.next(false);
  }

  /**
   * @method initializeAuthState
   * @description Inicializa el estado de autenticación de forma síncrona al cargar la aplicación
   * @private
   */
  private initializeAuthState(): void {
    const token = this.getToken();
    const expiry = localStorage.getItem('tokenExpiry');
    const userStr = localStorage.getItem('currentUser');

    if (token && expiry && userStr) {
      const expiryDate = new Date(expiry);
      if (expiryDate > new Date()) {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        this.isLoggedInSubject.next(true);
        console.log('Auth state initialized - User authenticated:', user.email);
      } else {
        console.log('Auth state initialized - Token expired, clearing session');
        this.clearSession();
      }
    } else {
      console.log('Auth state initialized - No valid session found');
    }
  }

  /**
   * @method checkTokenValidity
   * @description Verifica si el token almacenado es válido y actualiza el estado de sesión
   */
  checkTokenValidity(): void {
    const token = this.getToken();
    const expiry = localStorage.getItem('tokenExpiry');
    const userStr = localStorage.getItem('currentUser');

    if (token && expiry && userStr) {
      const expiryDate = new Date(expiry);
      if (expiryDate > new Date()) {
        const user = JSON.parse(userStr);
        this.currentUserSubject.next(user);
        this.isLoggedInSubject.next(true);
      } else {
        this.clearSession();
      }
    }
  }

  /**
   * @method validateToken
   * @description Valida un token específico contra el backend
   * @param {string} token - Token JWT a validar
   * @returns {Observable<boolean>} Observable que indica si el token es válido
   */
  validateToken(token: string): Observable<boolean> {
    return this.http.post<any>(`${this.API_URL}/validate`, null, {
      params: { token }
    }).pipe(
      map(response => response.valid),
      catchError(() => of(false))
    );
  }

  /**
   * @method refreshToken
   * @description Refresca el token de autenticación actual
   * @returns {Observable<string>} Observable con el nuevo token
   */
  refreshToken(): Observable<string> {
    const currentToken = this.getToken();
    if (!currentToken) {
      throw new Error('No token available');
    }

    return this.http.post<any>(`${this.API_URL}/refresh`, null, {
      params: { token: currentToken }
    }).pipe(
      map(response => response.token),
      tap(newToken => {
        localStorage.setItem('token', newToken);
        // Actualizar fecha de expiración (asumiendo 24 horas)
        const expiry = new Date();
        expiry.setTime(expiry.getTime() + (24 * 60 * 60 * 1000));
        localStorage.setItem('tokenExpiry', expiry.toISOString());
      })
    );
  }

  /**
   * @method getToken
   * @description Obtiene el token JWT almacenado
   * @returns {string|null} Token JWT o null si no existe
   */
  getToken(): string | null {
    return localStorage.getItem('token');
  }

  /**
   * @method getCurrentUser
   * @description Obtiene el usuario actualmente autenticado
   * @returns {User|null} Usuario actual o null
   */
  getCurrentUser(): User | null {
    const user = this.currentUserSubject.value;
    if (user && !user.roleNames) {
      // Ensure roleNames is always an array
      user.roleNames = [];
    }
    return user;
  }

  /**
   * @method isAuthenticated
   * @description Verifica si hay un usuario autenticado actualmente
   * @returns {boolean} True si hay un usuario autenticado
   */
  isAuthenticated(): boolean {
    // Verificar tanto el estado en memoria como el token almacenado
    return this.isLoggedInSubject.value && this.hasValidToken();
  }

  /**
   * @method isLoggedIn
   * @description Alias para isAuthenticated()
   * @returns {boolean} True si hay un usuario autenticado
   */
  isLoggedIn(): boolean {
    return this.isAuthenticated();
  }

  /**
   * @method hasRole
   * @description Verifica si el usuario actual tiene un rol específico
   * @param {string} role - Nombre del rol a verificar
   * @returns {boolean} True si el usuario tiene el rol especificado
   */
  hasRole(role: string): boolean {
    const user = this.getCurrentUser();
    if (!user || !user.roleNames || !Array.isArray(user.roleNames)) {
      console.warn('AuthService: User or roleNames not available', { user });
      return false;
    }
    return user.roleNames.includes(role);
  }

  /**
   * @method isAdmin
   * @description Verifica si el usuario actual tiene rol de administrador
   * @returns {boolean} True si el usuario es administrador
   */
  isAdmin(): boolean {
    return this.hasRole('ADMIN');
  }

  /**
   * @method setSession
   * @description Configura la sesión del usuario después de autenticación exitosa
   * @param {AuthResponse} authResult - Resultado de la autenticación del backend
   * @private
   */
  private setSession(authResult: AuthResponse): void {
    const expiry = new Date();
    
    // Si expiresIn no está definido, usar un valor por defecto de 24 horas
    const expiresInSeconds = authResult.expiresIn || 86400; // 24 horas en segundos
    expiry.setTime(expiry.getTime() + (expiresInSeconds * 1000));

    localStorage.setItem('token', authResult.token);
    localStorage.setItem('tokenExpiry', expiry.toISOString());
    localStorage.setItem('currentUser', JSON.stringify(authResult.user));
    
    console.log('Session set successfully:', {
      token: authResult.token.substring(0, 20) + '...',
      expiresIn: expiresInSeconds,
      expiresAt: expiry.toISOString(),
      user: authResult.user.email
    });
    
    this.currentUserSubject.next(authResult.user);
    this.isLoggedInSubject.next(true);
  }

  /**
   * @method hasValidToken
   * @description Verifica si hay un token válido almacenado de forma síncrona
   * @returns {boolean} True si hay un token válido
   */
  hasValidToken(): boolean {
    const token = this.getToken();
    const expiry = localStorage.getItem('tokenExpiry');
    const userStr = localStorage.getItem('currentUser');

    if (token && expiry && userStr) {
      const expiryDate = new Date(expiry);
      return expiryDate > new Date();
    }
    return false;
  }

  /**
   * @method forceCheckAuthentication
   * @description Fuerza una verificación del estado de autenticación
   * Útil para casos donde necesitamos verificar el estado inmediatamente
   */
  forceCheckAuthentication(): boolean {
    this.checkTokenValidity();
    return this.isAuthenticated();
  }
}

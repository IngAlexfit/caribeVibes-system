import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * @class AuthGuard
 * @description Guard que protege rutas para usuarios autenticados.
 * Verifica si el usuario ha iniciado sesión antes de permitirle acceder a rutas protegidas.
 * Redirige a la página de login si el usuario no está autenticado.
 * @implements {CanActivate}
 */
@Injectable({
  providedIn: 'root'
})
export class AuthGuard implements CanActivate {

  /**
   * @constructor
   * @param {AuthService} authService - Servicio de autenticación
   * @param {Router} router - Servicio de navegación
   */
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * @method canActivate
   * @description Verifica si el usuario puede acceder a una ruta protegida
   * @returns {boolean} Verdadero si el usuario está autenticado, falso en caso contrario
   */
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      return true;
    } else {
      this.router.navigate(['/login']);
      return false;
    }
  }
}

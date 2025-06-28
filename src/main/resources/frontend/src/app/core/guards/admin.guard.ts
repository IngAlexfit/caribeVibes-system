import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * @class AdminGuard
 * @description Guard que protege rutas exclusivas para administradores.
 * Verifica si el usuario está autenticado y tiene rol de administrador.
 * Redirige a la página principal si el usuario no es administrador.
 * @implements {CanActivate}
 */
@Injectable({
  providedIn: 'root'
})
export class AdminGuard implements CanActivate {

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
   * @description Verifica si el usuario puede acceder a una ruta de administrador
   * @returns {boolean} Verdadero si el usuario está autenticado y es administrador
   */
  canActivate(): boolean {
    if (this.authService.isAuthenticated() && this.authService.isAdmin()) {
      return true;
    } else {
      this.router.navigate(['/']);
      return false;
    }
  }
}

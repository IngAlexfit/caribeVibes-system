import { Injectable } from '@angular/core';
import { CanActivate, Router } from '@angular/router';
import { AuthService } from '../services/auth.service';

/**
 * @class UserGuard
 * @description Guard que protege rutas exclusivas para usuarios regulares.
 * Verifica si el usuario está autenticado y NO es administrador.
 * Redirige al panel de administración si el usuario es admin.
 * @implements {CanActivate}
 */
@Injectable({
  providedIn: 'root'
})
export class UserGuard implements CanActivate {

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
   * @description Verifica si el usuario puede acceder a una ruta de usuario regular
   * @returns {boolean} Verdadero si el usuario está autenticado y NO es administrador
   */
  canActivate(): boolean {
    if (this.authService.isAuthenticated()) {
      if (this.authService.isAdmin()) {
        // Si es admin, redirigir al panel de administración
        this.router.navigate(['/admin/dashboard']);
        return false;
      } else {
        // Si es usuario regular, permitir acceso
        return true;
      }
    } else {
      // Si no está autenticado, redirigir al login
      this.router.navigate(['/auth/login']);
      return false;
    }
  }
}

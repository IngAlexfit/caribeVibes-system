import { Component, OnInit, OnDestroy } from '@angular/core';
import { Router } from '@angular/router';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AuthService } from '../../../core/services/auth.service';
import { User } from '../../../core/models/user.model';

/**
 * @class NavbarComponent
 * @description Componente de navegación principal de la aplicación.
 * Gestiona la barra de navegación y cambia su comportamiento según el estado de autenticación.
 * @implements {OnInit}
 * @implements {OnDestroy}
 */
@Component({
  selector: 'app-navbar',
  templateUrl: './navbar.component.html',
  styleUrls: ['./navbar.component.scss']
})
export class NavbarComponent implements OnInit, OnDestroy {
  /** @property {User|null} currentUser - Usuario actualmente autenticado o null si no hay sesión */
  currentUser: User | null = null;
  
  /** @property {boolean} isLoggedIn - Indica si hay un usuario con sesión iniciada */
  isLoggedIn = false;
  
  /** @property {boolean} isAdmin - Indica si el usuario actual tiene rol de administrador */
  isAdmin = false;
  
  /** @property {Subject<void>} destroy$ - Subject para gestionar la cancelación de suscripciones */
  private destroy$ = new Subject<void>();

  /**
   * @constructor
   * @param {AuthService} authService - Servicio de autenticación
   * @param {Router} router - Servicio de enrutamiento de Angular
   */
  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa las suscripciones a los observables del servicio de autenticación
   * para mantener actualizado el estado de la sesión y el usuario actual
   */
  ngOnInit(): void {
    this.authService.currentUser$
      .pipe(takeUntil(this.destroy$))
      .subscribe(user => {
        this.currentUser = user;
        // Actualizar estado de admin
        this.isAdmin = user ? this.authService.hasRole('ADMIN') : false;
      });

    this.authService.isLoggedIn$
      .pipe(takeUntil(this.destroy$))
      .subscribe(isLoggedIn => {
        this.isLoggedIn = isLoggedIn;
      });
  }

  /**
   * @method ngOnDestroy
   * @description Limpia las suscripciones cuando el componente es destruido
   * para evitar fugas de memoria
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * @method logout
   * @description Cierra la sesión del usuario actual
   */
  logout(): void {
    this.authService.logout();
  }

  /**
   * @method navigateToLogin
   * @description Navega a la página de inicio de sesión
   */
  navigateToLogin(): void {
    this.router.navigate(['/login']);
  }

  /**
   * @method navigateToRegister
   * @description Navega a la página de registro
   */
  navigateToRegister(): void {
    this.router.navigate(['/registro']);
  }

  /**
   * @method navigateTo
   * @description Navega a una ruta específica
   * @param {string} route - Ruta destino
   */
  navigateTo(route: string): void {
    this.router.navigate([route]);
  }
}

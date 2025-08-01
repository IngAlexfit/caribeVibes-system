import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { LoginRequest, AuthResponse } from '../../../core/models/user.model';
import { filter, take } from 'rxjs/operators';
import Swal from 'sweetalert2';

/**
 * @class LoginComponent
 * @description Componente para el inicio de sesión de usuarios.
 * Gestiona el formulario de autenticación y redirecciona según el rol del usuario.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {
  /** @property {FormGroup} loginForm - Formulario reactivo para el inicio de sesión */
  loginForm!: FormGroup;
  
  /** @property {boolean} isLoading - Indica si está en proceso de autenticación */
  isLoading = false;
  
  /** @property {boolean} showPassword - Controla la visibilidad de la contraseña */
  showPassword = false;

  /**
   * @constructor
   * @param {FormBuilder} fb - Servicio para crear formularios reactivos
   * @param {AuthService} authService - Servicio de autenticación
   * @param {Router} router - Servicio para navegación entre rutas
   */
  constructor(
    private fb: FormBuilder,
    private authService: AuthService,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente, crea el formulario y verifica si ya hay sesión
   */
  ngOnInit(): void {
    this.initForm();
    
    // Redirect if already logged in
    if (this.authService.isLoggedIn()) {
      this.router.navigate(['/dashboard']);
    }
  }

  /**
   * @method initForm
   * @description Inicializa el formulario de login con sus validadores
   * @private
   */
  private initForm(): void {
    this.loginForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]]
    });
  }
  /**
   * @method onSubmit
   * @description Maneja el evento de envío del formulario de inicio de sesión.
   * Valida el formulario, envía credenciales al servicio de autenticación
   * y redirecciona según el rol del usuario.
   */
  onSubmit(): void {
    if (this.loginForm.valid && !this.isLoading) {
      this.isLoading = true;
      const { email, password } = this.loginForm.value;
      const credentials: LoginRequest = { email, password };

      this.authService.login(credentials).subscribe({
        next: (response: AuthResponse) => {
          console.log('Login successful:', response);
          console.log('Auth state after login:', {
            isAuthenticated: this.authService.isAuthenticated(),
            currentUser: this.authService.getCurrentUser(),
            hasValidToken: this.authService.hasValidToken(),
            token: this.authService.getToken()?.substring(0, 20) + '...'
          });
          
          Swal.fire({
            icon: 'success',
            title: '¡Bienvenido!',
            text: 'Has iniciado sesión exitosamente',
            showConfirmButton: false,
            timer: 1500
          });
          
          // Usar el observable de estado de autenticación para asegurar que la navegación
          // se haga cuando el estado esté completamente establecido
          this.authService.currentUser$.pipe(
            filter(user => user !== null),
            take(1)
          ).subscribe({
            next: (user) => {
              this.isLoading = false;
              console.log('User state confirmed, proceeding with navigation:', {
                user: user?.email,
                isAuthenticated: this.authService.isAuthenticated(),
                hasValidToken: this.authService.hasValidToken()
              });
              
              // Redirect based on user role
              try {
                // Verificar roles directamente del token
                const user = response.user;
                const isAdminUser = user.roles && user.roles.includes('ADMIN');
                console.log('Login: User roles check:', { 
                  userRoles: user.roles, 
                  isAdmin: isAdminUser,
                  authServiceIsAdmin: this.authService.isAdmin()
                });
                
                if (isAdminUser) {
                  console.log('Redirecting to admin dashboard');
                  this.router.navigate(['/admin/dashboard']);
                } else {
                  console.log('Redirecting to user dashboard');
                  this.router.navigate(['/dashboard']);
                }
              } catch (error) {
                console.error('Error during navigation:', error);
                this.isLoading = false;
              }
            },
            error: (error) => {
              console.error('Error waiting for user state:', error);
              this.isLoading = false;
            }
          });
        },
        error: (error: any) => {
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error de autenticación',
            text: error.error?.message || 'Credenciales inválidas',
            confirmButtonColor: '#007bff'
          });
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * @method togglePasswordVisibility
   * @description Alterna la visibilidad de la contraseña entre texto plano y oculto
   */
  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  /**
   * @method markFormGroupTouched
   * @description Marca todos los campos del formulario como tocados para mostrar validaciones
   * @private
   */
  private markFormGroupTouched(): void {
    Object.keys(this.loginForm.controls).forEach(key => {
      this.loginForm.get(key)?.markAsTouched();
    });
  }

  /**
   * @method getFieldError
   * @description Obtiene el mensaje de error para un campo específico del formulario
   * @param {string} fieldName - Nombre del campo del formulario
   * @returns {string} Mensaje de error o cadena vacía si no hay error
   */
  getFieldError(fieldName: string): string {
    const field = this.loginForm.get(fieldName);
    if (field?.touched && field?.errors) {
      if (field.errors['required']) {
        return `El ${fieldName === 'email' ? 'correo electrónico' : 'password'} es requerido`;
      }
      if (field.errors['email']) {
        return 'El formato del correo electrónico no es válido';
      }
      if (field.errors['minlength']) {
        return 'La contraseña debe tener al menos 6 caracteres';
      }
    }
    return '';
  }
}

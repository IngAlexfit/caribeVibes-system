import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';
import { RegisterRequest, AuthResponse } from '../../../core/models/user.model';
import Swal from 'sweetalert2';

/**
 * @class RegisterComponent
 * @description Componente para el registro de nuevos usuarios.
 * Gestiona el formulario de registro completo con validaciones y opciones de preferencias.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.scss']
})
export class RegisterComponent implements OnInit {
  /** @property {FormGroup} registerForm - Formulario reactivo para el registro de usuario */
  registerForm!: FormGroup;
  
  /** @property {boolean} isLoading - Indica si está en proceso de envío del formulario */
  isLoading = false;
  
  /** @property {boolean} showPassword - Controla la visibilidad de la contraseña */
  showPassword = false;
  
  /** @property {boolean} showConfirmPassword - Controla la visibilidad de la confirmación de contraseña */
  showConfirmPassword = false;

  /** 
   * @property {string[]} interests - Lista de intereses disponibles para selección
   * Estas opciones permiten personalizar las recomendaciones de destinos
   */
  interests = [
    'Playa', 'Montañas', 'Cultura', 'Gastronomía', 'Historia', 
    'Naturaleza', 'Deportes', 'Relajación', 'Aventura', 'Fotografía'
  ];

  /**
   * @property {string[]} adventures - Lista de tipos de aventuras disponibles para selección
   * Estas opciones ayudan a personalizar las actividades recomendadas
   */
  adventures = [
    'Buceo', 'Senderismo', 'Parapente', 'Surf', 'Rafting',
    'Escalada', 'Ciclismo', 'Kayak', 'Snorkel', 'Zip Line'
  ];

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
   * @description Inicializa el formulario de registro con todos sus campos y validadores
   * @private
   */
  private initForm(): void {
    this.registerForm = this.fb.group({
      username: ['', [Validators.required, Validators.minLength(3)]],
      firstName: ['', [Validators.required, Validators.minLength(2)]],
      lastName: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, Validators.minLength(6)]],
      confirmPassword: ['', [Validators.required]],
      interests: [[]],
      adventures: [[]],
      acceptTerms: [false, [Validators.requiredTrue]]
    }, {
      validators: this.passwordMatchValidator
    });
  }

  /**
   * @method passwordMatchValidator
   * @description Validador personalizado para verificar que las contraseñas coincidan
   * @param {FormGroup} form - Formulario a validar
   * @returns {null} - Retorna null pero establece errores en el FormControl si es necesario
   * @private
   */
  private passwordMatchValidator(form: FormGroup) {
    const password = form.get('password');
    const confirmPassword = form.get('confirmPassword');
    
    if (password && confirmPassword && password.value !== confirmPassword.value) {
      confirmPassword.setErrors({ passwordMismatch: true });
    } else if (confirmPassword?.errors?.['passwordMismatch']) {
      confirmPassword.setErrors(null);
    }
    
    return null;
  }

  /**
   * @method onSubmit
   * @description Maneja el evento de envío del formulario de registro
   * Valida el formulario, envía los datos al servicio de autenticación
   * y muestra mensajes de éxito o error
   */
  onSubmit(): void {
    if (this.registerForm.valid && !this.isLoading) {
      this.isLoading = true;
      const formValue = this.registerForm.value;
      
      const registerData: RegisterRequest = {
        username: formValue.username,
        firstName: formValue.firstName,
        lastName: formValue.lastName,
        email: formValue.email,
        password: formValue.password,
        confirmPassword: formValue.confirmPassword,
        acceptTerms: formValue.acceptTerms,
        interests: formValue.interests || [],
        adventures: formValue.adventures || []
      };

      console.log('=== REGISTER FORM SUBMISSION ===');
      console.log('Form Value:', formValue);
      console.log('Register Data:', registerData);
      console.log('Making register request...');

      this.authService.register(registerData).subscribe({
        next: (response: AuthResponse) => {
          console.log('=== REGISTER SUCCESS ===');
          console.log('Response:', response);
          this.isLoading = false;
          Swal.fire({
            icon: 'success',
            title: '¡Registro exitoso!',
            text: 'Tu cuenta ha sido creada correctamente',
            showConfirmButton: false,
            timer: 2000
          });
          
          // Redirect to dashboard
          this.router.navigate(['/dashboard']);
        },
        error: (error: any) => {
          console.log('=== REGISTER ERROR ===');
          console.error('Full error object:', error);
          console.error('Error status:', error.status);
          console.error('Error message:', error.message);
          console.error('Error response:', error.error);
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error en el registro',
            text: error.error?.message || error.message || 'Ocurrió un error al crear la cuenta',
            confirmButtonColor: '#007bff'
          });
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  togglePasswordVisibility(): void {
    this.showPassword = !this.showPassword;
  }

  toggleConfirmPasswordVisibility(): void {
    this.showConfirmPassword = !this.showConfirmPassword;
  }

  onInterestChange(interest: string, event: any): void {
    const interests = this.registerForm.get('interests')?.value || [];
    if (event.target.checked) {
      interests.push(interest);
    } else {
      const index = interests.indexOf(interest);
      if (index > -1) {
        interests.splice(index, 1);
      }
    }
    this.registerForm.patchValue({ interests });
  }

  onAdventureChange(adventure: string, event: any): void {
    const adventures = this.registerForm.get('adventures')?.value || [];
    if (event.target.checked) {
      adventures.push(adventure);
    } else {
      const index = adventures.indexOf(adventure);
      if (index > -1) {
        adventures.splice(index, 1);
      }
    }
    this.registerForm.patchValue({ adventures });
  }

  isInterestSelected(interest: string): boolean {
    const interests = this.registerForm.get('interests')?.value || [];
    return interests.includes(interest);
  }

  isAdventureSelected(adventure: string): boolean {
    const adventures = this.registerForm.get('adventures')?.value || [];
    return adventures.includes(adventure);
  }

  private markFormGroupTouched(): void {
    Object.keys(this.registerForm.controls).forEach(key => {
      this.registerForm.get(key)?.markAsTouched();
    });
  }

  getFieldError(fieldName: string): string {
    const field = this.registerForm.get(fieldName);
    if (field?.touched && field?.errors) {
      if (field.errors['required']) {
        return `${this.getFieldLabel(fieldName)} es requerido`;
      }
      if (field.errors['email']) {
        return 'El formato del correo electrónico no es válido';
      }
      if (field.errors['minlength']) {
        const minLength = field.errors['minlength'].requiredLength;
        return `${this.getFieldLabel(fieldName)} debe tener al menos ${minLength} caracteres`;
      }
      if (field.errors['passwordMismatch']) {
        return 'Las contraseñas no coinciden';
      }
      if (field.errors['requiredTrue']) {
        return 'Debes aceptar los términos y condiciones';
      }
    }
    return '';
  }

  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'username': 'El nombre de usuario',
      'firstName': 'El nombre',
      'lastName': 'El apellido',
      'email': 'El correo electrónico',
      'password': 'La contraseña',
      'confirmPassword': 'La confirmación de contraseña'
    };
    return labels[fieldName] || fieldName;
  }
}

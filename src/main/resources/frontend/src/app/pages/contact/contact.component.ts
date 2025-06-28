import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { ContactService } from '../../core/services/contact.service';
import Swal from 'sweetalert2';

/**
 * @class ContactComponent
 * @description Componente para la página de contacto de la aplicación.
 * Permite a los usuarios enviar mensajes y consultas a través de un formulario.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-contact',
  templateUrl: './contact.component.html',
  styleUrls: ['./contact.component.scss']
})
export class ContactComponent implements OnInit {
  /** @property {FormGroup} contactForm - Formulario de contacto con validaciones */
  contactForm!: FormGroup;
  
  /** @property {boolean} isLoading - Indicador de estado de envío del formulario */
  isLoading = false;

  /** @property {Object} contactInfo - Información de contacto de la empresa */
  contactInfo = {
    address: 'Av. del Caribe 123, Puerto Aventura, Caribe',
    phone: '+1 (555) 123-4567',
    email: 'info@caribevibes.com',
    hours: 'Lunes a Domingo: 8:00 AM - 10:00 PM'
  };

  /**
   * @constructor
   * @param {FormBuilder} fb - Servicio para crear formularios reactivos
   * @param {ContactService} contactService - Servicio para enviar mensajes de contacto
   */
  constructor(
    private fb: FormBuilder,
    private contactService: ContactService
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente y el formulario de contacto
   */
  ngOnInit(): void {
    this.initForm();
  }

  /**
   * @method initForm
   * @private
   * @description Inicializa el formulario de contacto con validaciones
   */
  private initForm(): void {
    this.contactForm = this.fb.group({
      name: ['', [Validators.required, Validators.minLength(2)]],
      email: ['', [Validators.required, Validators.email]],
      phone: ['', [Validators.required, Validators.minLength(10)]],
      subject: ['', [Validators.required, Validators.minLength(5)]],
      message: ['', [Validators.required, Validators.minLength(10)]]
    });
  }

  /**
   * @method onSubmit
   * @description Maneja el envío del formulario de contacto
   * Valida el formulario, envía los datos y muestra notificaciones
   */
  onSubmit(): void {
    if (this.contactForm.valid && !this.isLoading) {
      this.isLoading = true;
      const formData = this.contactForm.value;

      this.contactService.createContact(formData).subscribe({
        next: (response: any) => {
          this.isLoading = false;
          Swal.fire({
            icon: 'success',
            title: '¡Mensaje enviado!',
            text: 'Gracias por contactarnos. Te responderemos pronto.',
            showConfirmButton: false,
            timer: 3000
          });
          this.contactForm.reset();
        },
        error: (error: any) => {
          this.isLoading = false;
          Swal.fire({
            icon: 'error',
            title: 'Error al enviar',
            text: 'No pudimos enviar tu mensaje. Inténtalo de nuevo.',
            confirmButtonColor: '#007bff'
          });
        }
      });
    } else {
      this.markFormGroupTouched();
    }
  }

  /**
   * @method markFormGroupTouched
   * @private
   * @description Marca todos los campos del formulario como tocados para mostrar errores
   */
  private markFormGroupTouched(): void {
    Object.keys(this.contactForm.controls).forEach(key => {
      this.contactForm.get(key)?.markAsTouched();
    });
  }

  /**
   * @method getFieldError
   * @description Obtiene el mensaje de error para un campo específico del formulario
   * @param {string} fieldName - Nombre del campo del formulario
   * @returns {string} Mensaje de error o cadena vacía si no hay errores
   */
  getFieldError(fieldName: string): string {
    const field = this.contactForm.get(fieldName);
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
    }
    return '';
  }

  /**
   * @method getFieldLabel
   * @private
   * @description Obtiene la etiqueta legible para un campo del formulario
   * @param {string} fieldName - Nombre del campo
   * @returns {string} Etiqueta legible del campo
   */
  private getFieldLabel(fieldName: string): string {
    const labels: { [key: string]: string } = {
      'name': 'El nombre',
      'email': 'El correo electrónico',
      'phone': 'El teléfono',
      'subject': 'El asunto',
      'message': 'El mensaje'
    };
    return labels[fieldName] || fieldName;
  }
}

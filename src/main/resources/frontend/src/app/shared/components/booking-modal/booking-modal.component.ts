import { Component, Input, Output, EventEmitter, OnInit, OnChanges } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { BookingService } from '../../../core/services/booking.service';
import { AuthService } from '../../../core/services/auth.service';
import { DestinationService } from '../../../core/services/destination.service';
import { BookingRequest } from '../../../core/models/booking.model';
import { HotelResponse, RoomType } from '../../../core/models/hotel.model';
import { Activity } from '../../../core/models/destination.model';
import Swal from 'sweetalert2';

/**
 * @class BookingModalComponent
 * @description Componente modal para realizar reservas de hoteles.
 * Permite seleccionar fechas, habitaciones, huéspedes y actividades opcionales.
 */
@Component({
  selector: 'app-booking-modal',
  templateUrl: './booking-modal.component.html',
  styleUrls: ['./booking-modal.component.scss']
})
export class BookingModalComponent implements OnInit, OnChanges {
  /** @property {HotelResponse|null} hotel - Hotel a reservar */
  @Input() hotel: HotelResponse | null = null;
  
  /** @property {number|null} destinationId - ID del destino para mostrar actividades */
  @Input() destinationId: number | null = null;
  
  /** @property {boolean} isVisible - Controla la visibilidad del modal */
  @Input() isVisible = false;
  
  /** @property {EventEmitter<void>} close - Evento para cerrar el modal */
  @Output() close = new EventEmitter<void>();
  
  /** @property {EventEmitter<any>} bookingCreated - Evento emitido cuando se crea una reserva */
  @Output() bookingCreated = new EventEmitter<any>();

  /** @property {FormGroup} bookingForm - Formulario de reserva */
  bookingForm: FormGroup;
  
  /** @property {boolean} isLoading - Estado de carga */
  isLoading = false;
  
  /** @property {boolean} isSubmitting - Estado de envío del formulario */
  isSubmitting = false;
  
  /** @property {Activity[]} availableActivities - Actividades disponibles en el destino */
  availableActivities: Activity[] = [];
  
  /** @property {number[]} selectedActivities - IDs de actividades seleccionadas */
  selectedActivities: number[] = [];
  
  /** @property {number} totalPrice - Precio total calculado */
  totalPrice = 0;
  
  /** @property {number} basePrice - Precio base del hotel */
  basePrice = 0;
  
  /** @property {number} activitiesPrice - Precio total de actividades */
  activitiesPrice = 0;
  
  /** @property {string} errorMessage - Mensaje de error */
  errorMessage = '';

  /** @property {string} minDate - Fecha mínima para check-in (hoy) */
  minDate = '';

  /** @property {string} minCheckOutDate - Fecha mínima para check-out */
  minCheckOutDate = '';

  constructor(
    private fb: FormBuilder,
    private bookingService: BookingService,
    private authService: AuthService,
    private destinationService: DestinationService
  ) {
    this.bookingForm = this.fb.group({
      checkInDate: ['', [Validators.required, this.futureDateValidator]],
      checkOutDate: ['', [Validators.required, this.futureDateValidator]],
      numGuests: [2, [Validators.required, Validators.min(1), Validators.max(20)]],
      numRooms: [1, [Validators.required, Validators.min(1), Validators.max(10)]],
      roomTypeId: [null, [Validators.required, this.nonZeroValidator]],
      specialRequests: ['']
    });

    // Configurar fechas mínimas
    const today = new Date();
    this.minDate = today.toISOString().split('T')[0];
    
    const tomorrow = new Date(today);
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minCheckOutDate = tomorrow.toISOString().split('T')[0];
  }

  ngOnInit(): void {
    // Configurar validadores dinámicos para fechas
    this.bookingForm.get('checkInDate')?.valueChanges.subscribe(checkInDate => {
      if (checkInDate) {
        const minCheckOut = new Date(checkInDate);
        minCheckOut.setDate(minCheckOut.getDate() + 1);
        this.minCheckOutDate = minCheckOut.toISOString().split('T')[0];
        
        // Resetear checkout si es anterior a la nueva fecha mínima
        const currentCheckOut = this.bookingForm.get('checkOutDate')?.value;
        if (currentCheckOut && currentCheckOut <= checkInDate) {
          this.bookingForm.patchValue({ checkOutDate: '' });
        }
      }
    });

    // Escuchar cambios en el formulario para recalcular precio
    this.bookingForm.valueChanges.subscribe(() => {
      this.calculateTotalPrice();
    });

    // Escuchar cambios en la visibilidad del modal
    this.setupModalWatcher();
  }

  private setupModalWatcher(): void {
    // Podríamos usar ngOnChanges o un setter para isVisible
    // Por ahora, manejaremos esto cuando se abra el modal externamente
  }

  ngOnChanges(): void {
    if (this.isVisible && this.hotel) {
      this.onShow();
    }
  }

  /**
   * @method loadActivities
   * @description Carga las actividades disponibles para el destino
   */
  private loadActivities(): void {
    if (this.destinationId) {
      this.destinationService.getAllDestinationActivities(this.destinationId).subscribe({
        next: (activities) => {
          this.availableActivities = activities;
        },
        error: (error) => {
          console.error('Error loading activities:', error);
        }
      });
    }
  }

  /**
   * @method onShow
   * @description Se ejecuta cuando el modal se muestra
   */
  onShow(): void {
    this.resetForm();
    this.loadActivities();
    this.calculateTotalPrice();
  }

  /**
   * @method resetForm
   * @description Resetea el formulario a sus valores por defecto
   */
  private resetForm(): void {
    this.bookingForm.reset({
      numGuests: 2,
      numRooms: 1,
      roomTypeId: null,
      specialRequests: ''
    });
    this.selectedActivities = [];
    this.errorMessage = '';
    this.totalPrice = 0;
    this.basePrice = 0;
    this.activitiesPrice = 0;
  }

  /**
   * @method toggleActivity
   * @description Alterna la selección de una actividad
   * @param {number} activityId - ID de la actividad
   */
  toggleActivity(activityId: number): void {
    const index = this.selectedActivities.indexOf(activityId);
    if (index > -1) {
      this.selectedActivities.splice(index, 1);
    } else {
      this.selectedActivities.push(activityId);
    }
    this.calculateTotalPrice();
  }

  /**
   * @method isActivitySelected
   * @description Verifica si una actividad está seleccionada
   * @param {number} activityId - ID de la actividad
   * @returns {boolean} True si está seleccionada
   */
  isActivitySelected(activityId: number): boolean {
    return this.selectedActivities.includes(activityId);
  }

  /**
   * @method calculateTotalPrice
   * @description Calcula el precio total de la reserva
   */
  private calculateTotalPrice(): void {
    const formValue = this.bookingForm.value;
    
    // Calcular precio base del hotel
    this.basePrice = 0;
    if (this.hotel && formValue.roomTypeId && formValue.checkInDate && formValue.checkOutDate) {
      const selectedRoom = this.hotel.roomTypes.find(room => room.id === +formValue.roomTypeId);
      if (selectedRoom) {
        const checkIn = new Date(formValue.checkInDate);
        const checkOut = new Date(formValue.checkOutDate);
        const nights = Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24));
        this.basePrice = selectedRoom.pricePerNight * nights;
      }
    }

    // Calcular precio de actividades
    this.activitiesPrice = this.selectedActivities.reduce((total, activityId) => {
      const activity = this.availableActivities.find(a => a.id === activityId);
      return total + (activity ? activity.price : 0);
    }, 0);

    this.totalPrice = this.basePrice + this.activitiesPrice;
  }

  /**
   * @method getNights
   * @description Calcula el número de noches de la estancia
   * @returns {number} Número de noches
   */
  getNights(): number {
    const formValue = this.bookingForm.value;
    if (formValue.checkInDate && formValue.checkOutDate) {
      const checkIn = new Date(formValue.checkInDate);
      const checkOut = new Date(formValue.checkOutDate);
      return Math.ceil((checkOut.getTime() - checkIn.getTime()) / (1000 * 60 * 60 * 24));
    }
    return 0;
  }

  /**
   * @method onSubmit
   * @description Maneja el envío del formulario de reserva
   */
  onSubmit(): void {
    if (this.bookingForm.valid && this.hotel) {
      this.isSubmitting = true;
      this.errorMessage = '';

      // Validación adicional para asegurar que roomTypeId sea válido
      const roomTypeId = this.bookingForm.value.roomTypeId;
      if (!roomTypeId || roomTypeId <= 0) {
        this.errorMessage = 'Debe seleccionar un tipo de habitación válido';
        this.isSubmitting = false;
        return;
      }

      // Crear la reserva básica SIN actividades (el backend no las soporta en el request inicial)
      const bookingRequest: BookingRequest = {
        hotelId: this.hotel.id,
        roomTypeId: Number(roomTypeId),
        checkInDate: this.bookingForm.value.checkInDate,
        checkOutDate: this.bookingForm.value.checkOutDate,
        numGuests: Number(this.bookingForm.value.numGuests),
        numRooms: Number(this.bookingForm.value.numRooms),
        specialRequests: this.bookingForm.value.specialRequests || undefined
        // NO incluir activityIds aquí - el backend no lo soporta
      };

      console.log('Sending booking request:', bookingRequest);

      // Primero crear la reserva
      this.bookingService.createBooking(bookingRequest).subscribe({
        next: (response) => {
          console.log('Booking created successfully:', response);
          
          // Si hay actividades seleccionadas, agregarlas después de crear la reserva
          if (this.selectedActivities.length > 0) {
            this.addActivitiesToBooking(response.id);
          } else {
            // Si no hay actividades, terminar el proceso
            this.completeBookingProcess(response);
          }
        },
        error: (error) => {
          this.isSubmitting = false;
          console.error('Booking error:', error);
          
          // Mostrar errores de validación específicos si están disponibles
          if (error.error?.validationErrors) {
            const validationErrors = error.error.validationErrors;
            let errorMessages = [];
            
            if (validationErrors.numRooms) errorMessages.push(validationErrors.numRooms);
            if (validationErrors.numGuests) errorMessages.push(validationErrors.numGuests);
            if (validationErrors.checkInDate) errorMessages.push(validationErrors.checkInDate);
            if (validationErrors.checkOutDate) errorMessages.push(validationErrors.checkOutDate);
            if (validationErrors.roomTypeId) errorMessages.push('Debe seleccionar un tipo de habitación');
            
            this.errorMessage = errorMessages.length > 0 
              ? errorMessages.join('. ') 
              : 'Errores de validación en el formulario';
          } else {
            this.errorMessage = error.error?.message || 'Error al crear la reserva. Por favor, inténtalo de nuevo.';
          }
        }
      });
    } else {
      this.markFormGroupTouched();
      this.errorMessage = 'Por favor, complete todos los campos requeridos';
    }
  }

  /**
   * @method addActivitiesToBooking
   * @description Agrega actividades a una reserva creada
   * @param {number} bookingId - ID de la reserva creada
   */
  private addActivitiesToBooking(bookingId: number): void {
    let activitiesAdded = 0;
    const totalActivities = this.selectedActivities.length;

    this.selectedActivities.forEach(activityId => {
      // Por defecto, agregar 1 persona por actividad (esto podría mejorarse en el futuro)
      const quantity = this.bookingForm.value.numGuests || 1;
      
      this.bookingService.addActivityToBooking(bookingId, activityId, quantity).subscribe({
        next: (activityResponse: any) => {
          console.log('Activity added successfully:', activityResponse);
          activitiesAdded++;
          
          // Si se han agregado todas las actividades, terminar el proceso
          if (activitiesAdded === totalActivities) {
            // Obtener la reserva completa con actividades
            this.bookingService.getBookingById(bookingId).subscribe({
              next: (completeBooking: any) => {
                this.completeBookingProcess(completeBooking);
              },
              error: (error: any) => {
                console.error('Error getting complete booking:', error);
                // Aunque haya error obteniendo la reserva completa, la creación fue exitosa
                this.completeBookingProcess({ id: bookingId });
              }
            });
          }
        },
        error: (error: any) => {
          console.error('Error adding activity:', error);
          activitiesAdded++;
          
          // Continuar aunque falle una actividad
          if (activitiesAdded === totalActivities) {
            this.completeBookingProcess({ id: bookingId });
          }
        }
      });
    });
  }

  /**
   * @method completeBookingProcess
   * @description Completa el proceso de reserva con SweetAlert2
   * @param {any} response - Respuesta de la reserva
   */
  private completeBookingProcess(response: any): void {
    this.isSubmitting = false;
    this.bookingCreated.emit(response);
    
    // Usar SweetAlert2 para una mejor experiencia de usuario
    Swal.fire({
      title: '¡Reserva Exitosa!',
      text: 'Tu reserva ha sido creada exitosamente.',
      icon: 'success',
      confirmButtonText: 'Aceptar',
      confirmButtonColor: '#28a745',
      background: '#fff',
      color: '#333',
      timer: 3000,
      timerProgressBar: true
    }).then(() => {
      this.closeModal();
    });
  }

  /**
   * @method markFormGroupTouched
   * @description Marca todos los campos del formulario como tocados para mostrar errores
   */
  private markFormGroupTouched(): void {
    Object.keys(this.bookingForm.controls).forEach(key => {
      const control = this.bookingForm.get(key);
      control?.markAsTouched();
    });
  }

  /**
   * @method closeModal
   * @description Cierra el modal
   */
  closeModal(): void {
    this.close.emit();
  }

  /**
   * @method isFieldInvalid
   * @description Verifica si un campo es inválido y ha sido tocado
   * @param {string} fieldName - Nombre del campo
   * @returns {boolean} True si el campo es inválido
   */
  isFieldInvalid(fieldName: string): boolean {
    const field = this.bookingForm.get(fieldName);
    return !!(field && field.invalid && field.touched);
  }

  /**
   * @method formatCurrency
   * @description Formatea un número como moneda
   * @param {number} amount - Monto a formatear
   * @returns {string} Monto formateado
   */
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'COP'
    }).format(amount);
  }

  /**
   * @method futureDateValidator
   * @description Validador personalizado para fechas futuras
   * @param {any} control - Control del formulario
   * @returns {any} Error si la fecha no es futura, null si es válida
   */
  futureDateValidator(control: any): { [key: string]: any } | null {
    if (!control.value) return null;
    
    const inputDate = new Date(control.value);
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    
    return inputDate <= today ? { 'notFutureDate': true } : null;
  }

  /**
   * @method nonZeroValidator
   * @description Validador personalizado para valores no nulos y no vacíos
   * @param {any} control - Control del formulario
   * @returns {any} Error si el valor es nulo/vacío, null si es válido
   */
  nonZeroValidator(control: any): { [key: string]: any } | null {
    const value = control.value;
    if (value === null || value === undefined || value === '' || value === 0) {
      return { 'invalidValue': true };
    }
    return null;
  }

  /**
   * @method getActivityName
   * @description Obtiene el nombre de una actividad por su ID
   * @param {number} activityId - ID de la actividad
   * @returns {string} Nombre de la actividad
   */
  getActivityName(activityId: number): string {
    const activity = this.availableActivities.find(a => a.id === activityId);
    return activity ? activity.name : '';
  }

  /**
   * @method getActivityPrice
   * @description Obtiene el precio de una actividad por su ID
   * @param {number} activityId - ID de la actividad
   * @returns {number} Precio de la actividad
   */
  getActivityPrice(activityId: number): number {
    const activity = this.availableActivities.find(a => a.id === activityId);
    return activity ? activity.price : 0;
  }
}

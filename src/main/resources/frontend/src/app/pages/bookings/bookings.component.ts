import { Component, OnInit } from '@angular/core';
import { BookingService } from '../../core/services/booking.service';
import { BookingResponse } from '../../core/models/booking.model';
import Swal from 'sweetalert2';

/**
 * @class BookingsComponent
 * @description Componente para gestionar y visualizar las reservas del usuario.
 * Permite al usuario ver, filtrar y cancelar sus reservas de viaje.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-bookings',
  templateUrl: './bookings.component.html',
  styleUrls: ['./bookings.component.scss']
})
export class BookingsComponent implements OnInit {
  /** @property {BookingResponse[]} bookings - Lista completa de reservas del usuario */
  bookings: BookingResponse[] = [];
  
  /** @property {BookingResponse[]} filteredBookings - Lista de reservas filtradas para mostrar */
  filteredBookings: BookingResponse[] = [];
  
  /** @property {boolean} isLoading - Indicador de estado de carga de datos */
  isLoading = true;
  
  /** @property {string} selectedStatus - Estado de reserva seleccionado para filtrar */
  selectedStatus = '';
  
  /** @property {Array<{value: string, label: string}>} statusOptions - Opciones de estados para el filtro */
  statusOptions = [
    { value: '', label: 'Todos los estados' },
    { value: 'PENDING', label: 'Pendiente' },
    { value: 'CONFIRMED', label: 'Confirmada' },
    { value: 'COMPLETED', label: 'Completada' },
    { value: 'CANCELLED', label: 'Cancelada' }
  ];

  /** @property {BookingResponse|null} selectedBooking - Reserva seleccionada para ver detalles */
  selectedBooking: BookingResponse | null = null;
  
  /** @property {boolean} showDetailsModal - Controla la visibilidad del modal de detalles */
  showDetailsModal = false;

  /**
   * @constructor
   * @param {BookingService} bookingService - Servicio para gestionar las reservas
   */
  constructor(private bookingService: BookingService) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente y carga las reservas del usuario
   */
  ngOnInit(): void {
    this.loadBookings();
  }

  /**
   * @method loadBookings
   * @private
   * @description Carga todas las reservas del usuario desde el servidor
   */
  private loadBookings(): void {
    this.isLoading = true;
    
    this.bookingService.getMyBookings().subscribe({
      next: (response) => {
        this.bookings = response.content;
        this.filteredBookings = response.content;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading bookings:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * @method onStatusChange
   * @description Maneja el cambio en el filtro de estado de reservas
   */
  onStatusChange(): void {
    this.filterBookings();
  }

  /**
   * @method filterBookings
   * @private
   * @description Filtra las reservas según el estado seleccionado
   */
  private filterBookings(): void {
    this.filteredBookings = this.bookings.filter(booking => {
      return !this.selectedStatus || booking.status === this.selectedStatus;
    });
  }

  /**
   * @method getStatusBadgeClass
   * @description Devuelve la clase CSS correspondiente al estado de la reserva
   * @param {string} status - Estado de la reserva
   * @returns {string} Clase CSS para la insignia de estado
   */
  getStatusBadgeClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'confirmed':
        return 'badge-success';
      case 'pending':
        return 'badge-warning';
      case 'cancelled':
        return 'badge-danger';
      case 'completed':
        return 'badge-info';
      default:
        return 'badge-secondary';
    }
  }

  /**
   * @method getStatusText
   * @description Traduce el código de estado a texto legible
   * @param {string} status - Estado de la reserva en formato código
   * @returns {string} Texto legible del estado
   */
  getStatusText(status: string): string {
    const option = this.statusOptions.find(s => s.value === status);
    return option ? option.label : status;
  }

  /**
   * @method getStatusLabel
   * @description Obtiene la etiqueta traducida del estado de la reserva
   * @param {string} status - Estado de la reserva
   * @returns {string} Etiqueta traducida
   */
  getStatusLabel(status: string): string {
    const statusOption = this.statusOptions.find(option => option.value === status);
    return statusOption ? statusOption.label : status;
  }

  /**
   * @method formatDate
   * @description Formatea una fecha en formato legible en español
   * @param {string | Date} date - Fecha a formatear
   * @returns {string} Fecha formateada
   */
  formatDate(date: string | Date): string {
    return new Date(date).toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'long',
      day: 'numeric'
    });
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
   * @method clearFilters
   * @description Limpia todos los filtros aplicados
   */
  clearFilters(): void {
    this.selectedStatus = '';
    this.filteredBookings = this.bookings;
  }

  /**
   * @method refreshBookings
   * @description Actualiza la lista de reservas desde el servidor
   */
  refreshBookings(): void {
    this.loadBookings();
  }

  /**
   * @method viewBookingDetails
   * @description Muestra los detalles de una reserva específica en un modal
   * @param {number} bookingId - ID de la reserva a visualizar
   */
  viewBookingDetails(bookingId: number): void {
    this.isLoading = true;
    
    this.bookingService.getBookingById(bookingId).subscribe({
      next: (booking) => {
        this.isLoading = false;
        this.selectedBooking = booking;
        this.showBookingDetailsModal(booking);
      },
      error: (error) => {
        this.isLoading = false;
        console.error('Error loading booking details:', error);
        Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar los detalles de la reserva',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      }
    });
  }

  /**
   * @method showBookingDetailsModal
   * @description Muestra el modal con los detalles de la reserva
   * @param {BookingResponse} booking - Datos de la reserva
   */
  private showBookingDetailsModal(booking: BookingResponse): void {
    const activitiesHtml = booking.activities && booking.activities.length > 0 
      ? `<div class="activities-section">
           <h5><i class="fas fa-star"></i> Actividades Incluidas:</h5>
           <ul class="activities-list">
             ${booking.activities.map(activity => 
               `<li><strong>${activity.activityName}</strong> - ${this.formatCurrency(activity.activityPrice)} (x${activity.quantity})</li>`
             ).join('')}
           </ul>
         </div>`
      : '<p><em>No se han incluido actividades en esta reserva.</em></p>';

    const specialRequestsHtml = booking.specialRequests 
      ? `<div class="special-requests-section">
           <h5><i class="fas fa-comment"></i> Solicitudes Especiales:</h5>
           <p>${booking.specialRequests}</p>
         </div>`
      : '';

    Swal.fire({
      title: `<i class="fas fa-info-circle"></i> Detalles de Reserva #${booking.id}`,
      html: `
        <div class="booking-details-modal">
          <div class="hotel-info">
            <h4><i class="fas fa-hotel"></i> ${booking.hotelName}</h4>
          </div>
          
          <div class="booking-info">
            <div class="info-row">
              <div class="info-item">
                <strong><i class="fas fa-calendar-check"></i> Check-in:</strong>
                <span>${new Date(booking.checkInDate).toLocaleDateString('es-ES')}</span>
              </div>
              <div class="info-item">
                <strong><i class="fas fa-calendar-times"></i> Check-out:</strong>
                <span>${new Date(booking.checkOutDate).toLocaleDateString('es-ES')}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <strong><i class="fas fa-users"></i> Huéspedes:</strong>
                <span>${booking.guests}</span>
              </div>
              <div class="info-item">
                <strong><i class="fas fa-bed"></i> Tipo de Habitación:</strong>
                <span>${booking.roomTypeName}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <strong><i class="fas fa-info-circle"></i> Estado:</strong>
                <span class="status status-${booking.status.toLowerCase()}">${this.getStatusLabel(booking.status)}</span>
              </div>
              <div class="info-item">
                <strong><i class="fas fa-dollar-sign"></i> Total:</strong>
                <span class="total-amount">${this.formatCurrency(booking.totalPrice)}</span>
              </div>
            </div>
            
            <div class="info-row">
              <div class="info-item">
                <strong><i class="fas fa-calendar-plus"></i> Fecha de Reserva:</strong>
                <span>${new Date(booking.bookingDate).toLocaleDateString('es-ES')}</span>
              </div>
            </div>
          </div>
          
          ${activitiesHtml}
          ${specialRequestsHtml}
        </div>
      `,
      width: 600,
      confirmButtonText: 'Cerrar',
      confirmButtonColor: '#007bff',
      customClass: {
        htmlContainer: 'booking-details-container'
      }
    });
  }

  /**
   * @method cancelBooking
   * @description Cancela una reserva tras confirmación del usuario con SweetAlert2
   * @param {number} bookingId - ID de la reserva a cancelar
   */
  cancelBooking(bookingId: number): void {
    Swal.fire({
      title: '¿Cancelar Reserva?',
      text: 'Esta acción no se puede deshacer. ¿Estás seguro de que deseas cancelar esta reserva?',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc3545',
      cancelButtonColor: '#6c757d',
      confirmButtonText: 'Sí, cancelar',
      cancelButtonText: 'No, mantener'
    }).then((result) => {
      if (result.isConfirmed) {
        this.bookingService.cancelBooking(bookingId).subscribe({
          next: () => {
            Swal.fire({
              title: '¡Cancelada!',
              text: 'Tu reserva ha sido cancelada exitosamente.',
              icon: 'success',
              confirmButtonText: 'Aceptar',
              timer: 3000,
              timerProgressBar: true
            });
            this.loadBookings(); // Refresh the list
          },
          error: (error) => {
            console.error('Error cancelling booking:', error);
            Swal.fire({
              title: 'Error',
              text: 'No se pudo cancelar la reserva. Por favor, inténtalo de nuevo.',
              icon: 'error',
              confirmButtonText: 'Aceptar'
            });
          }
        });
      }
    });
  }
}

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
    // Extract hotel information with null checks
    const hotelName = booking.hotel?.name || booking.hotelName || 'Hotel no especificado';
    const hotelLocation = booking.hotel?.location || 'Ubicación no especificada';
    const hotelImageUrl = booking.hotel?.imageUrl || '';
    const hotelRating = booking.hotel?.rating || 0;
    
    // Extract room type information with null checks
    const roomTypeName = booking.roomType?.name || booking.roomTypeName || 'Tipo de habitación no especificado';
    const roomTypeDescription = booking.roomType?.description || '';
    const roomTypeCapacity = booking.roomType?.maxOccupancy || 0;
    const roomTypePrice = booking.roomType?.pricePerNight || 0;
    
    // Extract user information with null checks
    const userName = booking.user?.name || 'Usuario no especificado';
    const userEmail = booking.user?.email || '';
    
    // Extract guest and room information with fallback to legacy fields
    const numGuests = booking.numGuests || booking.guests || 0;
    const numRooms = booking.numRooms || 1;
    
    // Build activities HTML with improved formatting
    const activitiesHtml = booking.activities && booking.activities.length > 0 
      ? `<div class="activities-section">
           <h5><i class="fas fa-star text-warning"></i> Actividades Incluidas (${booking.activities.length}):</h5>
           <div class="activities-grid">
             ${booking.activities.map(activity => 
               `<div class="activity-card">
                  <div class="activity-header">
                    <h6>${activity.activity?.name || activity.activityName || 'Actividad sin nombre'}</h6>
                    <span class="activity-price">${this.formatCurrency(activity.activity?.price || activity.pricePerPerson || 0)}</span>
                  </div>
                  <p class="activity-description">${activity.activity?.description || 'Sin descripción'}</p>
                  <div class="activity-details">
                    <div class="activity-quantity">
                      <small class="text-muted">
                        <i class="fas fa-hashtag"></i> 
                        Cantidad: ${activity.quantity || 1}
                      </small>
                    </div>
                    ${activity.scheduledDate ? `
                      <div class="activity-date">
                        <small class="text-muted">
                          <i class="fas fa-calendar-alt"></i> 
                          Fecha programada: ${new Date(activity.scheduledDate).toLocaleDateString('es-ES')}
                        </small>
                      </div>
                    ` : ''}
                  </div>
                </div>`
             ).join('')}
           </div>
         </div>`
      : `<div class="activities-section">
           <h5><i class="fas fa-star text-muted"></i> Actividades:</h5>
           <p class="text-muted"><em>No se han incluido actividades en esta reserva.</em></p>
         </div>`;

    const specialRequestsHtml = booking.specialRequests 
      ? `<div class="special-requests-section">
           <h5><i class="fas fa-comment text-info"></i> Solicitudes Especiales:</h5>
           <div class="special-requests-content">
             <p>${booking.specialRequests}</p>
           </div>
         </div>`
      : '';

    // Calculate nights
    const checkInDate = new Date(booking.checkInDate);
    const checkOutDate = new Date(booking.checkOutDate);
    const nights = Math.ceil((checkOutDate.getTime() - checkInDate.getTime()) / (1000 * 60 * 60 * 24));

    Swal.fire({
      title: `<i class="fas fa-info-circle"></i> Detalles de Reserva #${booking.id}`,
      html: `
        <div class="booking-details-modal">
          <div class="hotel-info-section">
            <div class="hotel-header">
              ${hotelImageUrl ? `<img src="${hotelImageUrl}" alt="${hotelName}" class="hotel-image"/>` : ''}
              <div class="hotel-details">
                <h4><i class="fas fa-hotel text-primary"></i> ${hotelName}</h4>
                <p class="hotel-location">
                  <i class="fas fa-map-marker-alt text-danger"></i> ${hotelLocation}
                </p>
                ${hotelRating > 0 ? `
                  <div class="hotel-rating">
                    <div class="stars">
                      ${Array.from({length: 5}, (_, i) => 
                        `<i class="fas fa-star ${i < hotelRating ? 'text-warning' : 'text-muted'}"></i>`
                      ).join('')}
                    </div>
                    <span class="rating-text">(${hotelRating}/5)</span>
                  </div>
                ` : ''}
              </div>
            </div>
          </div>
          
          <div class="booking-info-section">
            <div class="info-grid">
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-calendar-check text-success"></i>
                  <strong>Check-in</strong>
                </div>
                <div class="info-value">${checkInDate.toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</div>
              </div>
              
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-calendar-times text-warning"></i>
                  <strong>Check-out</strong>
                </div>
                <div class="info-value">${checkOutDate.toLocaleDateString('es-ES', { weekday: 'long', year: 'numeric', month: 'long', day: 'numeric' })}</div>
              </div>
              
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-moon text-info"></i>
                  <strong>Noches</strong>
                </div>
                <div class="info-value">${nights} noche${nights !== 1 ? 's' : ''}</div>
              </div>
              
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-users text-primary"></i>
                  <strong>Huéspedes</strong>
                </div>
                <div class="info-value">${numGuests} huésped${numGuests !== 1 ? 'es' : ''}</div>
              </div>
              
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-bed text-secondary"></i>
                  <strong>Habitaciones</strong>
                </div>
                <div class="info-value">${numRooms} habitación${numRooms !== 1 ? 'es' : ''}</div>
              </div>
              
              <div class="info-card">
                <div class="info-header">
                  <i class="fas fa-door-open text-info"></i>
                  <strong>Tipo de Habitación</strong>
                </div>
                <div class="info-value">${roomTypeName}</div>
                ${roomTypeDescription ? `<div class="info-sub">${roomTypeDescription}</div>` : ''}
                ${roomTypeCapacity > 0 ? `<div class="info-sub">Capacidad: ${roomTypeCapacity} personas</div>` : ''}
                ${roomTypePrice > 0 ? `<div class="info-sub">Precio: ${this.formatCurrency(roomTypePrice)}/noche</div>` : ''}
              </div>
            </div>
          </div>
          
          <div class="status-price-section">
            <div class="status-info">
              <div class="info-header">
                <i class="fas fa-info-circle"></i>
                <strong>Estado</strong>
              </div>
              <span class="status-badge status-${booking.status.toLowerCase()}">${this.getStatusLabel(booking.status)}</span>
            </div>
            
            <div class="price-info">
              <div class="info-header">
                <i class="fas fa-dollar-sign text-success"></i>
                <strong>Total</strong>
              </div>
              <div class="total-amount">${this.formatCurrency(booking.totalPrice)}</div>
            </div>
          </div>
          
          <div class="booking-meta-section">
            <div class="meta-item">
              <i class="fas fa-calendar-plus text-muted"></i>
              <strong>Fecha de Reserva:</strong>
              <span>${new Date(booking.bookingDate).toLocaleDateString('es-ES', { year: 'numeric', month: 'long', day: 'numeric' })}</span>
            </div>
            <div class="meta-item">
              <i class="fas fa-user text-muted"></i>
              <strong>Reservado por:</strong>
              <span>${userName}${userEmail ? ` (${userEmail})` : ''}</span>
            </div>
          </div>
          
          ${activitiesHtml}
          ${specialRequestsHtml}
        </div>
      `,
      width: 800,
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

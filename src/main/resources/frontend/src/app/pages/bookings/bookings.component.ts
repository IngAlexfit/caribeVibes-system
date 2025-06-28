import { Component, OnInit } from '@angular/core';
import { BookingService } from '../../core/services/booking.service';
import { BookingResponse } from '../../core/models/booking.model';

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
   * @description Formatea un número como moneda en Euros
   * @param {number} amount - Monto a formatear
   * @returns {string} Monto formateado como moneda
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
   * @description Muestra los detalles de una reserva específica
   * @param {number} bookingId - ID de la reserva a visualizar
   */
  viewBookingDetails(bookingId: number): void {
    // Navigate to booking details or show modal
    console.log('View booking details:', bookingId);
  }

  /**
   * @method cancelBooking
   * @description Cancela una reserva tras confirmación del usuario
   * @param {number} bookingId - ID de la reserva a cancelar
   */
  cancelBooking(bookingId: number): void {
    if (confirm('¿Estás seguro de que deseas cancelar esta reserva?')) {
      this.bookingService.cancelBooking(bookingId).subscribe({
        next: () => {
          console.log('Booking cancelled successfully');
          this.loadBookings(); // Refresh the list
        },
        error: (error) => {
          console.error('Error cancelling booking:', error);
        }
      });
    }
  }
}

import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { BookingService } from '../../core/services/booking.service';
import { BookingResponse } from '../../core/models/booking.model';
import { PageResponse } from '../../core/models/common.model';
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

  // Pagination properties
  /** @property {number} currentPage - Página actual */
  currentPage = 0;
  
  /** @property {number} pageSize - Tamaño de página */
  pageSize = 10;
  
  /** @property {number} totalElements - Total de elementos */
  totalElements = 0;
  
  /** @property {number} totalPages - Total de páginas */
  totalPages = 0;
  
  /** @property {boolean} hasNext - Indica si hay página siguiente */
  hasNext = false;
  
  /** @property {boolean} hasPrevious - Indica si hay página anterior */
  hasPrevious = false;

  /**
   * @constructor
   * @param {BookingService} bookingService - Servicio para gestionar las reservas
   * @param {Router} router - Router para navegación
   */
  constructor(
    private bookingService: BookingService,
    private router: Router
  ) {}

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
   * @description Carga las reservas del usuario con paginación
   */
  private loadBookings(): void {
    this.isLoading = true;
    
    this.bookingService.getMyBookings(this.currentPage, this.pageSize).subscribe({
      next: (response: PageResponse<BookingResponse>) => {
        this.bookings = response.content;
        this.totalElements = response.totalElements;
        this.totalPages = response.totalPages;
        this.hasNext = !response.last;
        this.hasPrevious = !response.first;
        this.isLoading = false;
        this.filterBookings();
      },
      error: (error: any) => {
        console.error('Error loading bookings:', error);
        this.isLoading = false;
        Swal.fire({
          title: 'Error',
          text: 'No se pudieron cargar las reservas. Por favor, intenta de nuevo.',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
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
   * @method changePage
   * @description Cambia la página actual y recarga los datos
   * @param {number} page - Número de página
   */
  changePage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadBookings();
    }
  }

  /**
   * @method nextPage
   * @description Navega a la página siguiente
   */
  nextPage(): void {
    if (this.hasNext) {
      this.currentPage++;
      this.loadBookings();
    }
  }

  /**
   * @method previousPage
   * @description Navega a la página anterior
   */
  previousPage(): void {
    if (this.hasPrevious) {
      this.currentPage--;
      this.loadBookings();
    }
  }

  /**
   * @method goToPage
   * @description Navega a una página específica
   * @param {number} page - Número de página al que navegar
   */
  goToPage(page: number): void {
    if (page >= 0 && page < this.totalPages) {
      this.currentPage = page;
      this.loadBookings();
    }
  }

  /**
   * @method getPageNumbers
   * @description Obtiene los números de página para mostrar en la paginación
   * @returns {number[]} Array de números de página
   */
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const maxPages = Math.min(this.totalPages, 5); // Mostrar un máximo de 5 números de página
    const startPage = Math.max(0, this.currentPage - 2);
    const endPage = Math.min(this.totalPages - 1, startPage + maxPages - 1);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
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
   * @description Navega a la página de detalles de la reserva
   * @param {number} bookingId - ID de la reserva a visualizar
   */
  viewBookingDetails(bookingId: number): void {
    this.router.navigate(['/bookings', bookingId]);
  }

  /**
   * @method quickViewBooking
   * @description Muestra un modal rápido con información básica de la reserva
   * @param {number} bookingId - ID de la reserva a visualizar
   */
  quickViewBooking(bookingId: number): void {
    this.isLoading = true;
    
    this.bookingService.getBookingById(bookingId).subscribe({
      next: (booking) => {
        this.isLoading = false;
        this.selectedBooking = booking;
        this.showQuickViewModal(booking);
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
   * @method showQuickViewModal
   * @description Muestra un modal condensado con información básica de la reserva
   * @param {BookingResponse} booking - Datos de la reserva
   */
  private showQuickViewModal(booking: BookingResponse): void {
    const nights = Math.ceil((new Date(booking.checkOutDate).getTime() - new Date(booking.checkInDate).getTime()) / (1000 * 60 * 60 * 24));
    
    Swal.fire({
      title: `<i class="fas fa-info-circle"></i> Reserva #${booking.id}`,
      html: `
        <div class="quick-view-modal">
          <div class="booking-summary">
            <div class="summary-item">
              <strong>Hotel:</strong> ${booking.hotel?.name || 'No especificado'}
            </div>
            <div class="summary-item">
              <strong>Fechas:</strong> ${this.formatDate(booking.checkInDate)} - ${this.formatDate(booking.checkOutDate)}
            </div>
            <div class="summary-item">
              <strong>Duración:</strong> ${nights} noche${nights !== 1 ? 's' : ''}
            </div>
            <div class="summary-item">
              <strong>Huéspedes:</strong> ${booking.numGuests}
            </div>
            <div class="summary-item">
              <strong>Total:</strong> ${this.formatCurrency(booking.totalPrice)}
            </div>
            <div class="summary-item">
              <strong>Estado:</strong> <span class="status-${booking.status.toLowerCase()}">${this.getStatusLabel(booking.status)}</span>
            </div>
          </div>
          <div class="modal-actions">
            <button class="btn btn-primary" onclick="window.location.href='/bookings/${booking.id}'">
              <i class="fas fa-eye"></i> Ver detalles completos
            </button>
          </div>
        </div>
      `,
      width: 500,
      showConfirmButton: false,
      showCloseButton: true,
      customClass: {
        htmlContainer: 'quick-view-container'
      }
    });
  }
  /**
   * @method downloadVoucher
   * @description Descarga el voucher de una reserva
   * @param {number} bookingId - ID de la reserva
   */
  downloadVoucher(bookingId: number): void {
    // Mostrar loading
    Swal.fire({
      title: 'Descargando voucher...',
      text: 'Por favor espere mientras se genera el documento.',
      allowOutsideClick: false,
      showConfirmButton: false,
      didOpen: () => {
        Swal.showLoading();
      }
    });

    this.bookingService.downloadVoucher(bookingId).subscribe({
      next: (blob: Blob) => {
        // Crear URL para el blob
        const url = window.URL.createObjectURL(blob);
        
        // Crear elemento de descarga
        const link = document.createElement('a');
        link.href = url;
        link.download = `voucher-reserva-${bookingId}.pdf`;
        
        // Simular click para descargar
        document.body.appendChild(link);
        link.click();
        
        // Limpiar
        document.body.removeChild(link);
        window.URL.revokeObjectURL(url);
        
        // Mostrar éxito
        Swal.fire({
          title: 'Voucher descargado',
          text: 'El voucher se ha descargado correctamente.',
          icon: 'success',
          confirmButtonText: 'Entendido'
        });
      },
      error: (error) => {
        console.error('Error descargando voucher:', error);
        Swal.fire({
          title: 'Error',
          text: 'Ocurrió un error al descargar el voucher. Por favor intente nuevamente.',
          icon: 'error',
          confirmButtonText: 'Entendido'
        });
      }
    });
  }

  /**
   * @method trackByBookingId
   * @description Track by function for ngFor optimization
   * @param {number} index - Index of the item
   * @param {BookingResponse} booking - Booking item
   * @returns {number} Unique identifier
   */
  trackByBookingId(index: number, booking: BookingResponse): number {
    return booking.id;
  }

  /**
   * @method getPendingCount
   * @description Get count of pending bookings
   * @returns {number} Count of pending bookings
   */
  get pendingBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'PENDING').length;
  }

  /**
   * @method getConfirmedCount
   * @description Get count of confirmed bookings
   * @returns {number} Count of confirmed bookings
   */
  get confirmedBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'CONFIRMED').length;
  }

  /**
   * @method getCompletedCount
   * @description Get count of completed bookings
   * @returns {number} Count of completed bookings
   */
  get completedBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'COMPLETED').length;
  }

  /**
   * @method getCancelledCount
   * @description Get count of cancelled bookings
   * @returns {number} Count of cancelled bookings
   */
  get cancelledBookingsCount(): number {
    return this.bookings.filter(b => b.status === 'CANCELLED').length;
  }

  /**
   * @method getDisplayedRange
   * @description Get the displayed range for pagination
   * @returns {object} Object with start and end values
   */
  getDisplayedRange(): { start: number; end: number } {
    const start = (this.currentPage * this.pageSize) + 1;
    const end = Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
    return { start, end };
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

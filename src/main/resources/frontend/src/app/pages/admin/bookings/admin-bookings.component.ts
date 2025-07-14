import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { AdminService } from '../../../core/services/admin.service';
import { BookingResponse, BookingStatus } from '../../../core/models/booking.model';
import { PageResponse } from '../../../core/models/common.model';
import Swal from 'sweetalert2';

/**
 * @interface BookingFilters
 * @description Filtros para la búsqueda de reservas
 */
interface BookingFilters {
  /** Estado de la reserva */
  status: string;
  /** Fecha desde */
  dateFrom: string;
  /** Fecha hasta */
  dateTo: string;
  /** Término de búsqueda */
  searchTerm: string;
  /** Hotel */
  hotelId: string;
}

/**
 * @interface BookingStats
 * @description Estadísticas de reservas
 */
interface BookingStats {
  /** Total de reservas */
  total: number;
  /** Reservas confirmadas */
  confirmed: number;
  /** Reservas pendientes */
  pending: number;
  /** Reservas canceladas */
  cancelled: number;
  /** Reservas completadas */
  completed: number;
  /** Ingresos totales */
  totalRevenue: number;
  /** Ingresos del mes */
  monthlyRevenue: number;
}

/**
 * @class AdminBookingsComponent
 * @description Componente para la gestión administrativa de reservas.
 * Permite ver, filtrar, confirmar, cancelar y gestionar todas las reservas del sistema.
 * @implements {OnInit, OnDestroy}
 */
@Component({
  selector: 'app-admin-bookings',
  templateUrl: './admin-bookings.component.html',
  styleUrls: ['./admin-bookings.component.scss']
})
export class AdminBookingsComponent implements OnInit, OnDestroy {
  /** @property {Subject<void>} destroy$ - Subject para manejar la destrucción del componente */
  private destroy$ = new Subject<void>();

  /** @property {boolean} isLoading - Estado de carga */
  isLoading = true;

  /** @property {boolean} isLoadingAction - Estado de carga para acciones */
  isLoadingAction = false;

  /** @property {BookingResponse[]} bookings - Lista de reservas */
  bookings: BookingResponse[] = [];

  /** @property {PageResponse<BookingResponse>|null} bookingsPage - Información de paginación */
  bookingsPage: PageResponse<BookingResponse> | null = null;

  /** @property {BookingResponse|null} selectedBooking - Reserva seleccionada */
  selectedBooking: BookingResponse | null = null;

  /** @property {BookingStats} stats - Estadísticas de reservas */
  stats: BookingStats = {
    total: 0,
    confirmed: 0,
    pending: 0,
    cancelled: 0,
    completed: 0,
    totalRevenue: 0,
    monthlyRevenue: 0
  };

  /** @property {BookingFilters} filters - Filtros aplicados */
  filters: BookingFilters = {
    status: 'all',
    dateFrom: '',
    dateTo: '',
    searchTerm: '',
    hotelId: 'all'
  };

  /** @property {number} currentPage - Página actual */
  currentPage = 0;

  /** @property {number} pageSize - Tamaño de página */
  pageSize = 10;

  /** @property {string} sortBy - Campo de ordenamiento */
  sortBy = 'bookingDate';

  /** @property {string} sortDir - Dirección de ordenamiento */
  sortDir = 'desc';

  /** @property {string} errorMessage - Mensaje de error */
  errorMessage = '';

  /** @property {boolean} showFilters - Mostrar panel de filtros */
  showFilters = false;

  /** @property {BookingStatus} BookingStatus - Enum de estados para uso en template */
  BookingStatus = BookingStatus;

  /** @property {BookingResponse[]} filteredBookings - Lista de reservas filtradas */
  filteredBookings: BookingResponse[] = [];

  // Propiedades para filtros individuales (usadas en el template)
  /** @property {string} selectedStatus - Estado seleccionado en el filtro */
  selectedStatus = '';
  
  /** @property {string} dateFrom - Fecha desde para filtro */
  dateFrom = '';
  
  /** @property {string} dateTo - Fecha hasta para filtro */
  dateTo = '';
  
  /** @property {string} searchTerm - Término de búsqueda */
  searchTerm = '';

  /** @property {boolean} loading - Estado de carga general */
  loading = false;

  /** @property {any} statistics - Estadísticas para mostrar en dashboard */
  statistics: any = null;

  /** @property {Math} Math - Referencia a Math para usar en el template */
  Math = Math;

  /**
   * @constructor
   * @param {BookingService} bookingService - Servicio de reservas
   */
  constructor(private adminService: AdminService) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente
   */
  ngOnInit(): void {
    this.loadBookings();
  }

  /**
   * @method ngOnDestroy
   * @description Limpia las suscripciones al destruir el componente
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * @method loadBookings
   * @description Carga la lista de reservas según los filtros aplicados
   */
  loadBookings(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Usar los parámetros de paginación
    const request = this.adminService.getAllBookings(this.currentPage, this.pageSize, this.sortBy, this.sortDir);

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: PageResponse<BookingResponse>) => {
        this.bookingsPage = response;
        this.bookings = response.content;
        this.filteredBookings = [...this.bookings]; // Inicializar filteredBookings
        this.updateFilteredBookings(); // Aplicar filtros
        this.calculateStats(); // Calcular estadísticas después de cargar los datos
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error cargando reservas:', error);
        this.errorMessage = 'Error al cargar las reservas. Intente nuevamente.';
        this.isLoading = false;
      }
    });
  }

  /**
   * @method calculateStats
   * @description Calcula las estadísticas de reservas
   */
  calculateStats(): void {
    console.log('Calculando estadísticas para', this.bookings.length, 'reservas');
    
    // Esta sería una implementación real conectada al backend
    // Por ahora, calculamos estadísticas básicas desde los datos disponibles
    this.stats = {
      total: this.bookings.length,
      confirmed: this.bookings.filter(b => b.status === 'CONFIRMED').length,
      pending: this.bookings.filter(b => b.status === 'PENDING').length,
      cancelled: this.bookings.filter(b => b.status === 'CANCELLED').length,
      completed: this.bookings.filter(b => b.status === 'COMPLETED').length,
      totalRevenue: this.bookings.reduce((sum, b) => sum + (b.totalPrice || 0), 0),
      monthlyRevenue: this.calculateMonthlyRevenue()
    };

    // Actualizar statistics para el template
    this.statistics = {
      total: this.stats.total,
      confirmed: this.stats.confirmed,
      pending: this.stats.pending,
      cancelled: this.stats.cancelled,
      completed: this.stats.completed
    };
    
    console.log('Estadísticas calculadas:', this.statistics);
  }

  /**
   * @method calculateMonthlyRevenue
   * @description Calcula los ingresos del mes actual
   * @returns {number} Ingresos del mes
   */
  private calculateMonthlyRevenue(): number {
    const currentMonth = new Date().getMonth();
    const currentYear = new Date().getFullYear();
    
    return this.bookings
      .filter(booking => {
        const bookingDate = new Date(booking.bookingDate);
        return bookingDate.getMonth() === currentMonth && 
               bookingDate.getFullYear() === currentYear;
      })
      .reduce((sum, booking) => sum + (booking.totalPrice || 0), 0);
  }

  /**
   * @method onPageChange
   * @description Maneja el cambio de página
   * @param {number} page - Nueva página
   */
  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadBookings();
  }

  /**
   * @method onPageSizeChange
   * @description Maneja el cambio de tamaño de página
   * @param {number} newSize - Nuevo tamaño de página
   */
  onPageSizeChange(newSize: number): void {
    this.pageSize = newSize;
    this.currentPage = 0; // Resetear a la primera página
    this.loadBookings();
  }

  /**
   * @method applyFilters
   * @description Aplica los filtros y recarga las reservas
   */
  applyFilters(): void {
    // Sincronizar filters con las propiedades individuales
    this.filters.status = this.selectedStatus || 'all';
    this.filters.dateFrom = this.dateFrom;
    this.filters.dateTo = this.dateTo;
    this.filters.searchTerm = this.searchTerm;
    
    this.currentPage = 0;
    this.updateFilteredBookings();
  }

  /**
   * @method clearFilters
   * @description Limpia todos los filtros
   */
  clearFilters(): void {
    this.filters = {
      status: 'all',
      dateFrom: '',
      dateTo: '',
      searchTerm: '',
      hotelId: 'all'
    };
    
    // Limpiar también las propiedades individuales
    this.selectedStatus = '';
    this.dateFrom = '';
    this.dateTo = '';
    this.searchTerm = '';
    
    this.applyFilters();
  }

  /**
   * @method selectBooking
   * @description Selecciona una reserva para ver detalles
   * @param {BookingResponse} booking - Reserva seleccionada
   */
  selectBooking(booking: BookingResponse): void {
    this.selectedBooking = booking;
  }

  /**
   * @method confirmBooking
   * @description Confirma una reserva pendiente
   * @param {number} bookingId - ID de la reserva
   */
  confirmBooking(bookingId: number): void {
    Swal.fire({
      title: '¿Confirmar reserva?',
      text: 'Esta acción confirmará la reserva y notificará al cliente.',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Confirmar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#28a745'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoadingAction = true;

        this.adminService.updateBookingStatus(bookingId, 'CONFIRMED')
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = 'CONFIRMED' as BookingStatus;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = 'CONFIRMED' as BookingStatus;
              }

              this.isLoadingAction = false;
              this.calculateStats();

              Swal.fire({
                title: 'Reserva confirmada',
                text: 'La reserva ha sido confirmada exitosamente.',
                icon: 'success'
              });
            },
            error: (error) => {
              console.error('Error confirmando reserva:', error);
              this.isLoadingAction = false;
              Swal.fire({
                title: 'Error',
                text: 'No se pudo confirmar la reserva. Intente nuevamente.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method cancelBooking
   * @description Cancela una reserva
   * @param {number} bookingId - ID de la reserva
   */
  cancelBooking(bookingId: number): void {
    Swal.fire({
      title: '¿Cancelar reserva?',
      text: 'Esta acción cancelará la reserva y notificará al cliente.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Cancelar reserva',
      cancelButtonText: 'No cancelar',
      confirmButtonColor: '#dc3545'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoadingAction = true;

        this.adminService.updateBookingStatus(bookingId, 'CANCELLED')
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = 'CANCELLED' as BookingStatus;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = 'CANCELLED' as BookingStatus;
              }

              this.isLoadingAction = false;
              this.calculateStats();

              Swal.fire({
                title: 'Reserva cancelada',
                text: 'La reserva ha sido cancelada exitosamente.',
                icon: 'success'
              });
            },
            error: (error) => {
              console.error('Error cancelando reserva:', error);
              this.isLoadingAction = false;
              Swal.fire({
                title: 'Error',
                text: 'No se pudo cancelar la reserva. Intente nuevamente.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method completeBooking
   * @description Marca una reserva como completada
   * @param {number} bookingId - ID de la reserva
   */
  completeBooking(bookingId: number): void {
    Swal.fire({
      title: '¿Marcar como completada?',
      text: 'Esta acción marcará la reserva como completada.',
      icon: 'question',
      showCancelButton: true,
      confirmButtonText: 'Completar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#17a2b8'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoadingAction = true;

        this.adminService.updateBookingStatus(bookingId, 'COMPLETED')
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = 'COMPLETED' as BookingStatus;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = 'COMPLETED' as BookingStatus;
              }

              this.isLoadingAction = false;
              this.calculateStats();

              Swal.fire({
                title: 'Reserva completada',
                text: 'La reserva ha sido marcada como completada.',
                icon: 'success'
              });
            },
            error: (error) => {
              console.error('Error completando reserva:', error);
              this.isLoadingAction = false;
              Swal.fire({
                title: 'Error',
                text: 'No se pudo completar la reserva. Intente nuevamente.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method downloadVoucher
   * @description Descarga el voucher de una reserva
   * @param {number} bookingId - ID de la reserva
   */
  downloadVoucher(bookingId: number): void {
    this.adminService.downloadBookingVoucher(bookingId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `voucher-reserva-${bookingId}.pdf`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);

          Swal.fire({
            title: 'Éxito',
            text: 'Voucher descargado exitosamente.',
            icon: 'success',
            timer: 2000,
            showConfirmButton: false
          });
        },
        error: (error) => {
          console.error('Error descargando voucher:', error);
          Swal.fire({
            title: 'Error',
            text: 'No se pudo descargar el voucher. Intente nuevamente.',
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method exportBookings
   * @description Exporta las reservas a un archivo
   * @param {string} [format='xlsx'] - Formato de exportación
   */
  exportBookings(format: string = 'xlsx'): void {
    this.isLoadingAction = true;
    
    this.adminService.exportData(format, 'bookings')
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `reservas.${format}`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          this.isLoadingAction = false;
        },
        error: (error) => {
          console.error('Error exportando reservas:', error);
          this.isLoadingAction = false;
          Swal.fire({
            title: 'Error',
            text: 'No se pudo exportar las reservas. Intente nuevamente.',
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method refreshBookings
   * @description Recarga la lista de reservas
   */
  refreshBookings(): void {
    this.loadBookings();
    this.calculateStats();
  }

  /**
   * @method hasFiltersApplied
   * @description Verifica si hay filtros aplicados
   * @returns {boolean} True si hay filtros aplicados
   */
  hasFiltersApplied(): boolean {
    return this.filters.status !== 'all' ||
           this.filters.dateFrom !== '' ||
           this.filters.dateTo !== '' ||
           this.filters.searchTerm !== '' ||
           this.filters.hotelId !== 'all';
  }

  /**
   * @method goToPage
   * @description Navega a una página específica
   * @param {number} page - Número de página
   */
  goToPage(page: number): void {
    if (page < 0 || !this.bookingsPage || page >= this.bookingsPage.totalPages) {
      return;
    }
    this.currentPage = page;
    this.loadBookings();
  }

  /**
   * @method getPageNumbers
   * @description Obtiene los números de página para mostrar en la paginación
   * @returns {number[]} Array de números de página
   */
  getPageNumbers(): number[] {
    if (!this.bookingsPage) return [];
    
    const totalPages = this.bookingsPage.totalPages;
    const currentPage = this.bookingsPage.number;
    const pages: number[] = [];
    
    // Mostrar máximo 5 páginas
    const startPage = Math.max(0, currentPage - 2);
    const endPage = Math.min(totalPages - 1, startPage + 4);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  /**
   * @method getTotalPages
   * @description Obtiene el total de páginas
   * @returns {number} Total de páginas
   */
  getTotalPages(): number {
    return this.bookingsPage ? this.bookingsPage.totalPages : 0;
  }

  /**
   * @method trackByBookingId
   * @description Función de tracking para ngFor de reservas
   * @param {number} index - Índice del elemento
   * @param {BookingResponse} booking - Reserva
   * @returns {number} ID de la reserva
   */
  trackByBookingId(index: number, booking: BookingResponse): number {
    return booking.id;
  }

  /**
   * @method viewBookingDetails
   * @description Muestra los detalles de una reserva
   * @param {BookingResponse} booking - Reserva a mostrar
   */
  viewBookingDetails(booking: BookingResponse): void {
    this.selectedBooking = booking;
    // Mostrar modal con detalles
    const modalElement = document.getElementById('bookingDetailsModal');
    if (modalElement) {
      // Si usas Bootstrap, puedes usar esto:
      // const modal = new (window as any).bootstrap.Modal(modalElement);
      // modal.show();
      
      // Por ahora mostramos un SweetAlert con los detalles
      Swal.fire({
        title: `Reserva #${booking.id}`,
        html: `
          <div class="text-left">
            <p><strong>Cliente:</strong> ${booking.user.firstName} ${booking.user.lastName}</p>
            <p><strong>Email:</strong> ${booking.user.email}</p>
            <p><strong>Hotel:</strong> ${booking.hotel.name}</p>
            <p><strong>Tipo habitación:</strong> ${booking.roomType.name}</p>
            <p><strong>Check-in:</strong> ${new Date(booking.checkInDate).toLocaleDateString('es-ES')}</p>
            <p><strong>Check-out:</strong> ${new Date(booking.checkOutDate).toLocaleDateString('es-ES')}</p>
            <p><strong>Huéspedes:</strong> ${booking.numGuests}</p>
            <p><strong>Habitaciones:</strong> ${booking.numRooms}</p>
            <p><strong>Total:</strong> $${booking.totalPrice.toLocaleString('es-CO')}</p>
            <p><strong>Estado:</strong> ${this.getStatusText(booking.status)}</p>
            <p><strong>Código:</strong> ${booking.confirmationCode}</p>
            ${booking.specialRequests ? `<p><strong>Solicitudes especiales:</strong> ${booking.specialRequests}</p>` : ''}
          </div>
        `,
        width: '600px',
        confirmButtonText: 'Cerrar'
      });
    }
  }

  /**
   * @method getStatusText
   * @description Obtiene el texto en español para un estado de reserva
   * @param {string} status - Estado de la reserva
   * @returns {string} Texto del estado en español
   */
  getStatusText(status: string): string {
    switch (status) {
      case 'PENDING':
        return 'Pendiente';
      case 'CONFIRMED':
        return 'Confirmada';
      case 'COMPLETED':
        return 'Completada';
      case 'CANCELLED':
        return 'Cancelada';
      default:
        return status;
    }
  }

  /**
   * @method updateFilteredBookings
   * @description Actualiza la lista de reservas filtradas
   */
  private updateFilteredBookings(): void {
    this.filteredBookings = this.bookings.filter(booking => {
      // Filtro por término de búsqueda
      if (this.searchTerm) {
        const searchTerm = this.searchTerm.toLowerCase();
        const fullName = `${booking.user?.firstName || ''} ${booking.user?.lastName || ''}`.trim();
        const matchesSearch = 
          fullName.toLowerCase().includes(searchTerm) ||
          (booking.user?.email || '').toLowerCase().includes(searchTerm) ||
          booking.id.toString().includes(searchTerm) ||
          (booking.hotel?.name || '').toLowerCase().includes(searchTerm) ||
          (booking.confirmationCode || '').toLowerCase().includes(searchTerm);
        
        if (!matchesSearch) return false;
      }

      // Filtro por estado
      if (this.selectedStatus && this.selectedStatus !== 'all') {
        if (booking.status !== this.selectedStatus) return false;
      }

      // Filtro por fecha
      if (this.dateFrom) {
        const bookingDate = new Date(booking.bookingDate);
        const fromDate = new Date(this.dateFrom);
        if (bookingDate < fromDate) return false;
      }

      if (this.dateTo) {
        const bookingDate = new Date(booking.bookingDate);
        const toDate = new Date(this.dateTo);
        toDate.setHours(23, 59, 59, 999); // Final del día
        if (bookingDate > toDate) return false;
      }

      return true;
    });
  }

  /**
   * @method calculateNights
   * @description Calcula el número de noches entre dos fechas
   * @param {string} checkIn - Fecha de entrada
   * @param {string} checkOut - Fecha de salida
   * @returns {number} Número de noches
   */
  calculateNights(checkIn: string, checkOut: string): number {
    const startDate = new Date(checkIn);
    const endDate = new Date(checkOut);
    const diffTime = endDate.getTime() - startDate.getTime();
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }
}

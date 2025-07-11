import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { BookingService } from '../../../core/services/booking.service';
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

  /** @property {BookingStats|null} statistics - Estadísticas de reservas (alias para stats) */
  get statistics(): BookingStats | null {
    return this.stats;
  }

  /** @property {string} selectedStatus - Estado seleccionado para filtrar */
  selectedStatus = '';

  /** @property {string} dateFrom - Fecha desde para filtrar */
  dateFrom = '';

  /** @property {string} dateTo - Fecha hasta para filtrar */
  dateTo = '';

  /** @property {string} searchTerm - Término de búsqueda */
  searchTerm = '';

  /** @property {boolean} loading - Alias para isLoading */
  get loading(): boolean {
    return this.isLoading;
  }

  /**
   * @constructor
   * @param {BookingService} bookingService - Servicio de reservas
   */
  constructor(private bookingService: BookingService) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente
   */
  ngOnInit(): void {
    this.loadBookings();
    this.calculateStats();
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

    let request;

    if (this.filters.status !== 'all') {
      request = this.bookingService.getBookingsByStatus(
        this.filters.status as BookingStatus,
        this.currentPage,
        this.pageSize
      );
    } else {
      request = this.bookingService.getAllBookings(
        this.currentPage,
        this.pageSize,
        this.sortBy,
        this.sortDir
      );
    }

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response: PageResponse<BookingResponse>) => {
        this.bookingsPage = response;
        this.bookings = response.content;
        this.filteredBookings = [...this.bookings]; // Inicializar filteredBookings
        this.updateFilteredBookings(); // Aplicar filtros
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
    // Esta sería una implementación real conectada al backend
    // Por ahora, calculamos estadísticas básicas desde los datos disponibles
    this.stats = {
      total: this.bookings.length,
      confirmed: this.bookings.filter(b => b.status === BookingStatus.CONFIRMED).length,
      pending: this.bookings.filter(b => b.status === BookingStatus.PENDING).length,
      cancelled: this.bookings.filter(b => b.status === BookingStatus.CANCELLED).length,
      completed: this.bookings.filter(b => b.status === BookingStatus.COMPLETED).length,
      totalRevenue: this.bookings.reduce((sum, b) => sum + (b.totalPrice || 0), 0),
      monthlyRevenue: this.calculateMonthlyRevenue()
    };
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
   * @param {number} size - Nuevo tamaño
   */
  onPageSizeChange(size: number): void {
    this.pageSize = size;
    this.currentPage = 0;
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

        this.bookingService.confirmBooking(bookingId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = BookingStatus.CONFIRMED;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = BookingStatus.CONFIRMED;
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

        this.bookingService.cancelBooking(bookingId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = BookingStatus.CANCELLED;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = BookingStatus.CANCELLED;
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

        this.bookingService.completeBooking(bookingId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Actualizar el estado en la lista
              const bookingIndex = this.bookings.findIndex(b => b.id === bookingId);
              if (bookingIndex !== -1) {
                this.bookings[bookingIndex].status = BookingStatus.COMPLETED;
              }

              // Actualizar reserva seleccionada
              if (this.selectedBooking && this.selectedBooking.id === bookingId) {
                this.selectedBooking.status = BookingStatus.COMPLETED;
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
    this.bookingService.downloadVoucher(bookingId)
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
    
    this.bookingService.exportBookings(format)
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
    const currentPage = this.currentPage;
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
    // Aquí podrías abrir un modal o navegar a una página de detalles
  }

  /**
   * @method calculateNights
   * @description Calcula el número de noches de una reserva
   * @param {string} checkIn - Fecha de entrada
   * @param {string} checkOut - Fecha de salida
   * @returns {number} Número de noches
   */
  calculateNights(checkIn: string, checkOut: string): number {
    const checkInDate = new Date(checkIn);
    const checkOutDate = new Date(checkOut);
    const timeDiff = checkOutDate.getTime() - checkInDate.getTime();
    return Math.ceil(timeDiff / (1000 * 3600 * 24));
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
        const matchesSearch = 
          (booking.customerName || booking.user?.name || '').toLowerCase().includes(searchTerm) ||
          (booking.customerEmail || booking.user?.email || '').toLowerCase().includes(searchTerm) ||
          booking.id.toString().includes(searchTerm) ||
          (booking.hotel?.name || '').toLowerCase().includes(searchTerm);
        
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
        if (bookingDate > toDate) return false;
      }

      return true;
    });
  }

  /**
   * @method onSearchTermChange
   * @description Maneja el cambio en el término de búsqueda
   */
  onSearchTermChange(): void {
    this.updateFilteredBookings();
  }

  /**
   * @method onStatusFilterChange
   * @description Maneja el cambio en el filtro de estado
   */
  onStatusFilterChange(): void {
    this.updateFilteredBookings();
  }

  /**
   * @method onDateFilterChange
   * @description Maneja el cambio en los filtros de fecha
   */
  onDateFilterChange(): void {
    this.updateFilteredBookings();
  }

  /**
   * @method getStatusText
   * @description Obtiene el texto en español para un estado de reserva
   * @param {BookingStatus} status - Estado de la reserva
   * @returns {string} Texto en español del estado
   */
  getStatusText(status: BookingStatus): string {
    switch (status) {
      case BookingStatus.PENDING:
        return 'Pendiente';
      case BookingStatus.CONFIRMED:
        return 'Confirmada';
      case BookingStatus.CANCELLED:
        return 'Cancelada';
      case BookingStatus.COMPLETED:
        return 'Completada';
      default:
        return 'Desconocido';
    }
  }
}

import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { BookingService } from '../../core/services/booking.service';
import { BookingResponse } from '../../core/models/booking.model';
import { Subject, takeUntil } from 'rxjs';
import Swal from 'sweetalert2';

/**
 * @class BookingDetailComponent
 * @description Dedicated component for displaying comprehensive booking details.
 * Provides a full-page view with all booking information and actions.
 * @implements {OnInit, OnDestroy}
 */
@Component({
  selector: 'app-booking-detail',
  templateUrl: './booking-detail.component.html',
  styleUrls: ['./booking-detail.component.scss']
})
export class BookingDetailComponent implements OnInit, OnDestroy {
  /** @property {BookingResponse|null} booking - Current booking data */
  booking: BookingResponse | null = null;
  
  /** @property {boolean} isLoading - Loading state indicator */
  isLoading = true;
  
  /** @property {boolean} isDownloadingVoucher - Voucher download state */
  isDownloadingVoucher = false;
  
  /** @property {string} errorMessage - Error message if any */
  errorMessage = '';
  
  /** @property {Subject<void>} destroy$ - Subject for component cleanup */
  private destroy$ = new Subject<void>();

  /**
   * @constructor
   * @param {ActivatedRoute} route - Angular route service
   * @param {Router} router - Angular router service
   * @param {BookingService} bookingService - Booking service
   */
  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private bookingService: BookingService
  ) {}

  /**
   * @method ngOnInit
   * @description Initialize component and load booking data
   */
  ngOnInit(): void {
    this.loadBookingDetails();
  }

  /**
   * @method ngOnDestroy
   * @description Cleanup component subscriptions
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * @method loadBookingDetails
   * @private
   * @description Load booking details from route parameter
   */
  private loadBookingDetails(): void {
    const bookingId = this.route.snapshot.paramMap.get('id');
    
    if (!bookingId || isNaN(Number(bookingId))) {
      this.errorMessage = 'ID de reserva inválido';
      this.isLoading = false;
      return;
    }

    this.isLoading = true;
    this.bookingService.getBookingById(Number(bookingId))
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (booking) => {
          this.booking = booking;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error loading booking details:', error);
          this.errorMessage = 'No se pudo cargar la información de la reserva';
          this.isLoading = false;
          
          // Show error and redirect after delay
          Swal.fire({
            title: 'Error',
            text: 'No se pudo cargar la información de la reserva',
            icon: 'error',
            confirmButtonText: 'Volver a Reservas'
          }).then(() => {
            this.router.navigate(['/bookings']);
          });
        }
      });
  }

  /**
   * @method downloadVoucher
   * @description Download booking voucher PDF
   */
  downloadVoucher(): void {
    if (!this.booking) return;

    this.isDownloadingVoucher = true;
    
    // For now, show a placeholder implementation
    // TODO: Implement actual voucher download when backend is ready
    setTimeout(() => {
      this.isDownloadingVoucher = false;
      Swal.fire({
        title: 'Próximamente',
        text: 'La funcionalidad de descarga de vouchers estará disponible pronto.',
        icon: 'info',
        confirmButtonText: 'Entendido'
      });
    }, 1000);
  }

  /**
   * @method shareBooking
   * @description Share booking details via Web Share API or copy link
   */
  shareBooking(): void {
    if (!this.booking) return;

    const shareData = {
      title: `Reserva #${this.booking.id} - Caribe Vibes`,
      text: `Detalles de mi reserva en ${this.booking.hotel?.name || 'Hotel'}`,
      url: window.location.href
    };

    if (navigator.share) {
      navigator.share(shareData).catch(console.error);
    } else {
      // Fallback: copy to clipboard
      navigator.clipboard.writeText(window.location.href).then(() => {
        Swal.fire({
          title: 'Enlace copiado',
          text: 'El enlace de la reserva ha sido copiado al portapapeles',
          icon: 'success',
          timer: 2000,
          showConfirmButton: false
        });
      }).catch(() => {
        Swal.fire({
          title: 'Error',
          text: 'No se pudo copiar el enlace',
          icon: 'error',
          confirmButtonText: 'Aceptar'
        });
      });
    }
  }

  /**
   * @method cancelBooking
   * @description Cancel the current booking
   */
  cancelBooking(): void {
    if (!this.booking) return;

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
      if (result.isConfirmed && this.booking) {
        this.bookingService.cancelBooking(this.booking.id)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              Swal.fire({
                title: '¡Cancelada!',
                text: 'Tu reserva ha sido cancelada exitosamente.',
                icon: 'success',
                confirmButtonText: 'Aceptar'
              }).then(() => {
                this.router.navigate(['/bookings']);
              });
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

  /**
   * @method goBack
   * @description Navigate back to bookings list
   */
  goBack(): void {
    this.router.navigate(['/bookings']);
  }

  /**
   * @method formatDate
   * @description Format date for display
   * @param {string | Date} date - Date to format
   * @returns {string} Formatted date string
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
   * @description Format amount as currency
   * @param {number} amount - Amount to format
   * @returns {string} Formatted currency string
   */
  formatCurrency(amount: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'COP'
    }).format(amount);
  }

  /**
   * @method getStatusClass
   * @description Get CSS class for booking status
   * @param {string} status - Booking status
   * @returns {string} CSS class name
   */
  getStatusClass(status: string): string {
    switch (status.toLowerCase()) {
      case 'confirmed':
        return 'status-confirmed';
      case 'pending':
        return 'status-pending';
      case 'cancelled':
        return 'status-cancelled';
      case 'completed':
        return 'status-completed';
      default:
        return 'status-default';
    }
  }

  /**
   * @method getStatusText
   * @description Get localized status text
   * @param {string} status - Booking status
   * @returns {string} Localized status text
   */
  getStatusText(status: string): string {
    const statusMap: { [key: string]: string } = {
      'PENDING': 'Pendiente',
      'CONFIRMED': 'Confirmada',
      'CANCELLED': 'Cancelada',
      'COMPLETED': 'Completada'
    };
    return statusMap[status] || status;
  }

  /**
   * @method calculateNights
   * @description Calculate number of nights for the booking
   * @returns {number} Number of nights
   */
  calculateNights(): number {
    if (!this.booking) return 0;
    
    const checkIn = new Date(this.booking.checkInDate);
    const checkOut = new Date(this.booking.checkOutDate);
    const diffTime = Math.abs(checkOut.getTime() - checkIn.getTime());
    return Math.ceil(diffTime / (1000 * 60 * 60 * 24));
  }

  /**
   * @method canCancelBooking
   * @description Check if booking can be cancelled
   * @returns {boolean} True if booking can be cancelled
   */
  canCancelBooking(): boolean {
    if (!this.booking) return false;
    
    return this.booking.status === 'PENDING' || this.booking.status === 'CONFIRMED';
  }
}

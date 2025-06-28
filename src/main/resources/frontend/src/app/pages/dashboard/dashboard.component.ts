import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../../core/services/auth.service';
import { BookingService } from '../../core/services/booking.service';
import { DestinationService } from '../../core/services/destination.service';
import { HotelService } from '../../core/services/hotel.service';
import { User } from '../../core/models/user.model';
import { BookingResponse } from '../../core/models/booking.model';
import { DestinationResponse } from '../../core/models/destination.model';
import { HotelResponse } from '../../core/models/hotel.model';
import { Observable, forkJoin } from 'rxjs';
import { map } from 'rxjs/operators';

/**
 * @interface DashboardStats
 * @description Estadísticas para mostrar en el panel de control del usuario
 */
interface DashboardStats {
  /** @property {number} totalBookings - Total de reservas realizadas */
  totalBookings: number;
  /** @property {number} activeBookings - Reservas activas/confirmadas */
  activeBookings: number;
  /** @property {number} completedBookings - Reservas completadas */
  completedBookings: number;
  /** @property {number} cancelledBookings - Reservas canceladas */
  cancelledBookings: number;
}

/**
 * @class DashboardComponent
 * @description Componente principal del panel de control del usuario.
 * Muestra información personalizada como estadísticas de reservas, 
 * reservas recientes, destinos favoritos y hoteles recomendados.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-dashboard',
  templateUrl: './dashboard.component.html',
  styleUrls: ['./dashboard.component.scss']
})
export class DashboardComponent implements OnInit {
  /** @property {User|null} currentUser - Usuario actual autenticado */
  currentUser: User | null = null;
  
  /** @property {BookingResponse[]} recentBookings - Reservas recientes del usuario */
  recentBookings: BookingResponse[] = [];
  
  /** @property {DestinationResponse[]} favoriteDestinations - Destinos favoritos para mostrar */
  favoriteDestinations: DestinationResponse[] = [];
  
  /** @property {HotelResponse[]} recommendedHotels - Hoteles recomendados para el usuario */
  recommendedHotels: HotelResponse[] = [];
  
  /** @property {DashboardStats} stats - Estadísticas de reservas del usuario */
  stats: DashboardStats = {
    totalBookings: 0,
    activeBookings: 0,
    completedBookings: 0,
    cancelledBookings: 0
  };
  
  /** @property {boolean} isLoading - Indicador de estado de carga de datos */
  isLoading = true;
  
  /**
   * @constructor
   * @param {AuthService} authService - Servicio de autenticación
   * @param {BookingService} bookingService - Servicio de reservas
   * @param {DestinationService} destinationService - Servicio de destinos
   * @param {HotelService} hotelService - Servicio de hoteles
   * @param {Router} router - Servicio de navegación
   */
  constructor(
    private authService: AuthService,
    private bookingService: BookingService,
    private destinationService: DestinationService,
    private hotelService: HotelService,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente obteniendo el usuario actual y cargando datos del dashboard
   */
  ngOnInit(): void {
    this.currentUser = this.authService.getCurrentUser();
    this.loadDashboardData();
  }

  /**
   * @method loadDashboardData
   * @private
   * @description Carga todos los datos necesarios para el dashboard usando forkJoin para solicitudes paralelas
   */
  private loadDashboardData(): void {
    this.isLoading = true;
    
    forkJoin({
      bookings: this.bookingService.getMyBookings(),
      destinations: this.destinationService.getAllDestinations(),
      hotels: this.hotelService.getAllHotels()
    }).subscribe({
      next: (data) => {
        this.processBookingsData(data.bookings.content);
        this.favoriteDestinations = data.destinations.content.slice(0, 3);
        this.recommendedHotels = data.hotels.content.slice(0, 3);
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error loading dashboard data:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * @method processBookingsData
   * @private
   * @description Procesa los datos de reservas para obtener las más recientes y calcular estadísticas
   * @param {BookingResponse[]} bookings - Lista de todas las reservas del usuario
   */
  private processBookingsData(bookings: BookingResponse[]): void {
    this.recentBookings = bookings
      .sort((a, b) => new Date(b.bookingDate).getTime() - new Date(a.bookingDate).getTime())
      .slice(0, 5);

    this.stats = {
      totalBookings: bookings.length,
      activeBookings: bookings.filter(b => b.status === 'CONFIRMED').length,
      completedBookings: bookings.filter(b => b.status === 'COMPLETED').length,
      cancelledBookings: bookings.filter(b => b.status === 'CANCELLED').length
    };
  }

  /**
   * @method getWelcomeMessage
   * @description Devuelve un saludo personalizado según la hora del día
   * @returns {string} Saludo personalizado
   */
  getWelcomeMessage(): string {
    const hour = new Date().getHours();
    if (hour < 12) return 'Buenos días';
    if (hour < 18) return 'Buenas tardes';
    return 'Buenas noches';
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
   * @description Traduce el código de estado a texto legible en español
   * @param {string} status - Estado de la reserva en formato código
   * @returns {string} Texto legible del estado
   */
  getStatusText(status: string): string {
    switch (status.toLowerCase()) {
      case 'confirmed':
        return 'Confirmada';
      case 'pending':
        return 'Pendiente';
      case 'cancelled':
        return 'Cancelada';
      case 'completed':
        return 'Completada';
      default:
        return status;
    }
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
      month: 'short',
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
   * @method refreshData
   * @description Actualiza todos los datos del dashboard desde el servidor
   */
  refreshData(): void {
    this.loadDashboardData();
  }

  /**
   * @method navigateToDestination
   * @description Navega a la página de detalle de un destino específico
   * @param {number} destinationId - ID del destino a navegar
   */
  navigateToDestination(destinationId: number): void {
    this.router.navigate(['/destinations', destinationId]);
  }

  /**
   * @method navigateToHotel
   * @description Navega a la página de detalle de un hotel específico
   * @param {number} hotelId - ID del hotel a navegar
   */
  navigateToHotel(hotelId: number): void {
    this.router.navigate(['/hotels', hotelId]);
  }
}

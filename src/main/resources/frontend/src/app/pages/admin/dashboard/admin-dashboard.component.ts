import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil, filter, take } from 'rxjs/operators';
import { AdminService } from '../../../core/services/admin.service';
import { ContactService } from '../../../core/services/contact.service';
import { BookingService } from '../../../core/services/booking.service';
import { AuthService } from '../../../core/services/auth.service';
import Swal from 'sweetalert2';

/**
 * @interface DashboardCard
 * @description Representa una tarjeta de estadística en el dashboard
 */
interface DashboardCard {
  /** Título de la tarjeta */
  title: string;
  /** Valor principal a mostrar */
  value: number;
  /** Cambio porcentual respecto al período anterior */
  change: number;
  /** Icono a mostrar */
  icon: string;
  /** Color del tema */
  color: string;
  /** Descripción adicional */
  description: string;
}

/**
 * @class AdminDashboardComponent
 * @description Componente del dashboard principal de administración.
 * Muestra estadísticas generales del sistema, resúmenes de actividad y accesos rápidos.
 * @implements {OnInit, OnDestroy}
 */
@Component({
  selector: 'app-admin-dashboard',
  templateUrl: './admin-dashboard.component.html',
  styleUrls: ['./admin-dashboard.component.scss']
})
export class AdminDashboardComponent implements OnInit, OnDestroy {
  /** @property {typeof Math} Math - Referencia a Math para usar en el template */
  Math = Math;

  /** @property {Subject<void>} destroy$ - Subject para manejar la destrucción del componente */
  private destroy$ = new Subject<void>();

  /** @property {boolean} isLoading - Estado de carga */
  isLoading = true;

  /** @property {boolean} isLoadingCache - Estado de carga de operaciones de caché */
  isLoadingCache = false;

  /** @property {any} contactStats - Estadísticas de contactos */
  contactStats: any = null;

  /** @property {any} cacheInfo - Información del sistema de caché */
  cacheInfo: any = null;

  /** @property {DashboardCard[]} dashboardCards - Tarjetas de estadísticas para mostrar */
  dashboardCards: DashboardCard[] = [];

  /** @property {any[]} recentActivities - Actividades recientes del sistema */
  recentActivities: any[] = [];

  /** @property {string} errorMessage - Mensaje de error si ocurre algún problema */
  errorMessage = '';

  /**
   * @constructor
   * @param {AdminService} adminService - Servicio de administración
   * @param {ContactService} contactService - Servicio de contactos
   * @param {BookingService} bookingService - Servicio de reservas
   * @param {AuthService} authService - Servicio de autenticación
   */
  constructor(
    private adminService: AdminService,
    private contactService: ContactService,
    private bookingService: BookingService,
    private authService: AuthService
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente cargando las estadísticas
   */
  ngOnInit(): void {
    // Verificar que el usuario esté autenticado y sea admin antes de cargar datos
    if (this.authService.isAuthenticated() && this.authService.hasValidToken() && this.authService.isAdmin()) {
      console.log('Admin Dashboard: User authenticated and is admin, loading data');
      this.loadDashboardData();
    } else {
      console.log('Admin Dashboard: Waiting for admin auth state');
      // Esperar a que el estado de autenticación se establezca
      this.authService.currentUser$.pipe(
        filter((user: any) => user !== null && this.authService.isAdmin()),
        take(1),
        takeUntil(this.destroy$)
      ).subscribe({
        next: (user: any) => {
          console.log('Admin Dashboard: Admin auth state confirmed, loading data');
          this.loadDashboardData();
        },
        error: (error: any) => {
          console.error('Admin Dashboard: Error waiting for admin auth state:', error);
          this.authService.logout();
        }
      });
    }
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
   * @method loadDashboardData
   * @description Carga todos los datos necesarios para el dashboard
   */
  private loadDashboardData(): void {
    this.isLoading = true;
    this.errorMessage = '';

    // Cargar estadísticas del sistema (nuevo endpoint unificado)
    this.adminService.getSystemStatistics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.contactStats = stats.contacts;
          this.buildDashboardCards(stats);
        },
        error: (error) => {
          console.error('Error cargando estadísticas del sistema:', error);
          this.errorMessage = 'Error cargando estadísticas del sistema';
          this.isLoading = false;
        }
      });

    // Cargar información de caché
    this.adminService.getCacheInfo()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (info) => {
          this.cacheInfo = info;
          this.isLoading = false;
        },
        error: (error) => {
          console.error('Error cargando información de caché:', error);
          this.isLoading = false;
        }
      });
  }

  /**
   * @method buildDashboardCards
   * @description Construye las tarjetas de estadísticas basadas en los datos del sistema
   */
  /**
   * @method buildDashboardCards
   * @description Construye las tarjetas de estadísticas basadas en los datos disponibles
   * @param systemStats Estadísticas del sistema (opcional)
   */
  private buildDashboardCards(systemStats?: any): void {
    if (!this.contactStats && !systemStats) return;

    const contacts = systemStats?.contacts || this.contactStats;

    this.dashboardCards = [
      {
        title: 'Total Contactos',
        value: contacts?.total || 0,
        change: 0,
        icon: 'fas fa-envelope',
        color: 'primary',
        description: 'Mensajes de contacto totales'
      },
      {
        title: 'Mensajes Nuevos',
        value: contacts?.new || 0,
        change: 0,
        icon: 'fas fa-envelope-open',
        color: 'warning',
        description: 'Mensajes sin leer'
      },
      {
        title: 'Total Reservas',
        value: systemStats?.totalBookings || 0,
        change: 0,
        icon: 'fas fa-calendar-check',
        color: 'success',
        description: 'Reservas totales del sistema'
      },
      {
        title: 'Total Hoteles',
        value: systemStats?.totalHotels || 0,
        change: 0,
        icon: 'fas fa-hotel',
        color: 'info',
        description: 'Hoteles disponibles'
      }
    ];
  }

  /**
   * @method calculateUserGrowth
   * @description Calcula el crecimiento de usuarios (placeholder)
   * @returns {number} Porcentaje de crecimiento
   */
  private calculateUserGrowth(): number {
    // Implementar lógica real cuando esté disponible en el backend
    return Math.floor(Math.random() * 20) - 10;
  }

  /**
   * @method calculateBookingGrowth
   * @description Calcula el crecimiento de reservas (placeholder)
   * @returns {number} Porcentaje de crecimiento
   */
  private calculateBookingGrowth(): number {
    // Implementar lógica real cuando esté disponible en el backend
    return Math.floor(Math.random() * 30) - 15;
  }

  /**
   * @method calculateMonthlyBookingGrowth
   * @description Calcula el crecimiento mensual de reservas (placeholder)
   * @returns {number} Porcentaje de crecimiento
   */
  private calculateMonthlyBookingGrowth(): number {
    // Implementar lógica real cuando esté disponible en el backend
    return Math.floor(Math.random() * 25) - 10;
  }

  /**
   * @method clearAllCaches
   * @description Limpia todos los cachés del sistema
   */
  clearAllCaches(): void {
    Swal.fire({
      title: '¿Limpiar todos los cachés?',
      text: 'Esta acción limpiará todos los cachés del sistema. Esto puede afectar temporalmente el rendimiento.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Sí, limpiar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoadingCache = true;
        
        this.adminService.clearAllCaches()
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (response) => {
              this.isLoadingCache = false;
              Swal.fire({
                title: 'Cachés limpiados',
                text: `Se limpiaron ${response.clearedCaches || 0} cachés exitosamente.`,
                icon: 'success'
              });
              this.loadDashboardData(); // Recargar datos
            },
            error: (error) => {
              this.isLoadingCache = false;
              console.error('Error limpiando cachés:', error);
              Swal.fire({
                title: 'Error',
                text: 'Ocurrió un error al limpiar los cachés. Intente nuevamente.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method runSystemMaintenance
   * @description Ejecuta tareas de mantenimiento del sistema
   */
  runSystemMaintenance(): void {
    Swal.fire({
      title: '¿Ejecutar mantenimiento del sistema?',
      text: 'Esta acción ejecutará tareas de limpieza y optimización del sistema.',
      icon: 'info',
      showCancelButton: true,
      confirmButtonText: 'Ejecutar',
      cancelButtonText: 'Cancelar'
    }).then((result) => {
      if (result.isConfirmed) {
        this.isLoading = true;
        
        this.adminService.runSystemMaintenance()
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: (response) => {
              this.isLoading = false;
              Swal.fire({
                title: 'Mantenimiento completado',
                text: 'Las tareas de mantenimiento se ejecutaron exitosamente.',
                icon: 'success'
              });
              this.loadDashboardData(); // Recargar datos
            },
            error: (error) => {
              this.isLoading = false;
              console.error('Error en mantenimiento:', error);
              Swal.fire({
                title: 'Error',
                text: 'Ocurrió un error durante el mantenimiento. Intente nuevamente.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method refreshData
   * @description Recarga todos los datos del dashboard
   */
  refreshData(): void {
    this.loadDashboardData();
  }

  /**
   * @method exportSystemData
   * @description Exporta datos del sistema en el formato especificado
   * @param {string} format - Formato de exportación (csv, xlsx, json)
   * @param {string} dataType - Tipo de datos a exportar
   */
  exportSystemData(format: string, dataType: string): void {
    this.adminService.exportData(format, dataType)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (blob: Blob) => {
          const url = window.URL.createObjectURL(blob);
          const link = document.createElement('a');
          link.href = url;
          link.download = `${dataType}_export.${format}`;
          document.body.appendChild(link);
          link.click();
          document.body.removeChild(link);
          window.URL.revokeObjectURL(url);
          
          Swal.fire({
            title: 'Exportación completada',
            text: `Datos de ${dataType} exportados exitosamente.`,
            icon: 'success'
          });
        },
        error: (error) => {
          console.error('Error exportando datos:', error);
          Swal.fire({
            title: 'Error',
            text: 'Ocurrió un error al exportar los datos. Intente nuevamente.',
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method getChangeIcon
   * @description Obtiene el icono apropiado según el cambio porcentual
   * @param {number} change - Cambio porcentual
   * @returns {string} Clase del icono
   */
  getChangeIcon(change: number): string {
    if (change > 0) return 'fas fa-arrow-up';
    if (change < 0) return 'fas fa-arrow-down';
    return 'fas fa-minus';
  }

  /**
   * @method getChangeClass
   * @description Obtiene la clase CSS apropiada según el cambio porcentual
   * @param {number} change - Cambio porcentual
   * @returns {string} Clase CSS
   */
  getChangeClass(change: number): string {
    if (change > 0) return 'text-success';
    if (change < 0) return 'text-danger';
    return 'text-muted';
  }
}

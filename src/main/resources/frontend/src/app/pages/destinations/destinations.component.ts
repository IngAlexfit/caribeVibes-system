import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { DestinationService } from '../../core/services/destination.service';
import { HotelService } from '../../core/services/hotel.service';
import { DestinationResponse } from '../../core/models/destination.model';
import { HotelResponse } from '../../core/models/hotel.model';
import Swal from 'sweetalert2';

/**
 * @class DestinationsComponent
 * @description Componente para mostrar los destinos turísticos disponibles.
 * Incluye funcionalidad para ver la lista completa, filtrar, y ver detalles de un destino específico.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-destinations',
  templateUrl: './destinations.component.html',
  styleUrls: ['./destinations.component.scss']
})
export class DestinationsComponent implements OnInit {
  /** @property {DestinationResponse[]} destinations - Lista completa de destinos */
  destinations: DestinationResponse[] = [];
  
  /** @property {DestinationResponse[]} filteredDestinations - Lista de destinos filtrados */
  filteredDestinations: DestinationResponse[] = [];
  
  /** @property {DestinationResponse|null} selectedDestination - Destino seleccionado para vista de detalle */
  selectedDestination: DestinationResponse | null = null;
  
  /** @property {boolean} isLoading - Indicador de estado de carga de datos */
  isLoading = true;
  
  /** @property {boolean} isDetailView - Indica si se muestra vista de detalle o lista */
  isDetailView = false;
  
  /** @property {string} searchTerm - Término de búsqueda para filtrar destinos */
  searchTerm = '';
  
  /** @property {string} selectedCategory - Categoría seleccionada para filtrado */
  selectedCategory = '';
  
  /** @property {string[]} categories - Lista de categorías de destinos disponibles */
  categories = ['Playa', 'Montaña', 'Ciudad', 'Aventura', 'Cultura', 'Naturaleza'];

  /** @property {boolean} showBookingModal - Controla la visibilidad del modal de reserva */
  showBookingModal = false;

  /** @property {HotelResponse|null} selectedHotelForBooking - Hotel seleccionado para reservar */
  selectedHotelForBooking: HotelResponse | null = null;

  /**
   * @constructor
   * @param {DestinationService} destinationService - Servicio para obtener datos de destinos
   * @param {HotelService} hotelService - Servicio para obtener datos de hoteles
   * @param {ActivatedRoute} route - Servicio para acceder a parámetros de la ruta
   * @param {Router} router - Servicio para navegación entre rutas
   */
  constructor(
    private destinationService: DestinationService,
    private hotelService: HotelService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente determinando si se debe mostrar la lista o el detalle
   */
  ngOnInit(): void {
    // Verificar si estamos en vista de detalle
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      
      if (id) {
        this.isDetailView = true;
        this.loadDestinationDetail(+id);
      } else {
        this.isDetailView = false;
        this.loadDestinations();
      }
    });
  }

  /**
   * @method loadDestinations
   * @private
   * @description Carga todos los destinos disponibles desde el servicio
   */
  private loadDestinations(): void {
    this.isLoading = true;
      this.destinationService.getAllDestinations().subscribe({
      next: (response) => {
        this.destinations = response.content;
        this.filteredDestinations = response.content;
        this.isLoading = false;
      },      error: (error: any) => {
        console.error('Error loading destinations:', error);
        this.isLoading = false;
      }
    });
  }

  /**
   * @method loadDestinationDetail
   * @private
   * @description Carga los detalles de un destino específico
   * @param {number} id - ID del destino a cargar
   */
  private loadDestinationDetail(id: number): void {
    this.isLoading = true;
    
    this.destinationService.getDestinationById(id).subscribe({
      next: (destination) => {
        this.selectedDestination = destination;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading destination detail:', error);
        this.isLoading = false;
        // Redirigir a la lista si no se encuentra el destino
        this.router.navigate(['/destinations']);
      }
    });
  }

  /**
   * @method onSearch
   * @description Maneja el evento de búsqueda por texto
   */
  onSearch(): void {
    this.filterDestinations();
  }

  /**
   * @method onCategoryChange
   * @description Maneja el evento de cambio de categoría
   */
  onCategoryChange(): void {
    this.filterDestinations();
  }

  /**
   * @method filterDestinations
   * @private
   * @description Filtra los destinos según los criterios de búsqueda y categoría
   */
  private filterDestinations(): void {
    this.filteredDestinations = this.destinations.filter(destination => {
      const matchesSearch = destination.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           destination.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      // For now, we'll comment out category filtering until we add it to the model
      // const matchesCategory = !this.selectedCategory || destination.category === this.selectedCategory;
      const matchesCategory = true; // Placeholder until category is added to model
      
      return matchesSearch && matchesCategory;
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
    this.searchTerm = '';
    this.selectedCategory = '';
    this.filteredDestinations = this.destinations;
  }

  /**
   * @method goBackToList
   * @description Navega de regreso a la vista de lista de destinos
   */
  goBackToList(): void {
    this.router.navigate(['/destinations']);
  }

  /**
   * @method viewDestinationDetail
   * @description Navega a la vista de detalle de un destino específico
   * @param {number} destinationId - ID del destino a visualizar
   */
  viewDestinationDetail(destinationId: number): void {
    this.router.navigate(['/destinations', destinationId]);
  }

  /**
   * @method viewDestinationHotels
   * @description Navega a la página de hoteles filtrados por el destino actual
   * @param {number} destinationId - ID del destino cuyos hoteles se quieren ver
   */
  viewDestinationHotels(destinationId: number): void {
    this.router.navigate(['/hotels'], { 
      queryParams: { destinationId: destinationId } 
    });
  }

  /**
   * @method bookDestinationDirect
   * @description Inicia el proceso de reserva para un destino buscando el mejor hotel disponible
   * @param {number} destinationId - ID del destino a reservar
   */
  bookDestinationDirect(destinationId: number): void {
    // Buscar hoteles en el destino y mostrar el mejor disponible para reservar
    this.hotelService.getHotelsByDestination(destinationId).subscribe({
      next: (response) => {
        if (response.content && response.content.length > 0) {
          // Seleccionar el hotel con mejor rating o el primero disponible
          const bestHotel = response.content.reduce((best, current) => 
            current.rating > best.rating ? current : best
          );
          this.selectedHotelForBooking = bestHotel;
          this.showBookingModal = true;
        } else {
          // Si no hay hoteles, redirigir a la página de hoteles para ese destino
          this.viewDestinationHotels(destinationId);
        }
      },
      error: (error) => {
        console.error('Error loading hotels for destination:', error);
        // En caso de error, redirigir a hoteles
        this.viewDestinationHotels(destinationId);
      }
    });
  }

  /**
   * @method closeBookingModal
   * @description Cierra el modal de reserva
   */
  closeBookingModal(): void {
    this.showBookingModal = false;
    this.selectedHotelForBooking = null;
  }

  /**
   * @method onBookingCreated
   * @description Maneja el evento de reserva creada exitosamente
   * @param {any} booking - Datos de la reserva creada
   */
  onBookingCreated(booking: any): void {
    console.log('Reserva creada exitosamente:', booking);
    // Mostrar mensaje de éxito con SweetAlert
    Swal.fire({
      icon: 'success',
      title: '¡Reserva Creada!',
      text: 'Tu reserva ha sido creada exitosamente. Te redirigiremos a tus reservas.',
      confirmButtonText: 'Ver mis reservas',
      confirmButtonColor: '#007bff',
      timer: 3000,
      timerProgressBar: true
    }).then(() => {
      // Redirigir a la página de reservas
      this.router.navigate(['/bookings']);
    });
  }
}

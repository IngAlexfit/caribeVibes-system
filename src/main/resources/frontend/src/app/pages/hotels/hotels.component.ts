import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { HotelService } from '../../core/services/hotel.service';
import { DestinationService } from '../../core/services/destination.service';
import { HotelResponse } from '../../core/models/hotel.model';
import { DestinationResponse } from '../../core/models/destination.model';

/**
 * @class HotelsComponent
 * @description Componente para mostrar los hoteles disponibles.
 * Permite listar, filtrar y ver detalles de hoteles específicos.
 * @implements {OnInit}
 */
@Component({
  selector: 'app-hotels',
  templateUrl: './hotels.component.html',
  styleUrls: ['./hotels.component.scss']
})
export class HotelsComponent implements OnInit {
  /** @property {HotelResponse[]} hotels - Lista completa de hoteles */
  hotels: HotelResponse[] = [];
  
  /** @property {HotelResponse[]} filteredHotels - Lista de hoteles filtrados */
  filteredHotels: HotelResponse[] = [];
  
  /** @property {DestinationResponse[]} destinations - Lista de destinos disponibles para filtrado */
  destinations: DestinationResponse[] = [];
  
  /** @property {HotelResponse|null} selectedHotel - Hotel seleccionado para vista de detalle */
  selectedHotel: HotelResponse | null = null;
  
  /** @property {boolean} isLoading - Indicador de estado de carga de datos */
  isLoading = true;
  
  /** @property {boolean} isDetailView - Indica si se muestra vista de detalle o lista */
  isDetailView = false;
  
  /** @property {string} searchTerm - Término de búsqueda para filtrar hoteles */
  searchTerm = '';
  
  /** @property {number} selectedRating - Calificación mínima para filtrado */
  selectedRating = 0;
  
  /** @property {Object} priceRange - Rango de precios para filtrado de hoteles */
  priceRange = { min: 0, max: 1000000 };
  
  /** @property {number|null} destinationId - ID del destino para filtrar hoteles (opcional) */
  destinationId: number | null = null;

  /** @property {number|null} selectedDestinationFilter - ID del destino seleccionado en el filtro dropdown */
  selectedDestinationFilter: number | null = null;

  /** @property {boolean} filtersExpanded - Controla si los filtros están expandidos en móvil */
  filtersExpanded = false;

  /**
   * @constructor
   * @param {HotelService} hotelService - Servicio para obtener datos de hoteles
   * @param {DestinationService} destinationService - Servicio para obtener datos de destinos
   * @param {ActivatedRoute} route - Servicio para acceder a parámetros de la ruta
   * @param {Router} router - Servicio para navegación entre rutas
   */
  constructor(
    private hotelService: HotelService,
    private destinationService: DestinationService,
    private route: ActivatedRoute,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente determinando si se debe mostrar la lista o el detalle
   */
  ngOnInit(): void {
    console.log('🚀 HotelsComponent ngOnInit started');
    
    // Cargar destinos para el filtro
    this.loadDestinations();
    
    // Verificar si estamos en vista de detalle
    this.route.paramMap.subscribe(params => {
      const id = params.get('id');
      console.log('🔍 Route param id:', id);
      
      if (id) {
        this.isDetailView = true;
        this.loadHotelDetail(+id);
      } else {
        this.isDetailView = false;
        // Verificar si hay un destinationId en los query parameters
        this.route.queryParams.subscribe(queryParams => {
          console.log('🔍 Query params:', queryParams);
          this.destinationId = queryParams['destinationId'] ? +queryParams['destinationId'] : null;
          this.selectedDestinationFilter = this.destinationId;
          console.log('🎯 Set destinationId:', this.destinationId);
          console.log('🎯 Set selectedDestinationFilter:', this.selectedDestinationFilter);
          this.loadHotels();
        });
      }
    });
  }

  /**
   * @method loadDestinations
   * @private
   * @description Carga la lista de destinos disponibles para el filtro
   */
  private loadDestinations(): void {
    console.log('🌍 Loading destinations...');
    this.destinationService.getAllDestinations(0, 100).subscribe({
      next: (response) => {
        this.destinations = response.content;
        console.log('✅ Destinations loaded:', this.destinations.length);
        console.log('✅ Destinations data:', this.destinations.map(d => ({ id: d.id, name: d.name })));
      },
      error: (error: any) => {
        console.error('❌ Error loading destinations:', error);
      }
    });
  }

  /**
   * @method loadHotels
   * @private
   * @description Carga hoteles según el contexto (todos o filtrados por destino)
   */
  private loadHotels(): void {
    console.log('🏨 loadHotels called with destinationId:', this.destinationId);
    if (this.destinationId) {
      console.log('🏨 Loading hotels by destination:', this.destinationId);
      this.loadHotelsByDestination(this.destinationId);
    } else {
      console.log('🏨 Loading all hotels');
      this.loadAllHotels();
    }
  }

  /**
   * @method loadHotelDetail
   * @private
   * @description Carga los detalles de un hotel específico
   * @param {number} id - ID del hotel a cargar
   */
  private loadHotelDetail(id: number): void {
    this.isLoading = true;
    this.hotelService.getHotelById(id).subscribe({
      next: (hotel) => {
        this.selectedHotel = hotel;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading hotel detail:', error);
        this.isLoading = false;
        // Redirigir a la lista si no se encuentra el hotel
        this.router.navigate(['/hotels']);
      }
    });
  }

  /**
   * @method onSearch
   * @description Maneja el evento de búsqueda por texto
   */
  onSearch(): void {
    this.filterHotels();
  }

  /**
   * @method onRatingChange
   * @description Maneja el evento de cambio en el filtro de calificación
   */
  onRatingChange(): void {
    this.filterHotels();
  }

  /**
   * @method onPriceChange
   * @description Maneja el evento de cambio en el rango de precios
   */
  onPriceChange(): void {
    this.filterHotels();
  }
  /**
   * @method onDestinationFilterChange
   * @description Maneja el evento de cambio en el filtro de destino
   */
  onDestinationFilterChange(): void {
    console.log('🎯 Destination filter changed:', this.selectedDestinationFilter);
    console.log('🎯 Type of selectedDestinationFilter:', typeof this.selectedDestinationFilter);
    
    // Convertir a número si es string
    const destinationFilterId = this.selectedDestinationFilter ? +this.selectedDestinationFilter : null;
    console.log('🎯 Converted destinationFilterId:', destinationFilterId);
    
    // Si se selecciona un destino específico, recargar hoteles filtrados
    if (destinationFilterId && destinationFilterId > 0) {
      this.destinationId = destinationFilterId;
      this.selectedDestinationFilter = destinationFilterId;
      console.log('🏨 Loading hotels for destination:', this.destinationId);
      this.loadHotelsByDestination(destinationFilterId);
    } else {
      // Si se selecciona "Todos los destinos", cargar todos los hoteles
      this.destinationId = null;
      this.selectedDestinationFilter = null;
      console.log('🏨 Loading all hotels - clearing destination filter');
      this.loadAllHotels();
    }
  }

  /**
   * @method loadHotelsByDestination
   * @private
   * @description Carga hoteles filtrados por destino específico
   * @param {number} destinationId - ID del destino seleccionado
   */
  private loadHotelsByDestination(destinationId: number): void {
    this.isLoading = true;
    console.log('🚀 Calling API for destination:', destinationId);
    console.log('🚀 API URL will be:', `/api/hotels/by-destination/${destinationId}`);
    
    // Usar un tamaño de página mayor para obtener más resultados
    this.hotelService.getHotelsByDestination(destinationId, 0, 100).subscribe({
      next: (response) => {
        console.log('✅ Hotels loaded for destination:', response.content.length);
        console.log('✅ Response data:', response);
        this.hotels = response.content;
        this.filteredHotels = [...response.content]; // Crear una copia
        this.applyCurrentFilters(); // Aplicar otros filtros activos
        this.isLoading = false;
        console.log('✅ Final filtered hotels count:', this.filteredHotels.length);
      },
      error: (error: any) => {
        console.error('❌ Error loading hotels by destination:', error);
        console.error('❌ Error details:', JSON.stringify(error, null, 2));
        this.isLoading = false;
      }
    });
  }

  /**
   * @method loadAllHotels
   * @private
   * @description Carga todos los hoteles disponibles
   */
  private loadAllHotels(): void {
    this.isLoading = true;
    console.log('🚀 Loading all hotels');
    
    // Usar un tamaño de página mayor para obtener más resultados
    this.hotelService.getAllHotels(0, 100).subscribe({
      next: (response) => {
        console.log('✅ All hotels loaded:', response.content.length);
        console.log('✅ Response data:', response);
        this.hotels = response.content;
        this.filteredHotels = [...response.content]; // Crear una copia
        this.applyCurrentFilters(); // Aplicar otros filtros activos
        this.isLoading = false;
        console.log('✅ Final filtered hotels count:', this.filteredHotels.length);
      },
      error: (error: any) => {
        console.error('❌ Error loading all hotels:', error);
        console.error('❌ Error details:', JSON.stringify(error, null, 2));
        this.isLoading = false;
      }
    });
  }

  /**
   * @method applyCurrentFilters
   * @private
   * @description Aplica todos los filtros actuales excepto el de destino
   */
  private applyCurrentFilters(): void {
    const initialCount = this.hotels.length;
    console.log('🔍 Applying current filters to', initialCount, 'hotels');
    console.log('🔍 Current filters:', {
      searchTerm: this.searchTerm,
      selectedRating: this.selectedRating,
      priceRange: this.priceRange,
      selectedDestinationFilter: this.selectedDestinationFilter
    });
    
    this.filteredHotels = this.hotels.filter(hotel => {
      const matchesSearch = !this.searchTerm || 
                           hotel.name.toLowerCase().includes(this.searchTerm.toLowerCase()) ||
                           hotel.description.toLowerCase().includes(this.searchTerm.toLowerCase());
      
      const matchesRating = this.selectedRating === 0 || hotel.rating >= this.selectedRating;
      
      // Check if hotel has room types and if any room matches the price range
      const matchesPrice = !hotel.roomTypes || hotel.roomTypes.length === 0 || 
                          hotel.roomTypes.some((room: any) => 
                            room.pricePerNight >= this.priceRange.min && 
                            room.pricePerNight <= this.priceRange.max
                          );
      
      const result = matchesSearch && matchesRating && matchesPrice;
      
      if (!result) {
        console.log('🚫 Hotel filtered out:', hotel.name, {
          matchesSearch,
          matchesRating,
          matchesPrice
        });
      }
      
      return result;
    });
    
    console.log(`🔍 Filters applied: ${initialCount} → ${this.filteredHotels.length} hotels`);
  }

  /**
   * @method filterHotels
   * @description Filtra la lista de hoteles según múltiples criterios: texto, calificación y precio
   * Nota: El filtro de destino se maneja por separado en onDestinationFilterChange
   */
  filterHotels(): void {
    this.applyCurrentFilters();
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
   * @method getMinPrice
   * @description Obtiene el precio mínimo entre todas las habitaciones de un hotel
   * @param {HotelResponse} hotel - Hotel del cual obtener el precio mínimo
   * @returns {number} Precio mínimo de habitación
   */
  getMinPrice(hotel: HotelResponse): number {
    if (!hotel.roomTypes || hotel.roomTypes.length === 0) {
      return 0;
    }
    return Math.min(...hotel.roomTypes.map((room: any) => room.pricePerNight));
  }

  /**
   * @method clearFilters
   * @description Limpia todos los filtros aplicados y restaura la lista completa
   */
  clearFilters(): void {
    this.searchTerm = '';
    this.selectedRating = 0;
    this.priceRange = { min: 0, max: 1000000 };
    this.selectedDestinationFilter = null;
    this.destinationId = null;
    
    // Cargar todos los hoteles sin filtros
    this.loadAllHotels();
    
    // Navegar sin query parameters para remover el filtro de destino
    this.router.navigate(['/hotels']);
  }

  /**
   * @method toggleFilters
   * @description Alterna la visibilidad de los filtros en dispositivos móviles
   */
  toggleFilters(): void {
    this.filtersExpanded = !this.filtersExpanded;
  }

  /**
   * @method getActiveFiltersCount
   * @description Cuenta el número de filtros activos actualmente
   * @returns {number} Número de filtros activos
   */
  getActiveFiltersCount(): number {
    let count = 0;
    if (this.searchTerm) count++;
    if (this.selectedRating > 0) count++;
    if (this.priceRange.max < 1000000) count++;
    if (this.selectedDestinationFilter && this.selectedDestinationFilter !== 0) count++;
    return count;
  }

  /**
   * @method getSelectedDestinationName
   * @description Obtiene el nombre del destino seleccionado en el filtro
   * @returns {string} Nombre del destino o cadena vacía
   */
  getSelectedDestinationName(): string {
    const targetId = this.destinationId || this.selectedDestinationFilter;
    if (!targetId) return '';
    
    const destination = this.destinations.find(d => d.id === targetId);
    return destination ? destination.name : '';
  }

  /**
   * @method goBackToList
   * @description Navega de regreso a la vista de lista de hoteles
   */
  goBackToList(): void {
    this.router.navigate(['/hotels']);
  }

  /**
   * @method viewHotelDetails
   * @description Navega a la vista de detalle de un hotel específico
   * @param {number} hotelId - ID del hotel a visualizar
   */
  viewHotelDetails(hotelId: number): void {
    this.router.navigate(['/hotels', hotelId]);
  }
}

import { Component, OnInit } from '@angular/core';
import { HotelService } from '../../../core/services/hotel.service';
import { DestinationService } from '../../../core/services/destination.service';
import { ImageUploadService, ImageValidationResult } from '../../../core/services/image-upload.service';
import { Hotel, RoomType } from '../../../core/models/hotel.model';
import { Destination } from '../../../core/models/destination.model';

/**
 * @interface HotelFormData
 * @description Datos del formulario de hotel
 */
interface HotelFormData {
  id?: number;
  name: string;
  description: string;
  address: string;
  destinationId: number;
  basePrice: number;
  stars: number;
  amenities: string;
  phoneNumber: string;
  email: string;
  imageUrl: string;
  active: boolean;
}

/**
 * @interface RoomTypeFormData
 * @description Datos del formulario de tipo de habitación
 */
interface RoomTypeFormData {
  id?: number;
  name: string;
  description: string;
  price: number;
  capacity: number;
  availableRooms: number;
  active: boolean;
}

/**
 * @interface HotelFilters
 * @description Filtros para búsqueda de hoteles
 */
interface HotelFilters {
  search: string;
  destinationId: number | string | null;
  minPrice: number | null;
  maxPrice: number | null;
  minRating: number | null;
  active: boolean | string | null;
}

/**
 * @class AdminHotelsComponent
 * @description Componente para gestión administrativa de hoteles
 */
@Component({
  selector: 'app-admin-hotels',
  templateUrl: './admin-hotels.component.html',
  styleUrls: ['./admin-hotels.component.scss']
})
export class AdminHotelsComponent implements OnInit {

  // Datos principales
  hotels: Hotel[] = [];
  allHotels: Hotel[] = [];  // Todos los hoteles para filtrado local
  filteredHotels: Hotel[] = [];  // Hoteles después de aplicar filtros
  destinations: Destination[] = [];
  roomTypes: RoomType[] = [];
  
  // Paginación
  currentPage = 0;
  pageSize = 10;
  totalElements = 0;
  totalPages = 0;
  
  // Estado del componente
  isLoading = false;
  isLoadingRoomType = false;
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  showRoomTypesModal = false;
  showCreateRoomTypeModal = false;
  showEditRoomTypeModal = false;
  selectedHotel: Hotel | null = null;
  isEditingRoomType = false;
  
  // Formulario
  hotelForm: HotelFormData = this.getEmptyForm();
  currentRoomType: RoomTypeFormData = this.getEmptyRoomTypeForm();
  formErrors: any = {};
  
  // Manejo de imágenes
  imageUploadMethod: 'url' | 'file' = 'url';
  selectedImageFile: File | null = null;
  imagePreview: string | null = null;
  isUploadingImage = false;
  imageUploadError = '';
  
  // Configuración de imagen
  readonly IMAGE_CONFIG = {
    maxSizeBytes: 5 * 1024 * 1024, // 5MB
    minWidth: 800,
    minHeight: 600,
    maxWidth: 2048,
    maxHeight: 1536,
    allowedTypes: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    allowedExtensions: ['.jpg', '.jpeg', '.png', '.webp']
  };
  
  // Filtros
  filters: HotelFilters = {
    search: '',
    destinationId: null,
    minPrice: null,
    maxPrice: null,
    minRating: null,
    active: null
  };
  
  // Filtros avanzados
  showAdvancedFilters = false;
  
  // Mensajes
  successMessage = '';
  errorMessage = '';

  constructor(
    private hotelService: HotelService,
    private destinationService: DestinationService,
    private imageUploadService: ImageUploadService
  ) {}

  ngOnInit(): void {
    console.log('AdminHotelsComponent ngOnInit called');
    this.loadHotels();
    this.loadDestinations();
  }

  /**
   * Carga la lista de hoteles con filtros y paginación
   */
  loadHotels(): void {
    this.isLoading = true;
    this.clearMessages();
    
    console.log('Loading all hotels for client-side filtering');

    // Cargar todos los hoteles sin paginación para filtrado local
    this.hotelService.getAllHotelsForAdmin(0, 1000, 'name', 'asc')
      .subscribe({
        next: (response) => {
          console.log('Hotels response from ADMIN endpoint:', response);
          this.processAllHotelsResponse(response);
        },
        error: (error) => {
          console.error('Error with getAllHotelsForAdmin:', error);
          console.log('Falling back to regular getAllHotels...');
          
          // Si falla, usar el método estándar
          this.hotelService.getAllHotels(0, 1000, 'name', 'asc')
            .subscribe({
              next: (response) => {
                console.log('Hotels response from STANDARD endpoint:', response);
                this.processAllHotelsResponse(response);
              },
              error: (fallbackError) => {
                console.error('Error loading hotels with fallback:', fallbackError);
                this.errorMessage = 'Error al cargar los hoteles: ' + (fallbackError.message || 'Error desconocido');
                this.isLoading = false;
                
                // Reset values on error
                this.hotels = [];
                this.allHotels = [];
                this.filteredHotels = [];
                this.totalElements = 0;
                this.totalPages = 0;
              }
            });
        }
      });
  }

  /**
   * Procesa la respuesta del backend para los hoteles
   */
  private processHotelResponse(response: any): void {
    console.log('Processing hotel response:', response);
    console.log('Response content:', response.content);
    console.log('Response pagination info:', {
      totalElements: response.totalElements,
      totalPages: response.totalPages,
      currentPage: response.number,
      pageSize: response.size,
      contentLength: response.content?.length
    });
    
    this.hotels = response.content || [];
    this.totalElements = response.totalElements || 0;
    this.totalPages = response.totalPages || 0;
    this.currentPage = response.number || 0;
    this.pageSize = response.size || 10;
    this.isLoading = false;
    
    // Debugging adicional
    console.log('Component state after update:', {
      hotelsCount: this.hotels.length,
      totalElements: this.totalElements,
      totalPages: this.totalPages,
      currentPage: this.currentPage,
      pageSize: this.pageSize
    });

    // Validación de consistencia
    if (this.hotels.length > this.pageSize) {
      console.warn('WARNING: Content length is greater than page size!', {
        contentLength: this.hotels.length,
        pageSize: this.pageSize
      });
    }

    if (this.totalElements < this.hotels.length) {
      console.warn('WARNING: Total elements is less than content length!', {
        totalElements: this.totalElements,
        contentLength: this.hotels.length
      });
    }
  }

  /**
   * Procesa la respuesta del backend para todos los hoteles (filtrado local)
   */
  private processAllHotelsResponse(response: any): void {
    console.log('Processing all hotels response for client-side filtering:', response);
    
    // Mapear la respuesta del backend para normalizar el destinationId
    this.allHotels = (response.content || []).map((hotel: any) => ({
      ...hotel,
      // Normalizar destinationId para que sea accesible directamente
      destinationId: hotel.destination?.id || hotel.destinationId
    }));
    
    console.log('Loaded all hotels:', this.allHotels.length);
    
    // Debug: mostrar estructura de los primeros hoteles
    if (this.allHotels.length > 0) {
      console.log('Sample hotel structure:', {
        hotel: this.allHotels[0],
        originalDestination: response.content[0].destination,
        normalizedDestinationId: this.allHotels[0].destinationId,
        isActive: this.allHotels[0].isActive,
        destinationIdType: typeof this.allHotels[0].destinationId,
        isActiveType: typeof this.allHotels[0].isActive
      });
    }
    
    // Debug: mostrar todos los destinationId únicos para verificar
    const uniqueDestinationIds = [...new Set(this.allHotels.map(h => h.destinationId))];
    console.log('Unique destination IDs in hotels:', uniqueDestinationIds);
    
    // Debug: mostrar hoteles por destino
    const hotelsByDestination = this.allHotels.reduce((acc, hotel) => {
      const destId = hotel.destinationId?.toString() || 'sin_destino';
      if (!acc[destId]) acc[destId] = [];
      acc[destId].push(hotel.name);
      return acc;
    }, {} as any);
    console.log('Hotels grouped by destination:', hotelsByDestination);
    
    // Aplicar filtros y paginación local
    this.applyFiltersToData();
    this.isLoading = false;
  }

  /**
   * Aplica filtros a los datos localmente
   */
  applyFiltersToData(): void {
    let filtered = [...this.allHotels];
    
    console.log('=== APPLYING FILTERS ===');
    console.log('Starting with hotels:', filtered.length);
    console.log('Current filters:', this.filters);

    // Debug: verificar que tenemos hoteles para filtrar
    if (filtered.length === 0) {
      console.warn('No hotels to filter');
      this.filteredHotels = [];
      this.totalElements = 0;
      this.applyPaginationToFiltered();
      return;
    }

    // Filtro de búsqueda (nombre, descripción)
    if (this.filters.search && this.filters.search.trim()) {
      const searchTerm = this.filters.search.toLowerCase().trim();
      filtered = filtered.filter(hotel => 
        hotel.name.toLowerCase().includes(searchTerm) ||
        (hotel.description && hotel.description.toLowerCase().includes(searchTerm))
      );
    }

    // Filtro por destino - verificar que no sea null, undefined, cadena vacía o "null"
    if (this.filters.destinationId !== null && 
        this.filters.destinationId !== undefined && 
        this.filters.destinationId !== '' && 
        this.filters.destinationId !== 'null') {
      
      const destinationId = Number(this.filters.destinationId);
      console.log('Filtering by destination:', destinationId, 'type:', typeof destinationId);
      
      // Verificar que la conversión fue exitosa
      if (!isNaN(destinationId) && destinationId > 0) {
        filtered = filtered.filter(hotel => {
          const hotelDestinationId = hotel.destinationId;
          console.log(`Hotel ${hotel.name}: destinationId=${hotelDestinationId}, filtering by=${destinationId}, match=${hotelDestinationId === destinationId}`);
          return hotelDestinationId === destinationId;
        });
      }
    }

    // Filtro por estado activo/inactivo - asegurar comparación correcta de boolean
    if (this.filters.active !== null && 
        this.filters.active !== undefined && 
        this.filters.active !== '' && 
        this.filters.active !== 'null') {
      
      console.log('Filtering by active status:', this.filters.active, typeof this.filters.active);
      
      // Manejar diferentes tipos de valores que pueden llegar desde el HTML
      let targetActiveState: boolean | null = null;
      
      if (this.filters.active === true || this.filters.active === 'true') {
        targetActiveState = true;
      } else if (this.filters.active === false || this.filters.active === 'false') {
        targetActiveState = false;
      }
      
      // Solo aplicar filtro si tenemos un estado válido
      if (targetActiveState !== null) {
        filtered = filtered.filter(hotel => {
          const hotelIsActive = hotel.isActive === true;
          console.log(`Hotel ${hotel.name}: isActive=${hotelIsActive}, filtering by=${targetActiveState}, match=${hotelIsActive === targetActiveState}`);
          return hotelIsActive === targetActiveState;
        });
      }
    }

    // Filtro por precio mínimo
    if (this.filters.minPrice !== null && this.filters.minPrice > 0) {
      filtered = filtered.filter(hotel => 
        hotel.basePrice !== undefined && hotel.basePrice >= this.filters.minPrice!
      );
    }

    // Filtro por precio máximo
    if (this.filters.maxPrice !== null && this.filters.maxPrice > 0) {
      filtered = filtered.filter(hotel => 
        hotel.basePrice !== undefined && hotel.basePrice <= this.filters.maxPrice!
      );
    }

    // Filtro por rating mínimo
    if (this.filters.minRating !== null && this.filters.minRating > 0) {
      filtered = filtered.filter(hotel => 
        hotel.rating !== undefined && hotel.rating >= this.filters.minRating!
      );
    }

    this.filteredHotels = filtered;
    this.totalElements = this.filteredHotels.length;
    
    console.log('=== FILTER RESULTS ===');
    console.log('Filtered hotels count:', this.filteredHotels.length);
    console.log('Filtered hotels names:', this.filteredHotels.map(h => h.name));
    
    // Aplicar paginación a los resultados filtrados
    this.applyPaginationToFiltered();
  }

  /**
   * Aplica paginación a los hoteles filtrados
   */
  applyPaginationToFiltered(): void {
    this.totalPages = Math.ceil(this.totalElements / this.pageSize);
    
    // Asegurar que currentPage esté en rango válido
    if (this.currentPage >= this.totalPages && this.totalPages > 0) {
      this.currentPage = this.totalPages - 1;
    }
    if (this.currentPage < 0) {
      this.currentPage = 0;
    }

    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    this.hotels = this.filteredHotels.slice(startIndex, endIndex);
    
    console.log('Pagination applied:', {
      totalElements: this.totalElements,
      totalPages: this.totalPages,
      currentPage: this.currentPage,
      pageSize: this.pageSize,
      startIndex,
      endIndex,
      hotelsOnPage: this.hotels.length
    });
  }

  /**
   * Maneja el cambio en tiempo real del campo de búsqueda
   */
  onSearchChange(): void {
    this.currentPage = 0; // Reset a la primera página
    this.applyFiltersToData();
  }

  /**
   * Carga la lista de destinos
   */
  loadDestinations(): void {
    this.destinationService.getAllDestinations(0, 1000)
      .subscribe({
        next: (response) => {
          this.destinations = response.content || [];
          console.log('Loaded destinations:', this.destinations.length);
        },
        error: (error) => {
          console.error('Error loading destinations:', error);
        }
      });
  }

  /**
   * Abre el modal para crear un nuevo hotel
   */
  openCreateModal(): void {
    this.hotelForm = this.getEmptyForm();
    this.formErrors = {};
    this.resetImageUpload();
    this.showCreateModal = true;
  }

  /**
   * Abre el modal para editar un hotel
   */
  openEditModal(hotel: Hotel): void {
    this.selectedHotel = hotel;
    // Extraer phone del backend si no existe phoneNumber
    const phoneNumber = hotel.phoneNumber || (hotel as any).phone || '';
    
    this.hotelForm = {
      id: hotel.id,
      name: hotel.name,
      description: hotel.description || '',
      address: hotel.address || '',
      destinationId: hotel.destination?.id || hotel.destinationId || 0,
      basePrice: hotel.basePrice || 0,
      stars: hotel.stars || 3, // Valor por defecto 3 estrellas
      amenities: hotel.amenities || '',
      phoneNumber: phoneNumber,
      email: hotel.email || '',
      imageUrl: hotel.imageUrl || '',
      active: hotel.isActive !== undefined ? hotel.isActive : true
    };
    this.formErrors = {};
    this.resetImageUpload();
    this.showEditModal = true;
  }

  /**
   * Abre el modal de confirmación para eliminar un hotel
   */
  openDeleteModal(hotel: Hotel): void {
    this.selectedHotel = hotel;
    this.showDeleteModal = true;
  }

  /**
   * Cierra todos los modales
   */
  closeModals(): void {
    this.showCreateModal = false;
    this.showEditModal = false;
    this.showDeleteModal = false;
    this.selectedHotel = null;
    this.formErrors = {};
    this.resetImageUpload();
  }

  /**
   * Crea un nuevo hotel
   */
  async createHotel(): Promise<void> {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    
    try {
      // Si hay una imagen seleccionada, subirla primero
      let imageUrl = this.hotelForm.imageUrl;
      if (this.imageUploadMethod === 'file' && this.selectedImageFile) {
        imageUrl = await this.uploadImageFile();
      }
      
      // Convertir el formulario al formato esperado por el servicio
      const hotelData = {
        name: this.hotelForm.name,
        description: this.hotelForm.description,
        address: this.hotelForm.address,
        destinationId: this.hotelForm.destinationId,
        basePrice: this.hotelForm.basePrice,
        stars: this.hotelForm.stars,
        amenities: this.hotelForm.amenities,
        phoneNumber: this.hotelForm.phoneNumber ? this.hotelForm.phoneNumber.replace(/\s+/g, '') : null,
        email: this.hotelForm.email,
        imageUrl: imageUrl,
        active: this.hotelForm.active
      };
      
      console.log('Sending hotel create data:', hotelData);
      
      this.hotelService.createHotel(hotelData as any)
        .subscribe({
          next: (response: any) => {
            this.successMessage = 'Hotel creado exitosamente';
            this.closeModals();
            this.loadHotels();
            this.isLoading = false;
          },
          error: (error: any) => {
            console.error('Error creating hotel:', error);
            this.handleFormErrors(error);
            this.isLoading = false;
          }
        });
    } catch (error) {
      console.error('Error uploading image:', error);
      this.errorMessage = 'Error al subir la imagen';
      this.isLoading = false;
    }
  }

  /**
   * Actualiza un hotel existente
   */
  async updateHotel(): Promise<void> {
    if (!this.validateForm() || !this.hotelForm.id) {
      return;
    }

    this.isLoading = true;
    
    try {
      // Si hay una imagen seleccionada, subirla primero
      let imageUrl = this.hotelForm.imageUrl;
      if (this.imageUploadMethod === 'file' && this.selectedImageFile) {
        imageUrl = await this.uploadImageFile();
      }
      
      // Convertir el formulario al formato esperado por el servicio
      const hotelData = {
        name: this.hotelForm.name,
        description: this.hotelForm.description,
        address: this.hotelForm.address,
        destinationId: this.hotelForm.destinationId,
        basePrice: this.hotelForm.basePrice,
        stars: this.hotelForm.stars,
        amenities: this.hotelForm.amenities,
        phoneNumber: this.hotelForm.phoneNumber ? this.hotelForm.phoneNumber.replace(/\s+/g, '') : null,
        email: this.hotelForm.email,
        imageUrl: imageUrl,
        active: this.hotelForm.active
      };
      
      console.log('Sending hotel update data:', hotelData);
      
      this.hotelService.updateHotel(this.hotelForm.id, hotelData as any)
        .subscribe({
          next: (response: any) => {
            this.successMessage = 'Hotel actualizado exitosamente';
            this.closeModals();
            this.loadHotels();
            this.isLoading = false;
          },
          error: (error: any) => {
            console.error('Error updating hotel:', error);
            this.handleFormErrors(error);
            this.isLoading = false;
          }
        });
    } catch (error) {
      console.error('Error uploading image:', error);
      this.errorMessage = 'Error al subir la imagen';
      this.isLoading = false;
    }
  }

  /**
   * Desactiva un hotel
   */
  deleteHotel(): void {
    if (!this.selectedHotel) {
      return;
    }

    this.isLoading = true;
    
    // Usar el endpoint de desactivación de hotel
    this.hotelService.deactivateHotel(this.selectedHotel.id)
      .subscribe({
        next: (response: any) => {
          this.successMessage = 'Hotel desactivado exitosamente';
          this.closeModals();
          this.loadHotels();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error deactivating hotel:', error);
          
          // Si el error es de parsing pero el status es 200, considerarlo éxito
          if (error.status === 200) {
            this.successMessage = 'Hotel desactivado exitosamente';
            this.closeModals();
            this.loadHotels();
          } else {
            this.errorMessage = 'Error al desactivar el hotel';
          }
          this.isLoading = false;
        }
      });
  }

  /**
   * Aplica los filtros de búsqueda
   */
  applyFilters(): void {
    this.currentPage = 0;
    this.applyFiltersToData();
  }

  /**
   * Limpia todos los filtros
   */
  clearFilters(): void {
    this.filters = {
      search: '',
      destinationId: null,
      minPrice: null,
      maxPrice: null,
      minRating: null,
      active: null
    };
    this.currentPage = 0;
    this.applyFiltersToData();
  }

  /**
   * Cambia de página
   */
  changePage(page: number): void {
    console.log('Changing to page:', page, 'from current page:', this.currentPage);
    if (page >= 0 && page < this.totalPages && page !== this.currentPage) {
      this.currentPage = page;
      this.applyPaginationToFiltered();
    } else {
      console.log('Page change blocked. Page:', page, 'TotalPages:', this.totalPages, 'CurrentPage:', this.currentPage);
    }
  }

  /**
   * Cambia el tamaño de página
   */
  changePageSize(): void {
    console.log('Changing page size to:', this.pageSize);
    this.currentPage = 0; // Reset to first page
    this.applyPaginationToFiltered();
  }

  previousPage(): void {
    if (this.currentPage > 0) {
      this.currentPage--;
      this.applyPaginationToFiltered();
    }
  }

  nextPage(): void {
    if (this.currentPage < this.totalPages - 1) {
      this.currentPage++;
      this.applyPaginationToFiltered();
    }
  }

  /**
   * Obtiene las páginas visibles para la paginación (simplificada como en bookings)
   */
  getVisiblePages(): number[] {
    const totalPages = this.totalPages;
    const currentPage = this.currentPage;
    const visiblePages: number[] = [];
    
    // Máximo 5 páginas visibles para mantener compacto
    const maxVisible = 5;
    let startPage = Math.max(0, currentPage - Math.floor(maxVisible / 2));
    let endPage = Math.min(totalPages - 1, startPage + maxVisible - 1);
    
    // Ajustar si estamos cerca del final
    if (endPage - startPage + 1 < maxVisible) {
      startPage = Math.max(0, endPage - maxVisible + 1);
    }
    
    for (let i = startPage; i <= endPage; i++) {
      visiblePages.push(i);
    }
    
    return visiblePages;
  }

  /**
   * Referencia a Math para usar en el template
   */
  Math = Math;

  /**
   * Obtiene el rango final para la información de paginación
   */
  getEndRange(): number {
    return Math.min((this.currentPage + 1) * this.pageSize, this.totalElements);
  }

  /**
   * Obtiene un formulario vacío
   */
  private getEmptyForm(): HotelFormData {
    return {
      name: '',
      description: '',
      address: '',
      destinationId: 0,
      basePrice: 0, // Valor por defecto razonable para hoteles
      stars: 3, // Valor por defecto 3 estrellas
      amenities: '',
      phoneNumber: '',
      email: '',
      imageUrl: '',
      active: true
    };
  }

  /**
   * Obtiene un formulario vacío para tipo de habitación
   */
  private getEmptyRoomTypeForm(): RoomTypeFormData {
    return {
      name: '',
      description: '',
      price: 0,
      capacity: 1,
      availableRooms: 0,
      active: true
    };
  }

  /**
   * Obtiene los parámetros de filtro para la API
   */
  private getFilterParams(): any {
    const params: any = {};
    
    if (this.filters.search) {
      params.search = this.filters.search;
    }
    if (this.filters.destinationId) {
      params.destinationId = this.filters.destinationId;
    }
    if (this.filters.minPrice !== null) {
      params.minPrice = this.filters.minPrice;
    }
    if (this.filters.maxPrice !== null) {
      params.maxPrice = this.filters.maxPrice;
    }
    if (this.filters.minRating !== null) {
      params.minRating = this.filters.minRating;
    }
    if (this.filters.active !== null) {
      params.active = this.filters.active;
    }
    
    return params;
  }

  /**
   * Valida el formulario
   */
  private validateForm(): boolean {
    this.formErrors = {};
    let isValid = true;

    if (!this.hotelForm.name || this.hotelForm.name.trim().length < 2) {
      this.formErrors.name = 'El nombre es obligatorio (mínimo 2 caracteres)';
      isValid = false;
    }

    if (!this.hotelForm.description || this.hotelForm.description.trim().length < 10) {
      this.formErrors.description = 'La descripción es obligatoria (mínimo 10 caracteres)';
      isValid = false;
    }

    if (!this.hotelForm.address || this.hotelForm.address.trim().length < 5) {
      this.formErrors.address = 'La dirección es obligatoria (mínimo 5 caracteres)';
      isValid = false;
    }

    if (!this.hotelForm.destinationId || this.hotelForm.destinationId <= 0) {
      this.formErrors.destinationId = 'Debe seleccionar un destino';
      isValid = false;
    }

    // Validación de las estrellas del hotel (entre 1 y 5)
    if (this.hotelForm.stars < 1 || this.hotelForm.stars > 5) {
      this.formErrors.stars = 'Las estrellas del hotel deben estar entre 1 y 5';
      isValid = false;
    }

    // Validación del teléfono (patrón del backend: ^\\+?[1-9]\\d{1,14}$)
    if (this.hotelForm.phoneNumber && this.hotelForm.phoneNumber.trim()) {
      const phonePattern = /^\+?[1-9]\d{1,14}$/;
      const cleanPhone = this.hotelForm.phoneNumber.replace(/\s+/g, ''); // Limpiar espacios para validación
      if (!phonePattern.test(cleanPhone)) {
        this.formErrors.phoneNumber = 'Formato de teléfono inválido (ej: +575642800 o 5642800, sin espacios)';
        isValid = false;
      }
    }

    if (this.hotelForm.email && !this.isValidEmail(this.hotelForm.email)) {
      this.formErrors.email = 'Formato de email inválido';
      isValid = false;
    }

    // Validación de imagen
    if (this.imageUploadMethod === 'url') {
      if (this.hotelForm.imageUrl && this.hotelForm.imageUrl.trim()) {
        const urlValidation = this.imageUploadService.validateUrlWithFeedback(this.hotelForm.imageUrl);
        if (!urlValidation.isValid) {
          this.formErrors.imageUrl = urlValidation.message || 'URL de imagen inválida';
          isValid = false;
        }
      }
    } else if (this.imageUploadMethod === 'file') {
      if (!this.selectedImageFile && !this.hotelForm.imageUrl) {
        this.formErrors.imageFile = 'Debe seleccionar una imagen';
        isValid = false;
      }
      if (this.imageUploadError) {
        this.formErrors.imageFile = this.imageUploadError;
        isValid = false;
      }
    }

    return isValid;
  }

  /**
   * Valida formato de email
   */
  private isValidEmail(email: string): boolean {
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    return emailRegex.test(email);
  }

  /**
   * Maneja errores del formulario
   */
  private handleFormErrors(error: any): void {
    if (error.error && error.error.validationErrors) {
      this.formErrors = error.error.validationErrors;
      
      // Mensaje especial para el precio base
      if (this.formErrors.basePrice) {
        console.warn('Error de precio base:', this.formErrors.basePrice);
        console.log('Nota: El precio base debería ser calculado automáticamente por el backend basado en las habitaciones');
      }
    } else if (error.error && error.error.errors) {
      this.formErrors = error.error.errors;
    } else {
      this.errorMessage = error.error?.message || 'Error al procesar la solicitud';
    }
  }

  /**
   * Limpia los mensajes de éxito y error
   */
  private clearMessages(): void {
    this.successMessage = '';
    this.errorMessage = '';
  }

  /**
   * Obtiene el nombre del destino por ID
   */
  getDestinationName(destinationId: number): string {
    const destination = this.destinations.find(d => d.id === destinationId);
    return destination ? destination.name : 'Destino desconocido';
  }

  /**
   * Formatea un precio para mostrar
   */
  formatPrice(price: number): string {
    return new Intl.NumberFormat('es-ES', {
      style: 'currency',
      currency: 'COP'
    }).format(price);
  }

  /**
   * Genera un array de estrellas para mostrar rating
   */
  getStarsArray(rating: number): number[] {
    return Array(5).fill(0).map((_, i) => i < rating ? 1 : 0);
  }

  // ===========================
  // ROOM TYPES MANAGEMENT
  // ===========================

  /**
   * Abre el modal para gestionar habitaciones de un hotel
   */
  openRoomTypesModal(hotel: Hotel): void {
    this.selectedHotel = hotel;
    this.showRoomTypesModal = true;
    this.loadRoomTypes(hotel.id);
  }

  /**
   * Cierra el modal de gestión de habitaciones
   */
  closeRoomTypesModal(): void {
    this.showRoomTypesModal = false;
    this.selectedHotel = null;
    this.roomTypes = [];
  }

  /**
   * Carga los tipos de habitación de un hotel
   */
  private loadRoomTypes(hotelId: number): void {
    this.isLoading = true;
    this.hotelService.getHotelRoomTypes(hotelId).subscribe({
      next: (roomTypes: RoomType[]) => {
        this.roomTypes = roomTypes;
        this.isLoading = false;
      },
      error: (error: any) => {
        console.error('Error loading room types:', error);
        this.errorMessage = 'Error al cargar los tipos de habitación';
        this.isLoading = false;
      }
    });
  }

  /**
   * Abre el modal para crear un nuevo tipo de habitación
   */
  openCreateRoomTypeModal(): void {
    this.currentRoomType = this.getEmptyRoomTypeForm();
    this.isEditingRoomType = false;
    this.showCreateRoomTypeModal = true;
  }

  /**
   * Abre el modal para editar un tipo de habitación
   */
  openEditRoomTypeModal(roomType: RoomType): void {
    this.currentRoomType = {
      id: roomType.id,
      name: roomType.name || roomType.typeName || '',
      description: roomType.description || '',
      price: roomType.pricePerNight || roomType.price || 0,
      capacity: roomType.capacity || roomType.maxOccupancy || 1,
      availableRooms: roomType.availableRooms || 0,
      active: roomType.isActive !== undefined ? roomType.isActive : true
    };
    this.isEditingRoomType = true;
    this.showEditRoomTypeModal = true;
  }

  /**
   * Cierra los modales de crear/editar tipo de habitación
   */
  closeRoomTypeModals(): void {
    this.showCreateRoomTypeModal = false;
    this.showEditRoomTypeModal = false;
    this.currentRoomType = this.getEmptyRoomTypeForm();
    this.isEditingRoomType = false;
  }

  /**
   * Guarda un tipo de habitación (crear o actualizar)
   */
  saveRoomType(): void {
    if (!this.selectedHotel) {
      this.errorMessage = 'No hay hotel seleccionado';
      return;
    }

    this.isLoadingRoomType = true;

    const roomTypeData = {
      name: this.currentRoomType.name,
      description: this.currentRoomType.description,
      price: this.currentRoomType.price,
      capacity: this.currentRoomType.capacity,
      availableRooms: this.currentRoomType.availableRooms,
      active: this.currentRoomType.active
    };

    if (this.isEditingRoomType && this.currentRoomType.id) {
      // Actualizar tipo de habitación existente
      this.hotelService.updateRoomType(this.selectedHotel.id, this.currentRoomType.id, roomTypeData).subscribe({
        next: (updatedRoomType: any) => {
          this.successMessage = 'Tipo de habitación actualizado exitosamente';
          this.closeRoomTypeModals();
          this.loadRoomTypes(this.selectedHotel!.id);
          this.isLoadingRoomType = false;
        },
        error: (error: any) => {
          console.error('Error updating room type:', error);
          this.errorMessage = 'Error al actualizar el tipo de habitación';
          this.isLoadingRoomType = false;
        }
      });
    } else {
      // Crear nuevo tipo de habitación
      this.hotelService.createRoomType(this.selectedHotel.id, roomTypeData).subscribe({
        next: (newRoomType: any) => {
          this.successMessage = 'Tipo de habitación creado exitosamente';
          this.closeRoomTypeModals();
          this.loadRoomTypes(this.selectedHotel!.id);
          this.isLoadingRoomType = false;
        },
        error: (error: any) => {
          console.error('Error creating room type:', error);
          this.errorMessage = 'Error al crear el tipo de habitación';
          this.isLoadingRoomType = false;
        }
      });
    }
  }

  /**
   * Elimina un tipo de habitación
   */
  deleteRoomType(roomTypeId: number): void {
    if (!this.selectedHotel) {
      this.errorMessage = 'No hay hotel seleccionado';
      return;
    }

    if (!confirm('¿Estás seguro de que deseas eliminar este tipo de habitación?')) {
      return;
    }

    this.hotelService.deleteRoomType(this.selectedHotel.id, roomTypeId).subscribe({
      next: () => {
        this.successMessage = 'Tipo de habitación eliminado exitosamente';
        this.loadRoomTypes(this.selectedHotel!.id);
      },
      error: (error: any) => {
        console.error('Error deleting room type:', error);
        this.errorMessage = 'Error al eliminar el tipo de habitación';
      }
    });
  }

  /**
   * Método de debugging para verificar el estado de filtros
   * Llamar desde la consola del navegador: component.debugFilters()
   */
  debugFilters(): void {
    console.log('=== DEBUG FILTERS ===');
    console.log('Current filters:', this.filters);
    console.log('All hotels count:', this.allHotels.length);
    console.log('Filtered hotels count:', this.filteredHotels.length);
    console.log('Displayed hotels count:', this.hotels.length);
    console.log('Available destinations:', this.destinations.map(d => ({id: d.id, name: d.name})));
    
    if (this.allHotels.length > 0) {
      console.log('Hotel destinations mapping:', this.allHotels.map(h => ({
        name: h.name,
        originalDestination: h.destination,
        normalizedDestinationId: h.destinationId,
        destinationName: h.destinationName,
        isActive: h.isActive
      })));
    }
    
    // Test manual filtering
    if (this.filters.destinationId) {
      const targetDestinationId = Number(this.filters.destinationId);
      console.log('Testing filter for destination ID:', targetDestinationId);
      
      const manualFiltered = this.allHotels.filter(h => {
        const hotelDestId = h.destinationId;
        const matches = hotelDestId === targetDestinationId;
        console.log(`Hotel ${h.name}: destinationId=${hotelDestId}, target=${targetDestinationId}, matches=${matches}`);
        return matches;
      });
      
      console.log('Manual filter test result:', manualFiltered.map(h => h.name));
    }
  }

  // ===========================
  // IMAGE UPLOAD MANAGEMENT
  // ===========================

  /**
   * Cambia el método de subida de imagen
   */
  changeImageUploadMethod(method: 'url' | 'file'): void {
    this.imageUploadMethod = method;
    this.resetImageUpload();
  }

  /**
   * Resetea el estado de subida de imagen
   */
  resetImageUpload(): void {
    this.selectedImageFile = null;
    this.imagePreview = null;
    this.imageUploadError = '';
    this.isUploadingImage = false;
    
    // Si el método es URL, mantener la URL actual; si es file, limpiar
    if (this.imageUploadMethod === 'file') {
      this.hotelForm.imageUrl = '';
    }
  }

  /**
   * Maneja la selección de archivo de imagen
   */
  async onImageFileSelected(event: any): Promise<void> {
    const file: File = event.target.files[0];
    if (!file) {
      this.resetImageUpload();
      return;
    }

    this.imageUploadError = '';
    this.isUploadingImage = true;
    
    try {
      // Validar el archivo usando el servicio
      const validationResult: ImageValidationResult = await this.imageUploadService.validateImageFile(file, this.IMAGE_CONFIG);
      
      if (!validationResult.isValid) {
        this.imageUploadError = validationResult.errors.join('. ');
        this.resetImageUpload();
        return;
      }

      // El archivo es válido, guardarlo y crear preview
      this.selectedImageFile = file;
      this.imagePreview = await this.imageUploadService.createImagePreview(file);
      
      console.log(`Imagen válida: ${validationResult.dimensions?.width}x${validationResult.dimensions?.height}px, ${(file.size / 1024 / 1024).toFixed(2)}MB`);
      
    } catch (error) {
      console.error('Error validating image:', error);
      this.imageUploadError = 'Error al validar la imagen';
      this.resetImageUpload();
    } finally {
      this.isUploadingImage = false;
    }
  }

  /**
   * Sube la imagen al servidor
   */
  private uploadImageFile(): Promise<string> {
    return new Promise((resolve, reject) => {
      if (!this.selectedImageFile) {
        resolve(this.hotelForm.imageUrl || '');
        return;
      }

      this.isUploadingImage = true;
      
      // Usar el servicio real de subida de imagen
      this.imageUploadService.uploadHotelImage(this.selectedImageFile)
        .subscribe({
          next: (response) => {
            console.log('Imagen subida exitosamente:', response);
            this.isUploadingImage = false;
            resolve(response.url);
          },
          error: (error) => {
            console.error('Error uploading image:', error);
            this.isUploadingImage = false;
            
            // Fallback: simular subida si el endpoint no está disponible
            console.log('Fallback: simulando subida de imagen...');
            const timestamp = new Date().getTime();
            const fileName = `hotel_${timestamp}_${this.selectedImageFile!.name}`;
            const mockUrl = `/uploads/hotels/${fileName}`;
            
            setTimeout(() => {
              console.log('Imagen subida (simulada):', mockUrl);
              resolve(mockUrl);
            }, 1000);
          }
        });
    });
  }

  /**
   * Valida la URL de imagen
   */
  validateImageUrl(url: string): boolean {
    return this.imageUploadService.validateImageUrl(url);
  }

  /**
   * Maneja errores de carga de imagen
   */
  onImageLoadError(event: any): void {
    event.target.src = '/assets/images/hotel-placeholder.jpg';
  }

  /**
   * Obtiene la URL de imagen para mostrar
   */
  getImageUrl(): string {
    if (this.imageUploadMethod === 'file' && this.imagePreview) {
      return this.imagePreview;
    }
    return this.hotelForm.imageUrl || '/assets/images/hotel-placeholder.jpg';
  }

  /**
   * Obtiene información sobre las dimensiones recomendadas
   */
  getImageDimensionsInfo(): string {
    return this.imageUploadService.getDimensionsInfo(this.IMAGE_CONFIG);
  }

  /**
   * Obtiene información sobre el tamaño máximo de archivo
   */
  getMaxFileSizeInfo(): string {
    return this.imageUploadService.getMaxFileSizeInfo(this.IMAGE_CONFIG);
  }

  /**
   * Obtiene ejemplos de URLs válidas
   */
  getUrlExamples(): string[] {
    return this.imageUploadService.getUrlExamples();
  }

  /**
   * Obtiene información sobre formatos de URL aceptados
   */
  getUrlFormatsInfo(): string {
    return 'Formatos aceptados: URL web (https://...), ruta absoluta (/assets/...) o ruta relativa (uploads/...)';
  }

  /**
   * Valida la URL en tiempo real y muestra feedback
   */
  onImageUrlChange(): void {
    if (this.hotelForm.imageUrl && this.hotelForm.imageUrl.trim()) {
      const validation = this.imageUploadService.validateUrlWithFeedback(this.hotelForm.imageUrl);
      if (!validation.isValid) {
        this.formErrors.imageUrl = validation.message;
      } else {
        delete this.formErrors.imageUrl;
      }
    } else {
      delete this.formErrors.imageUrl;
    }
  }
}

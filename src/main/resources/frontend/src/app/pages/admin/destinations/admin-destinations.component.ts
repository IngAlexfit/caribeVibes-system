import { Component, OnInit } from '@angular/core';
import { DestinationService } from '../../../core/services/destination.service';

export interface AdminDestination {
  id?: number;
  name: string;
  description: string;
  country: string;
  countryName?: string; // Nombre del país que debe venir del backend
  imageUrl?: string;
  active?: boolean;
  isActive?: boolean;
  slug?: string;
  longDescription?: string;
  location?: string;
  category?: string;
  tags?: string[];
  lowSeasonPrice?: number;
  highSeasonPrice?: number;
  createdAt?: string;
}

@Component({
  selector: 'app-admin-destinations',
  templateUrl: './admin-destinations.component.html',
  styleUrls: ['./admin-destinations.component.scss']
})
export class AdminDestinationsComponent implements OnInit {
  // Data properties
  destinations: AdminDestination[] = [];
  allDestinations: AdminDestination[] = []; // Para almacenar todos los destinos sin filtrar
  filteredDestinations: AdminDestination[] = []; // Para almacenar destinos filtrados
  totalElements = 0;
  
  // Loading and error states
  isLoading = false;
  error: string | null = null;
  errorMessage: string | null = null;
  successMessage: string | null = null;
  
  // Pagination
  currentPage = 0;
  pageSize = 10;
  totalPages = 0;
  
  // Modal states
  showCreateModal = false;
  showEditModal = false;
  showDeleteModal = false;
  
  // Form data
  destinationForm: any = {
    name: '',
    description: '',
    country: '',
    imageUrl: '',
    active: true
  };
  
  // Form validation errors
  formErrors: any = {
    name: '',
    description: '',
    country: '',
    imageUrl: ''
  };
  
  // Filters
  filters: any = {
    search: '',
    country: '',
    active: ''
  };
  
  showAdvancedFilters = false;
  
  // Selected destination for delete modal
  selectedDestination: AdminDestination | null = null;
  
  // Countries list for select - will be populated dynamically from backend
  countries: { code: string, name: string }[] = [];

  constructor(private destinationService: DestinationService) {}

  ngOnInit(): void {
    this.loadDestinations();
  }

  // CRUD Operations
  loadDestinations(): void {
    this.isLoading = true;
    this.error = null;
    
    console.log('Loading destinations for admin with pagination:', {
      page: this.currentPage,
      size: this.pageSize
    });
    
    // Usar el endpoint específico para administradores que incluye destinos activos e inactivos
    const params = {
      page: this.currentPage,
      size: this.pageSize,
      sortBy: 'name',
      sortDir: 'asc'
    };
    
    this.destinationService.getDestinationsAdmin(params)
      .subscribe({
        next: (response: any) => {
          console.log('Admin destinations response:', response);
          this.handleDestinationsResponse(response);
        },
        error: (error: any) => {
          console.error('Error loading admin destinations:', error);
          
          // Fallback al método estándar si el endpoint de admin no está disponible
          console.log('Falling back to standard getAllDestinations...');
          this.destinationService.getAllDestinations(this.currentPage, this.pageSize, 'name', 'asc')
            .subscribe({
              next: (response: any) => {
                console.log('Fallback destinations response:', response);
                this.handleDestinationsResponse(response);
              },
              error: (fallbackError: any) => {
                console.error('Error loading destinations with fallback:', fallbackError);
                this.error = 'Error al cargar los destinos: ' + (fallbackError.message || 'Error desconocido');
                this.isLoading = false;
                this.destinations = [];
                this.allDestinations = [];
                this.totalElements = 0;
                this.totalPages = 0;
              }
            });
        }
      });
  }
  
  /**
   * Maneja la respuesta de destinos (método auxiliar)
   */
  private handleDestinationsResponse(response: any): void {
    if (response && response.content) {
      this.allDestinations = (response.content || []).map((dest: any) => ({
        ...dest,
        isActive: dest.active !== undefined ? dest.active : (dest.isActive !== undefined ? dest.isActive : true)
      }));
      this.totalElements = response.totalElements || 0;
      this.totalPages = response.totalPages || 0;
      this.currentPage = response.number || 0;
      this.pageSize = response.size || 10;
    } else {
      const rawDestinations = Array.isArray(response) ? response : [];
      this.allDestinations = rawDestinations.map((dest: any) => ({
        ...dest,
        isActive: dest.active !== undefined ? dest.active : (dest.isActive !== undefined ? dest.isActive : true)
      }));
      this.totalElements = this.allDestinations.length;
      this.totalPages = Math.ceil(this.totalElements / this.pageSize);
    }
    
    // Aplicar filtros después de cargar los datos
    this.applyFiltersToData();
    
    // Extraer países únicos de los destinos cargados
    this.extractUniqueCountries();
    
    console.log('Destinations loaded:', {
      total: this.allDestinations.length,
      filtered: this.filteredDestinations.length,
      totalElements: this.totalElements,
      totalPages: this.totalPages,
      currentPage: this.currentPage,
      uniqueCountries: this.countries.length
    });
    
    this.isLoading = false;
  }

  createDestination(): void {
    if (!this.validateForm()) {
      return;
    }

    this.isLoading = true;
    const destinationData = { ...this.destinationForm };
    
    this.destinationService.createDestinationAdmin(destinationData)
      .subscribe({
        next: (response: any) => {
          this.successMessage = 'Destino creado exitosamente';
          this.closeCreateModal();
          this.loadDestinations();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error creating destination:', error);
          this.errorMessage = 'Error al crear el destino: ' + (error.error?.message || 'Error desconocido');
          this.isLoading = false;
        }
      });
  }

  updateDestination(): void {
    if (!this.validateForm() || !this.selectedDestination?.id) {
      return;
    }

    this.isLoading = true;
    const destinationData = { ...this.destinationForm };
    
    this.destinationService.updateDestinationAdmin(this.selectedDestination.id, destinationData)
      .subscribe({
        next: (response: any) => {
          this.successMessage = 'Destino actualizado exitosamente';
          this.closeEditModal();
          this.loadDestinations();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error updating destination:', error);
          this.errorMessage = 'Error al actualizar el destino: ' + (error.error?.message || 'Error desconocido');
          this.isLoading = false;
        }
      });
  }

  /**
   * Desactiva un destino
   */
  deactivateDestination(): void {
    if (!this.selectedDestination?.id) {
      return;
    }

    this.isLoading = true;
    
    // Usar el endpoint de desactivación de destino (que devuelve texto)
    this.destinationService.deactivateDestination(this.selectedDestination.id)
      .subscribe({
        next: (response: string) => {
          console.log('Deactivation response:', response);
          this.successMessage = 'Destino desactivado exitosamente';
          this.closeDeactivateModal();
          this.loadDestinations();
          this.isLoading = false;
        },
        error: (error: any) => {
          console.error('Error deactivating destination:', error);
          
          // Si el error es de parsing pero el status es 200, considerarlo éxito
          if (error.status === 200) {
            this.successMessage = 'Destino desactivado exitosamente';
            this.closeDeactivateModal();
            this.loadDestinations();
          } else {
            this.errorMessage = 'Error al desactivar el destino: ' + (error.error?.message || error.message || 'Error desconocido');
          }
          this.isLoading = false;
        }
      });
  }

  // Modal Operations
  openCreateModal(): void {
    this.destinationForm = {
      name: '',
      description: '',
      country: '',
      imageUrl: '',
      active: true
    };
    this.clearFormErrors();
    this.showCreateModal = true;
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.clearFormErrors();
  }

  openEditModal(destination: AdminDestination): void {
    this.selectedDestination = { ...destination };
    this.destinationForm = {
      name: destination.name || '',
      description: destination.description || '',
      country: destination.country || '',
      imageUrl: destination.imageUrl || '',
      // Si no existe isActive, asumimos activo por defecto
      active: destination.isActive !== undefined ? destination.isActive : true
    };
    this.clearFormErrors();
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedDestination = null;
    this.clearFormErrors();
  }

  openDeactivateModal(destination: AdminDestination): void {
    this.selectedDestination = destination;
    this.showDeleteModal = true;
  }

  closeDeactivateModal(): void {
    this.showDeleteModal = false;
    this.selectedDestination = null;
  }

  closeModals(): void {
    this.closeCreateModal();
    this.closeEditModal();
    this.closeDeactivateModal();
  }

  // Form Validation
  validateForm(): boolean {
    this.clearFormErrors();
    let isValid = true;

    if (!this.destinationForm.name || this.destinationForm.name.trim().length < 2) {
      this.formErrors.name = 'El nombre debe tener al menos 2 caracteres';
      isValid = false;
    }

    if (!this.destinationForm.description || this.destinationForm.description.trim().length < 10) {
      this.formErrors.description = 'La descripción debe tener al menos 10 caracteres';
      isValid = false;
    }

    if (!this.destinationForm.country) {
      this.formErrors.country = 'Debe seleccionar un país';
      isValid = false;
    }

    return isValid;
  }

  clearFormErrors(): void {
    this.formErrors = {
      name: '',
      description: '',
      country: '',
      imageUrl: ''
    };
  }

  // Pagination
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

  /**
   * Obtiene las páginas visibles para la paginación (simplificada como en bookings y hoteles)
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

  // Filters
  toggleAdvancedFilters(): void {
    this.showAdvancedFilters = !this.showAdvancedFilters;
  }

  /**
   * Aplica filtros a los datos localmente
   */
  applyFiltersToData(): void {
    let filtered = [...this.allDestinations];

    // Filtro por búsqueda (nombre o descripción)
    if (this.filters.search && this.filters.search.trim()) {
      const searchTerm = this.filters.search.toLowerCase().trim();
      filtered = filtered.filter(dest => 
        dest.name.toLowerCase().includes(searchTerm) ||
        (dest.description && dest.description.toLowerCase().includes(searchTerm))
      );
    }

    // Filtro por país
    if (this.filters.country && this.filters.country.trim()) {
      filtered = filtered.filter(dest => dest.country === this.filters.country);
    }

    // Filtro por estado activo/inactivo
    if (this.filters.active !== null && this.filters.active !== undefined && this.filters.active !== '') {
      const isActiveFilter = this.filters.active === true || this.filters.active === 'true';
      filtered = filtered.filter(dest => dest.isActive === isActiveFilter);
    }

    this.filteredDestinations = filtered;
    
    // Aplicar paginación a los resultados filtrados
    this.applyPaginationToFiltered();
  }

  /**
   * Aplica paginación a los resultados filtrados
   */
  applyPaginationToFiltered(): void {
    const startIndex = this.currentPage * this.pageSize;
    const endIndex = startIndex + this.pageSize;
    
    this.destinations = this.filteredDestinations.slice(startIndex, endIndex);
    this.totalElements = this.filteredDestinations.length;
    this.totalPages = Math.ceil(this.totalElements / this.pageSize);
    
    // Si la página actual está fuera del rango, ir a la primera página
    if (this.currentPage >= this.totalPages && this.totalPages > 0) {
      this.currentPage = 0;
      this.applyPaginationToFiltered();
    }
  }

  /**
   * Aplica filtros cuando el usuario hace clic en "Aplicar Filtros"
   */
  applyFilters(): void {
    this.currentPage = 0; // Resetear a la primera página
    this.applyFiltersToData();
  }

  /**
   * Filtrado en tiempo real mientras el usuario escribe
   */
  onSearchChange(): void {
    this.currentPage = 0; // Resetear a la primera página
    this.applyFiltersToData();
  }

  clearFilters(): void {
    this.filters = {
      search: '',
      country: '',
      active: ''
    };
    this.currentPage = 0;
    this.applyFiltersToData();
  }

  // Utilities
  getPageNumbers(): number[] {
    const pages: number[] = [];
    const startPage = Math.max(0, this.currentPage - 2);
    const endPage = Math.min(this.totalPages - 1, this.currentPage + 2);
    
    for (let i = startPage; i <= endPage; i++) {
      pages.push(i);
    }
    
    return pages;
  }

  /**
   * Extrae países únicos de los destinos cargados y actualiza la lista de países
   * Los datos del país (código y nombre) vienen directamente del backend
   */
  extractUniqueCountries(): void {
    try {
      // Obtener países únicos de los destinos (asumiendo que el backend envía countryCode y countryName)
      const uniqueCountries = this.allDestinations
        .filter(dest => dest.country && dest.country.trim() !== '')
        .reduce((acc: { code: string, name: string }[], dest) => {
          const countryCode = dest.country;
          const countryName = dest.countryName || dest.country; // Usar countryName si está disponible, si no, usar el código
          
          // Solo agregar si no existe ya
          if (!acc.find(c => c.code === countryCode)) {
            acc.push({ code: countryCode, name: countryName });
          }
          return acc;
        }, []);

      this.countries = uniqueCountries;
      console.log('Extracted unique countries from backend data:', this.countries);
    } catch (error) {
      console.error('Error extracting countries:', error);
      // En caso de error, lista vacía para que se pueda cargar dinámicamente
      this.countries = [];
    }
  }

  // Helper methods for template
  getCountryName(countryCode: string): string {
    const country = this.countries.find(c => c.code === countryCode);
    return country ? country.name : countryCode;
  }

  clearError(): void {
    this.error = null;
    this.errorMessage = null;
  }

  clearSuccess(): void {
    this.successMessage = null;
  }
}

import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { 
  HotelReviewService, 
  HotelReview, 
  HotelReviewStats, 
  CreateHotelReviewRequest,
  UpdateHotelReviewRequest,
  ReviewableBooking,
  PageResponse
} from '../../core/services/hotel-review.service';
import { AuthService } from '../../core/services/auth.service';

/**
 * @class HotelReviewsComponent
 * @description Componente para gestionar reseñas de hoteles
 * Permite ver, crear, editar y eliminar reseñas
 */
@Component({
  selector: 'app-hotel-reviews',
  templateUrl: './hotel-reviews.component.html',
  styleUrls: ['./hotel-reviews.component.scss']
})
export class HotelReviewsComponent implements OnInit, OnDestroy {
  @Input() hotelId!: number;
  @Input() hotelName: string = '';
  @Input() showCreateButton: boolean = true;

  private destroy$ = new Subject<void>();

  // Estados
  isLoading = false;
  isLoadingStats = false;
  isSubmitting = false;
  errorMessage = '';
  successMessage = '';

  // Datos
  reviews: HotelReview[] = [];
  reviewStats: HotelReviewStats | null = null;
  reviewableBookings: ReviewableBooking[] = [];
  currentUser: any = null;

  // Paginación
  currentPage = 0;
  pageSize = 5;
  totalPages = 0;
  totalElements = 0;

  // Modales y formularios
  showCreateModal = false;
  showEditModal = false;
  createReviewForm: FormGroup;
  editReviewForm: FormGroup;
  selectedReview: HotelReview | null = null;
  selectedBooking: ReviewableBooking | null = null;

  // Filtros
  ratingFilter = 0;
  sortBy = 'newest';

  constructor(
    private reviewService: HotelReviewService,
    private authService: AuthService,
    private fb: FormBuilder
  ) {
    this.createReviewForm = this.createFormGroup();
    this.editReviewForm = this.createFormGroup();
  }

  ngOnInit(): void {
    this.loadCurrentUser();
    this.loadReviews();
    this.loadReviewStats();
    
    // Solo cargar reservas reseñables si el usuario está autenticado
    if (this.currentUser) {
      this.loadReviewableBookings();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private createFormGroup(): FormGroup {
    return this.fb.group({
      bookingId: [null, Validators.required],
      rating: [5, [Validators.required, Validators.min(1), Validators.max(5)]],
      title: ['', [Validators.required, Validators.maxLength(100)]],
      comment: ['', [Validators.required, Validators.maxLength(1000)]]
    });
  }

  private loadCurrentUser(): void {
    const user = this.authService.getCurrentUser();
    this.currentUser = user;
  }

  private loadReviews(): void {
    if (!this.hotelId) return;
    
    this.isLoading = true;
    this.reviewService.getHotelReviews(this.hotelId, this.currentPage, this.pageSize)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response: PageResponse<HotelReview>) => {
          this.reviews = response.content;
          this.totalPages = response.totalPages;
          this.totalElements = response.totalElements;
          this.isLoading = false;
        },
        error: (error) => {
          this.errorMessage = 'Error al cargar las reseñas';
          this.isLoading = false;
        }
      });
  }

  private loadReviewStats(): void {
    if (!this.hotelId) return;
    
    this.isLoadingStats = true;
    this.reviewService.getHotelReviewStats(this.hotelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.reviewStats = stats;
          
          // Asegurar que ratingDistribution esté inicializado
          if (this.reviewStats && !this.reviewStats.ratingDistribution) {
            this.reviewStats.ratingDistribution = {
              1: this.reviewStats.oneStar || 0,
              2: this.reviewStats.twoStars || 0,
              3: this.reviewStats.threeStars || 0,
              4: this.reviewStats.fourStars || 0,
              5: this.reviewStats.fiveStars || 0
            };
          }
          
          this.isLoadingStats = false;
        },
        error: (error) => {
          this.isLoadingStats = false;
        }
      });
  }

  private loadReviewableBookings(): void {
    if (!this.currentUser) return;
    
    this.reviewService.getReviewableBookings()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (bookings) => {
          this.reviewableBookings = bookings.filter(b => b.hotelId === this.hotelId);
        },
        error: (error) => {
          console.error('Error loading reviewable bookings:', error);
        }
      });
  }

  // Métodos de interfaz
  openCreateModal(): void {
    if (!this.currentUser) {
      this.errorMessage = 'Debes iniciar sesión para escribir una reseña';
      return;
    }
    
    if (this.reviewableBookings.length === 0) {
      this.errorMessage = 'No tienes reservas completadas para reseñar en este hotel';
      return;
    }
    
    this.createReviewForm.reset();
    this.createReviewForm.patchValue({ 
      rating: 5,
      bookingId: null
    });
    this.showCreateModal = true;
    this.errorMessage = '';
  }

  closeCreateModal(): void {
    this.showCreateModal = false;
    this.createReviewForm.reset();
  }

  openEditModal(review: HotelReview): void {
    this.selectedReview = review;
    this.editReviewForm.patchValue({
      rating: review.rating,
      title: review.title,
      comment: review.comment
    });
    this.showEditModal = true;
  }

  closeEditModal(): void {
    this.showEditModal = false;
    this.selectedReview = null;
    this.editReviewForm.reset();
  }

  onBookingSelected(): void {
    const bookingId = this.createReviewForm.get('bookingId')?.value;
    this.selectedBooking = this.reviewableBookings.find(b => b.bookingId === Number(bookingId)) || null;
  }

  submitCreateReview(): void {
    if (!this.createReviewForm.valid) return;
    
    this.isSubmitting = true;
    const formValue = this.createReviewForm.value;
    
    // Asegurar que bookingId sea un número válido
    const formData: CreateHotelReviewRequest = {
      bookingId: Number(formValue.bookingId),
      rating: formValue.rating,
      title: formValue.title,
      comment: formValue.comment
    };
    
    // Validar que bookingId no sea NaN
    if (isNaN(formData.bookingId) || formData.bookingId <= 0) {
      this.errorMessage = 'Debes seleccionar una reserva válida';
      this.isSubmitting = false;
      return;
    }
    
    this.reviewService.createReview(formData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (review) => {
          this.successMessage = 'Reseña creada exitosamente';
          this.closeCreateModal();
          this.loadReviews();
          this.loadReviewStats();
          this.loadReviewableBookings();
          this.isSubmitting = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al crear la reseña';
          this.isSubmitting = false;
        }
      });
  }

  submitEditReview(): void {
    if (!this.editReviewForm.valid || !this.selectedReview) return;
    
    this.isSubmitting = true;
    const formData = this.editReviewForm.value as UpdateHotelReviewRequest;
    
    this.reviewService.updateReview(this.selectedReview.id!, formData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (review) => {
          this.successMessage = 'Reseña actualizada exitosamente';
          this.closeEditModal();
          this.loadReviews();
          this.loadReviewStats();
          this.isSubmitting = false;
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al actualizar la reseña';
          this.isSubmitting = false;
        }
      });
  }

  deleteReview(review: HotelReview): void {
    if (!confirm('¿Estás seguro de que quieres eliminar esta reseña?')) return;
    
    this.reviewService.deleteReview(review.id!)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          this.successMessage = 'Reseña eliminada exitosamente';
          this.loadReviews();
          this.loadReviewStats();
          setTimeout(() => this.successMessage = '', 3000);
        },
        error: (error) => {
          this.errorMessage = error.error?.message || 'Error al eliminar la reseña';
        }
      });
  }

  // Métodos de utilidad
  canEditReview(review: HotelReview): boolean {
    // Solo el propietario de la reseña puede editarla
    return this.currentUser && 
           review.editable === true && 
           review.userId === this.currentUser.id;
  }

  /**
   * Método adicional de seguridad para verificar si el usuario puede eliminar una reseña
   * @param review La reseña a verificar
   * @returns true si el usuario puede eliminar la reseña
   */
  canDeleteReview(review: HotelReview): boolean {
    // Solo el propietario de la reseña puede eliminarla
    return this.currentUser && 
           review.userId === this.currentUser.id;
  }

  getStarArray(count: number): number[] {
    return Array(Math.max(0, Math.min(5, count))).fill(0).map((_, i) => i);
  }

  getFilledStars(rating: number): number[] {
    const rounded = Math.round(rating);
    return Array(Math.max(0, Math.min(5, rounded))).fill(0).map((_, i) => i);
  }

  getEmptyStars(rating: number): number[] {
    const rounded = Math.round(rating);
    const empty = 5 - rounded;
    return Array(Math.max(0, Math.min(5, empty))).fill(0).map((_, i) => i);
  }

  getRatingDistributionCount(rating: number): number {
    if (!this.reviewStats) return 0;
    
    // Construir el objeto ratingDistribution si no existe
    if (!this.reviewStats.ratingDistribution) {
      this.reviewStats.ratingDistribution = {
        1: this.reviewStats.oneStar || 0,
        2: this.reviewStats.twoStars || 0,
        3: this.reviewStats.threeStars || 0,
        4: this.reviewStats.fourStars || 0,
        5: this.reviewStats.fiveStars || 0
      };
    }
    
    return this.reviewStats.ratingDistribution[rating] || 0;
  }

  getRatingPercentage(rating: number): number {
    if (!this.reviewStats || this.reviewStats.totalReviews === 0) return 0;
    
    // Asegurarse de que ratingDistribution existe
    this.getRatingDistributionCount(rating);
    
    if (!this.reviewStats.ratingDistribution) return 0;
    return ((this.reviewStats.ratingDistribution[rating] || 0) / this.reviewStats.totalReviews) * 100;
  }

  changePage(page: number): void {
    this.currentPage = page;
    this.loadReviews();
  }

  applyFilters(): void {
    this.currentPage = 0;
    this.loadReviews();
  }

  clearMessages(): void {
    this.errorMessage = '';
    this.successMessage = '';
  }

  roundNumber(value: number): number {
    return Math.round(value);
  }
}

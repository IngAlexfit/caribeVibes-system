import { Component, Input, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { HotelReviewService, HotelReviewStats } from '../../core/services/hotel-review.service';

/**
 * @class HotelRatingDisplayComponent
 * @description Componente compacto para mostrar el rating y estadísticas de un hotel
 */
@Component({
  selector: 'app-hotel-rating-display',
  template: `
    <div class="hotel-rating-display" *ngIf="reviewStats">
      <div class="rating-summary">
        <div class="average-rating">
          <span class="rating-number">{{ reviewStats.averageRating | number:'1.1-1' }}</span>
          <div class="stars">
            <i *ngFor="let star of getStarArray(roundNumber(reviewStats.averageRating))" 
               class="fas fa-star text-warning"></i>
            <i *ngFor="let star of getStarArray(5 - roundNumber(reviewStats.averageRating))" 
               class="far fa-star text-muted"></i>
          </div>
        </div>
        <div class="review-count">
          <span class="count">{{ reviewStats.totalReviews }}</span>
          <span class="label">reseña{{ reviewStats.totalReviews !== 1 ? 's' : '' }}</span>
        </div>
      </div>
    </div>
    
    <div class="hotel-rating-display" *ngIf="!reviewStats && rating">
      <div class="rating-summary">
        <div class="average-rating">
          <span class="rating-number">{{ rating | number:'1.1-1' }}</span>
          <div class="stars">
            <i *ngFor="let star of getStarArray(roundNumber(rating))" 
               class="fas fa-star text-warning"></i>
            <i *ngFor="let star of getStarArray(5 - roundNumber(rating))" 
               class="far fa-star text-muted"></i>
          </div>
        </div>
      </div>
    </div>
  `,
  styles: [`
    .hotel-rating-display {
      .rating-summary {
        display: flex;
        align-items: center;
        gap: 10px;
        
        .average-rating {
          display: flex;
          align-items: center;
          gap: 5px;
          
          .rating-number {
            font-weight: 600;
            font-size: 1.1rem;
            color: #495057;
          }
          
          .stars {
            font-size: 0.9rem;
          }
        }
        
        .review-count {
          font-size: 0.85rem;
          color: #6c757d;
          
          .count {
            font-weight: 600;
          }
        }
      }
    }
  `]
})
export class HotelRatingDisplayComponent implements OnInit, OnDestroy {
  @Input() hotelId?: number;
  @Input() rating?: number; // Para mostrar rating básico sin estadísticas
  @Input() compact: boolean = false;

  private destroy$ = new Subject<void>();

  reviewStats: HotelReviewStats | null = null;
  isLoading = false;

  constructor(private reviewService: HotelReviewService) {}

  ngOnInit(): void {
    if (this.hotelId) {
      this.loadReviewStats();
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  private loadReviewStats(): void {
    if (!this.hotelId) return;
    
    this.isLoading = true;
    this.reviewService.getHotelReviewStats(this.hotelId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.reviewStats = stats;
          this.isLoading = false;
        },
        error: (error) => {
          this.isLoading = false;
        }
      });
  }

  getStarArray(rating: number): number[] {
    return Array(Math.max(0, Math.min(5, rating))).fill(0).map((_, i) => i);
  }

  roundNumber(value: number): number {
    return Math.round(value);
  }
}

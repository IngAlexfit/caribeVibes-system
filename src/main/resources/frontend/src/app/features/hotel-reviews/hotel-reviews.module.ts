import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule } from '@angular/forms';

import { HotelReviewsComponent } from './hotel-reviews.component';
import { HotelRatingDisplayComponent } from './hotel-rating-display.component';

/**
 * @class HotelReviewsModule
 * @description Módulo para el sistema de reseñas de hoteles
 */
@NgModule({
  declarations: [
    HotelReviewsComponent,
    HotelRatingDisplayComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    FormsModule
  ],
  exports: [
    HotelReviewsComponent,
    HotelRatingDisplayComponent
  ]
})
export class HotelReviewsModule { }

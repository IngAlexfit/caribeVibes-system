import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * @interface HotelReview
 * @description Representa una reseña de hotel
 */
export interface HotelReview {
  id?: number;
  rating: number;
  title: string;
  comment: string;
  createdAt?: string;
  updatedAt?: string;
  editable?: boolean;
  
  // Información del usuario (limitada por privacidad)
  userId?: number; // ID del usuario para validaciones de permisos
  userFirstName?: string;
  userLastInitial?: string;
  
  // Información básica del hotel
  hotelId: number;
  hotelName?: string;
  
  // Información de la reserva
  bookingId: number;
  bookingDate?: string;
  checkInDate?: string;
  checkOutDate?: string;
  guestName?: string;
  reviewDate?: string;
}

/**
 * @interface CreateHotelReviewRequest
 * @description DTO para crear una nueva reseña
 */
export interface CreateHotelReviewRequest {
  bookingId: number;
  rating: number;
  title: string;
  comment: string;
}

/**
 * @interface UpdateHotelReviewRequest
 * @description DTO para actualizar una reseña existente
 */
export interface UpdateHotelReviewRequest {
  rating: number;
  title: string;
  comment: string;
}

/**
 * @interface HotelReviewStats
 * @description Estadísticas de reseñas de un hotel
 */
export interface HotelReviewStats {
  hotelId: number;
  hotelName?: string;
  totalReviews: number;
  averageRating: number;
  
  // Distribución de calificaciones
  oneStar: number;
  twoStars: number;
  threeStars: number;
  fourStars: number;
  fiveStars: number;
  
  // Porcentajes de cada calificación
  oneStarPercent: number;
  twoStarsPercent: number;
  threeStarsPercent: number;
  fourStarsPercent: number;
  fiveStarsPercent: number;
  
  // Compatibilidad - distribución como objeto
  ratingDistribution?: { [key: number]: number };
}

/**
 * @interface ReviewableBooking
 * @description Reserva que puede ser reseñada
 */
export interface ReviewableBooking {
  bookingId: number;
  confirmationCode: string;
  bookingDate: string;
  checkInDate: string;
  checkOutDate: string;
  hotelId: number;
  hotelName: string;
  hotelImageUrl?: string;
  hotelStars?: number;
  numGuests: number;
  numRooms: number;
  totalPrice: number;
}

/**
 * @interface PageResponse
 * @description Respuesta paginada
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
}

/**
 * @class HotelReviewService
 * @description Servicio para gestionar reseñas de hoteles
 */
@Injectable({
  providedIn: 'root'
})
export class HotelReviewService {
  private readonly API_URL = `${environment.apiUrl}/hotel-reviews`;

  constructor(private http: HttpClient) {}

  /**
   * Crea una nueva reseña de hotel
   * @param review Datos de la reseña
   * @returns Observable con la reseña creada
   */
  createReview(review: CreateHotelReviewRequest): Observable<HotelReview> {
    return this.http.post<HotelReview>(this.API_URL, review);
  }

  /**
   * Actualiza una reseña existente
   * @param reviewId ID de la reseña
   * @param review Datos actualizados
   * @returns Observable con la reseña actualizada
   */
  updateReview(reviewId: number, review: UpdateHotelReviewRequest): Observable<HotelReview> {
    return this.http.put<HotelReview>(`${this.API_URL}/${reviewId}`, review);
  }

  /**
   * Elimina una reseña
   * @param reviewId ID de la reseña
   * @returns Observable void
   */
  deleteReview(reviewId: number): Observable<void> {
    return this.http.delete<void>(`${this.API_URL}/${reviewId}`);
  }

  /**
   * Obtiene todas las reseñas de un hotel
   * @param hotelId ID del hotel
   * @param page Página
   * @param size Tamaño de página
   * @returns Observable con reseñas paginadas
   */
  getHotelReviews(hotelId: number, page: number = 0, size: number = 10): Observable<PageResponse<HotelReview>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<HotelReview>>(`${this.API_URL}/hotel/${hotelId}`, { params });
  }

  /**
   * Obtiene las reseñas de un usuario
   * @param userId ID del usuario
   * @param page Página
   * @param size Tamaño de página
   * @returns Observable con reseñas paginadas
   */
  getUserReviews(userId: number, page: number = 0, size: number = 10): Observable<PageResponse<HotelReview>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<HotelReview>>(`${this.API_URL}/user/${userId}`, { params });
  }

  /**
   * Obtiene estadísticas de reseñas de un hotel
   * @param hotelId ID del hotel
   * @returns Observable con estadísticas
   */
  getHotelReviewStats(hotelId: number): Observable<HotelReviewStats> {
    return this.http.get<HotelReviewStats>(`${this.API_URL}/hotel/${hotelId}/stats`);
  }

  /**
   * Obtiene reservas que pueden ser reseñadas por un usuario
   * @returns Observable con reservas reseñables
   */
  getReviewableBookings(): Observable<ReviewableBooking[]> {
    return this.http.get<ReviewableBooking[]>(`${this.API_URL}/reviewable-bookings`);
  }

  /**
   * Busca reseñas por texto
   * @param searchTerm Término de búsqueda
   * @param page Página
   * @param size Tamaño de página
   * @returns Observable con reseñas encontradas
   */
  searchReviews(searchTerm: string, page: number = 0, size: number = 10): Observable<PageResponse<HotelReview>> {
    const params = new HttpParams()
      .set('searchTerm', searchTerm)
      .set('page', page.toString())
      .set('size', size.toString());
    
    return this.http.get<PageResponse<HotelReview>>(`${this.API_URL}/search`, { params });
  }

  /**
   * Verifica si un usuario puede reseñar un hotel
   * @param hotelId ID del hotel
   * @returns Observable boolean
   */
  canUserReviewHotel(hotelId: number): Observable<boolean> {
    return this.http.get<boolean>(`${this.API_URL}/can-review/${hotelId}`);
  }

  /**
   * Obtiene las reseñas realizadas por un usuario específico
   * @param userId ID del usuario
   * @returns Observable con las reseñas del usuario
   */
  getReviewsByUser(userId: number): Observable<HotelReview[]> {
    return this.http.get<HotelReview[]>(`${this.API_URL}/user/${userId}`);
  }
}

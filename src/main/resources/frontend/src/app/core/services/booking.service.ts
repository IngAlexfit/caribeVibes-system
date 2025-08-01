import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Booking, BookingRequest, BookingResponse, BookingActivity, BookingStatus } from '../models/booking.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../../environments/environment';

/**
 * @class BookingService
 * @description Servicio que gestiona todas las operaciones relacionadas con reservas.
 * Permite crear, consultar, cancelar y administrar reservas de hoteles.
 */
@Injectable({
  providedIn: 'root'
})
export class BookingService {
  /** @property {string} API_URL - URL base para las operaciones de API relacionadas con reservas */
  private readonly API_URL = `${environment.apiUrl}/bookings`;

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   */
  constructor(private http: HttpClient) { }

  /**
   * @method createBooking
   * @description Crea una nueva reserva de hotel
   * @param {BookingRequest} bookingRequest - Datos de la reserva a crear
   * @returns {Observable<BookingResponse>} Datos de la reserva creada
   */
  createBooking(bookingRequest: BookingRequest): Observable<BookingResponse> {
    return this.http.post<BookingResponse>(this.API_URL, bookingRequest);
  }

  /**
   * @method getMyBookings
   * @description Obtiene las reservas del usuario autenticado con paginación
   * @param {number} [page=0] - Número de página a consultar
   * @param {number} [size=10] - Cantidad de elementos por página
   * @returns {Observable<PageResponse<BookingResponse>>} Respuesta paginada con reservas
   */
  getMyBookings(page: number = 0, size: number = 10): Observable<PageResponse<BookingResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<BookingResponse>>(`${this.API_URL}/my-bookings`, { params });
  }

  /**
   * @method getBookingById
   * @description Obtiene una reserva específica por su ID
   * @param {number} id - ID de la reserva a consultar
   * @returns {Observable<BookingResponse>} Datos de la reserva
   */
  getBookingById(id: number): Observable<BookingResponse> {
    return this.http.get<BookingResponse>(`${this.API_URL}/${id}`);
  }

  /**
   * @method getBookingActivities
   * @description Obtiene las actividades asociadas a una reserva
   * @param {number} bookingId - ID de la reserva
   * @returns {Observable<BookingActivity[]>} Lista de actividades de la reserva
   */
  getBookingActivities(bookingId: number): Observable<BookingActivity[]> {
    return this.http.get<BookingActivity[]>(`${this.API_URL}/${bookingId}/activities`);
  }

  /**
   * @method addActivityToBooking
   * @description Agrega una actividad a una reserva existente
   * @param {number} bookingId - ID de la reserva
   * @param {number} activityId - ID de la actividad a agregar
   * @param {number} quantity - Cantidad de participantes
   * @returns {Observable<BookingActivity>} Actividad agregada a la reserva
   */
  addActivityToBooking(bookingId: number, activityId: number, quantity: number): Observable<BookingActivity> {
    const activityData = {
      activityId: activityId,
      quantity: quantity
    };
    return this.http.post<BookingActivity>(`${this.API_URL}/${bookingId}/activities`, activityData);
  }

  /**
   * @method removeActivityFromBooking
   * @description Remueve una actividad de una reserva
   * @param {number} bookingId - ID de la reserva
   * @param {number} activityId - ID de la actividad a remover
   * @returns {Observable<any>} Respuesta de la operación
   */
  removeActivityFromBooking(bookingId: number, activityId: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${bookingId}/activities/${activityId}`);
  }

  /**
   * @method cancelBooking
   * @description Cancela una reserva existente
   * @param {number} id - ID de la reserva a cancelar
   * @returns {Observable<any>} Respuesta de la operación
   */
  cancelBooking(id: number): Observable<any> {
    return this.http.put(`${this.API_URL}/${id}/cancel`, {});
  }

  /**
   * @method downloadVoucher
   * @description Descarga el voucher PDF de una reserva
   * @param {number} bookingId - ID de la reserva
   * @returns {Observable<Blob>} Archivo PDF como blob
   */
  downloadVoucher(bookingId: number): Observable<Blob> {
    return this.http.get(`${this.API_URL}/${bookingId}/voucher`, { 
      responseType: 'blob' 
    });
  }

  // ===========================================
  // MÉTODOS DE ADMINISTRACIÓN
  // ===========================================

  /**
   * @method getAllBookings
   * @description Obtiene todas las reservas del sistema (solo administradores)
   * @param {number} [page=0] - Número de página a consultar
   * @param {number} [size=10] - Cantidad de elementos por página
   * @param {string} [sortBy='bookingDate'] - Campo de ordenamiento
   * @param {string} [sortDir='desc'] - Dirección de ordenamiento
   * @returns {Observable<PageResponse<BookingResponse>>} Respuesta paginada con todas las reservas
   */
  getAllBookings(
    page: number = 0, 
    size: number = 10, 
    sortBy: string = 'bookingDate', 
    sortDir: string = 'desc'
  ): Observable<PageResponse<BookingResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<BookingResponse>>(this.API_URL, { params });
  }

  /**
   * @method confirmBooking
   * @description Confirma una reserva pendiente (solo administradores)
   * @param {number} bookingId - ID de la reserva a confirmar
   * @returns {Observable<{message: string, status: string}>} Confirmación de la operación
   */
  confirmBooking(bookingId: number): Observable<{message: string, status: string}> {
    return this.http.put<{message: string, status: string}>(`${this.API_URL}/${bookingId}/confirm`, {});
  }

  /**
   * @method completeBooking
   * @description Marca una reserva como completada (solo administradores)
   * @param {number} bookingId - ID de la reserva a completar
   * @returns {Observable<{message: string, status: string}>} Confirmación de la operación
   */
  completeBooking(bookingId: number): Observable<{message: string, status: string}> {
    return this.http.put<{message: string, status: string}>(`${this.API_URL}/${bookingId}/complete`, {});
  }

  /**
   * @method exportBookings
   * @description Exporta todas las reservas a un archivo (solo administradores)
   * @param {string} [format='xlsx'] - Formato de exportación (xlsx, csv, pdf)
   * @returns {Observable<Blob>} Archivo de exportación como blob
   */
  exportBookings(format: string = 'xlsx'): Observable<Blob> {
    const params = new HttpParams().set('format', format);
    return this.http.get(`${this.API_URL}/export`, { 
      params,
      responseType: 'blob' 
    });
  }

  /**
   * @method getBookingsByStatus
   * @description Obtiene reservas filtradas por estado (solo para administradores)
   * @param {BookingStatus} status - Estado por el cual filtrar
   * @param {number} [page=0] - Número de página a consultar
   * @param {number} [size=10] - Cantidad de elementos por página
   * @returns {Observable<PageResponse<BookingResponse>>} Respuesta paginada con reservas
   */
  getBookingsByStatus(status: BookingStatus, page: number = 0, size: number = 10): Observable<PageResponse<BookingResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<BookingResponse>>(`${this.API_URL}/by-status/${status}`, { params });
  }

  /**
   * @method getUpcomingBookings
   * @description Obtiene las reservas próximas (solo para administradores)
   * @param {number} [page=0] - Número de página a consultar
   * @param {number} [size=10] - Cantidad de elementos por página
   * @returns {Observable<PageResponse<BookingResponse>>} Respuesta paginada con reservas
   */
  getUpcomingBookings(page: number = 0, size: number = 10): Observable<PageResponse<BookingResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<BookingResponse>>(`${this.API_URL}/upcoming`, { params });
  }

}

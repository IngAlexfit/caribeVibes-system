import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Hotel, HotelResponse, RoomType } from '../models/hotel.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../../environments/environment';

/**
 * @class HotelService
 * @description Servicio que gestiona todas las operaciones relacionadas con hoteles.
 * Permite consultar, buscar y administrar hoteles y sus habitaciones.
 */
@Injectable({
  providedIn: 'root'
})
export class HotelService {
  /** @property {string} API_URL - URL base para las operaciones de API relacionadas con hoteles */
  private readonly API_URL = `${environment.apiUrl}/hotels`;

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   */
  constructor(private http: HttpClient) { }

  /**
   * @method getAllHotels
   * @description Obtiene un listado paginado de todos los hoteles disponibles
   * @param {number} [page=0] - Número de página (comienza en 0)
   * @param {number} [size=10] - Tamaño de página
   * @param {string} [sortBy='name'] - Campo por el cual ordenar
   * @param {string} [sortDir='asc'] - Dirección de ordenamiento (asc/desc)
   * @returns {Observable<PageResponse<HotelResponse>>} Observable con la respuesta paginada
   */
  getAllHotels(page: number = 0, size: number = 10, sortBy: string = 'name', sortDir: string = 'asc'): Observable<PageResponse<HotelResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<HotelResponse>>(this.API_URL, { params });
  }

  /**
   * @method getHotelById
   * @description Obtiene los detalles de un hotel específico por su ID
   * @param {number} id - ID del hotel a consultar
   * @returns {Observable<HotelResponse>} Observable con información del hotel
   */
  getHotelById(id: number): Observable<HotelResponse> {
    return this.http.get<HotelResponse>(`${this.API_URL}/${id}`);
  }

  /**
   * @method getHotelsByDestination
   * @description Obtiene hoteles filtrados por destino
   * @param {number} destinationId - ID del destino
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<HotelResponse>>} Observable con la respuesta paginada
   */
  getHotelsByDestination(destinationId: number, page: number = 0, size: number = 10): Observable<PageResponse<HotelResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<HotelResponse>>(`${this.API_URL}/by-destination/${destinationId}`, { params });
  }

  /**
   * @method getHotelsByStars
   * @description Obtiene hoteles filtrados por número de estrellas
   * @param {number} stars - Cantidad de estrellas
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<HotelResponse>>} Observable con la respuesta paginada
   */
  getHotelsByStars(stars: number, page: number = 0, size: number = 10): Observable<PageResponse<HotelResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<HotelResponse>>(`${this.API_URL}/by-stars/${stars}`, { params });
  }

  /**
   * @method searchHotelsByName
   * @description Busca hoteles por nombre
   * @param {string} name - Término de búsqueda
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<HotelResponse>>} Observable con la respuesta paginada
   */
  searchHotelsByName(name: string, page: number = 0, size: number = 10): Observable<PageResponse<HotelResponse>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<HotelResponse>>(`${this.API_URL}/search`, { params });
  }

  /**
   * @method getTopRatedHotels
   * @description Obtiene los hoteles mejor calificados
   * @param {number} [limit=10] - Número máximo de hoteles a retornar
   * @returns {Observable<HotelResponse[]>} Observable con el listado de hoteles
   */
  getTopRatedHotels(limit: number = 10): Observable<HotelResponse[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<HotelResponse[]>(`${this.API_URL}/top-rated`, { params });
  }

  /**
   * @method getHotelRoomTypes
   * @description Obtiene los tipos de habitación disponibles para un hotel
   * @param {number} hotelId - ID del hotel
   * @returns {Observable<RoomType[]>} Observable con los tipos de habitación
   */
  getHotelRoomTypes(hotelId: number): Observable<RoomType[]> {
    return this.http.get<RoomType[]>(`${this.API_URL}/${hotelId}/room-types`);
  }

  /**
   * @method createRoomType
   * @description Crea un nuevo tipo de habitación para un hotel (solo para administradores)
   * @param {number} hotelId - ID del hotel
   * @param {any} roomType - Datos del tipo de habitación a crear
   * @returns {Observable<RoomType>} Observable con el tipo de habitación creado
   */
  createRoomType(hotelId: number, roomType: any): Observable<RoomType> {
    return this.http.post<RoomType>(`${this.API_URL}/${hotelId}/room-types`, roomType);
  }

  /**
   * @method updateRoomType
   * @description Actualiza un tipo de habitación existente (solo para administradores)
   * @param {number} hotelId - ID del hotel
   * @param {number} roomTypeId - ID del tipo de habitación a actualizar
   * @param {any} roomType - Nuevos datos del tipo de habitación
   * @returns {Observable<RoomType>} Observable con el tipo de habitación actualizado
   */
  updateRoomType(hotelId: number, roomTypeId: number, roomType: any): Observable<RoomType> {
    return this.http.put<RoomType>(`${this.API_URL}/${hotelId}/room-types/${roomTypeId}`, roomType);
  }

  /**
   * @method deleteRoomType
   * @description Elimina un tipo de habitación (solo para administradores)
   * @param {number} hotelId - ID del hotel
   * @param {number} roomTypeId - ID del tipo de habitación a eliminar
   * @returns {Observable<string>} Observable con mensaje de confirmación
   */
  deleteRoomType(hotelId: number, roomTypeId: number): Observable<string> {
    return this.http.delete<string>(`${this.API_URL}/${hotelId}/room-types/${roomTypeId}`);
  }

  /**
   * @method getRoomTypeById
   * @description Obtiene un tipo de habitación específico por su ID
   * @param {number} roomTypeId - ID del tipo de habitación
   * @returns {Observable<RoomType>} Observable con el tipo de habitación
   */
  getRoomTypeById(roomTypeId: number): Observable<RoomType> {
    return this.http.get<RoomType>(`${this.API_URL}/room-types/${roomTypeId}`);
  }

  /**
   * @method createHotel
   * @description Crea un nuevo hotel (solo para administradores)
   * @param {any} hotel - Datos del hotel a crear
   * @returns {Observable<HotelResponse>} Observable con el hotel creado
   */
  createHotel(hotel: any): Observable<HotelResponse> {
    return this.http.post<HotelResponse>(this.API_URL, hotel);
  }

  /**
   * @method updateHotel
   * @description Actualiza la información de un hotel existente (solo para administradores)
   * @param {number} id - ID del hotel a actualizar
   * @param {any} hotel - Nuevos datos del hotel
   * @returns {Observable<HotelResponse>} Observable con el hotel actualizado
   */
  updateHotel(id: number, hotel: any): Observable<HotelResponse> {
    return this.http.put<HotelResponse>(`${this.API_URL}/${id}`, hotel);
  }

  /**
   * @method deactivateHotel
   * @description Desactiva un hotel (solo para administradores)
   * @param {number} id - ID del hotel a desactivar
   * @returns {Observable<string>} Observable con mensaje de confirmación
   */
  deactivateHotel(id: number): Observable<string> {
    return this.http.delete(`${this.API_URL}/${id}`, { 
      responseType: 'text' 
    });
  }

  /**
   * @method getAllHotelsForAdmin
   * @description Obtiene un listado paginado de todos los hoteles (incluidos inactivos) para administración
   * @param {number} [page=0] - Número de página (comienza en 0)
   * @param {number} [size=10] - Tamaño de página
   * @param {string} [sortBy='name'] - Campo por el cual ordenar
   * @param {string} [sortDir='asc'] - Dirección de ordenamiento (asc/desc)
   * @returns {Observable<PageResponse<HotelResponse>>} Observable con la respuesta paginada
   */
  getAllHotelsForAdmin(page: number = 0, size: number = 10, sortBy: string = 'name', sortDir: string = 'asc'): Observable<PageResponse<HotelResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<HotelResponse>>(`${this.API_URL}/admin/all`, { params });
  }
}

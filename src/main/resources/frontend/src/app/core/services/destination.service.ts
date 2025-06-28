import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Destination, DestinationResponse, Activity, Experience } from '../models/destination.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../../environments/environment';

/**
 * @class DestinationService
 * @description Servicio que gestiona todas las operaciones relacionadas con destinos turísticos.
 * Permite consultar, buscar y administrar destinos, actividades y experiencias.
 */
@Injectable({
  providedIn: 'root'
})
export class DestinationService {
  /** @property {string} API_URL - URL base para las operaciones de API relacionadas con destinos */
  private readonly API_URL = `${environment.apiUrl}/destinations`;

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   */
  constructor(private http: HttpClient) { }

  /**
   * @method getAllDestinations
   * @description Obtiene un listado paginado de todos los destinos disponibles
   * @param {number} [page=0] - Número de página (comienza en 0)
   * @param {number} [size=10] - Tamaño de página
   * @param {string} [sortBy='name'] - Campo por el cual ordenar
   * @param {string} [sortDir='asc'] - Dirección de ordenamiento (asc/desc)
   * @returns {Observable<PageResponse<DestinationResponse>>} Observable con la respuesta paginada
   */
  getAllDestinations(page: number = 0, size: number = 10, sortBy: string = 'name', sortDir: string = 'asc'): Observable<PageResponse<DestinationResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<DestinationResponse>>(this.API_URL, { params });
  }

  /**
   * @method getDestinationById
   * @description Obtiene los detalles de un destino específico por su ID
   * @param {number} id - ID del destino a consultar
   * @returns {Observable<DestinationResponse>} Observable con información del destino
   */
  getDestinationById(id: number): Observable<DestinationResponse> {
    return this.http.get<DestinationResponse>(`${this.API_URL}/${id}`);
  }

  /**
   * @method getPopularDestinations
   * @description Obtiene un listado de los destinos más populares
   * @param {number} [limit=10] - Número máximo de destinos a retornar
   * @returns {Observable<DestinationResponse[]>} Observable con el listado de destinos populares
   */
  getPopularDestinations(limit: number = 10): Observable<DestinationResponse[]> {
    const params = new HttpParams().set('limit', limit.toString());
    return this.http.get<DestinationResponse[]>(`${this.API_URL}/popular`, { params });
  }

  /**
   * @method getDestinationsByCountry
   * @description Obtiene destinos filtrados por país
   * @param {string} country - Nombre del país
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<DestinationResponse>>} Observable con la respuesta paginada
   */
  getDestinationsByCountry(country: string, page: number = 0, size: number = 10): Observable<PageResponse<DestinationResponse>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<DestinationResponse>>(`${this.API_URL}/by-country/${country}`, { params });
  }

  /**
   * @method searchDestinationsByName
   * @description Busca destinos por nombre
   * @param {string} name - Término de búsqueda
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<DestinationResponse>>} Observable con la respuesta paginada
   */
  searchDestinationsByName(name: string, page: number = 0, size: number = 10): Observable<PageResponse<DestinationResponse>> {
    const params = new HttpParams()
      .set('name', name)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<DestinationResponse>>(`${this.API_URL}/search`, { params });
  }

  /**
   * @method getDestinationActivities
   * @description Obtiene las actividades disponibles en un destino específico
   * @param {number} destinationId - ID del destino
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<Activity>>} Observable con la respuesta paginada
   */
  getDestinationActivities(destinationId: number, page: number = 0, size: number = 10): Observable<PageResponse<Activity>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Activity>>(`${this.API_URL}/${destinationId}/activities`, { params });
  }

  /**
   * @method getActivityById
   * @description Obtiene los detalles de una actividad específica por su ID
   * @param {number} activityId - ID de la actividad
   * @returns {Observable<Activity>} Observable con información de la actividad
   */
  getActivityById(activityId: number): Observable<Activity> {
    return this.http.get<Activity>(`${this.API_URL}/activities/${activityId}`);
  }

  /**
   * @method getExperienceById
   * @description Obtiene los detalles de una experiencia específica por su ID
   * @param {number} experienceId - ID de la experiencia
   * @returns {Observable<Experience>} Observable con información de la experiencia
   */
  getExperienceById(experienceId: number): Observable<Experience> {
    return this.http.get<Experience>(`${this.API_URL}/experiences/${experienceId}`);
  }

  /**
   * @method createDestination
   * @description Crea un nuevo destino (solo para administradores)
   * @param {Destination} destination - Datos del destino a crear
   * @returns {Observable<Destination>} Observable con el destino creado
   */
  createDestination(destination: Destination): Observable<Destination> {
    return this.http.post<Destination>(this.API_URL, destination);
  }

  /**
   * @method updateDestination
   * @description Actualiza la información de un destino existente (solo para administradores)
   * @param {number} id - ID del destino a actualizar
   * @param {Destination} destination - Nuevos datos del destino
   * @returns {Observable<Destination>} Observable con el destino actualizado
   */
  updateDestination(id: number, destination: Destination): Observable<Destination> {
    return this.http.put<Destination>(`${this.API_URL}/${id}`, destination);
  }

  /**
   * @method deactivateDestination
   * @description Desactiva un destino (solo para administradores)
   * @param {number} id - ID del destino a desactivar
   * @returns {Observable<string>} Observable con mensaje de confirmación
   */
  deactivateDestination(id: number): Observable<string> {
    return this.http.delete<string>(`${this.API_URL}/${id}`);
  }
}

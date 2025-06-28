import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contact, ContactRequest, ContactStatus } from '../models/common.model';
import { PageResponse } from '../models/common.model';
import { environment } from '../../../environments/environment';

/**
 * @class ContactService
 * @description Servicio que gestiona todas las operaciones relacionadas con mensajes de contacto.
 * Permite a usuarios enviar mensajes y a administradores gestionar estos mensajes.
 */
@Injectable({
  providedIn: 'root'
})
export class ContactService {
  /** @property {string} API_URL - URL base para las operaciones de API relacionadas con contactos */
  private readonly API_URL = `${environment.apiUrl}/contact`;

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   */
  constructor(private http: HttpClient) { }

  /**
   * @method createContact
   * @description Envía un nuevo mensaje de contacto desde un usuario
   * @param {ContactRequest} contactRequest - Datos del mensaje de contacto
   * @returns {Observable<any>} Observable con la respuesta del servidor
   */
  createContact(contactRequest: ContactRequest): Observable<any> {
    return this.http.post(`${this.API_URL}`, contactRequest);
  }

  /**
   * @method getAllContacts
   * @description Obtiene todos los mensajes de contacto (solo para administradores)
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @param {string} [sortBy='createdAt'] - Campo por el cual ordenar
   * @param {string} [sortDir='desc'] - Dirección de ordenamiento (asc/desc)
   * @returns {Observable<PageResponse<Contact>>} Observable con la respuesta paginada
   */
  getAllContacts(page: number = 0, size: number = 10, sortBy: string = 'createdAt', sortDir: string = 'desc'): Observable<PageResponse<Contact>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString())
      .set('sortBy', sortBy)
      .set('sortDir', sortDir);

    return this.http.get<PageResponse<Contact>>(this.API_URL, { params });
  }

  /**
   * @method getContactById
   * @description Obtiene un mensaje de contacto específico por su ID
   * @param {number} id - ID del mensaje
   * @returns {Observable<Contact>} Observable con el mensaje de contacto
   */
  getContactById(id: number): Observable<Contact> {
    return this.http.get<Contact>(`${this.API_URL}/${id}`);
  }

  /**
   * @method getContactsByStatus
   * @description Obtiene mensajes de contacto filtrados por estado
   * @param {ContactStatus} status - Estado de los mensajes a filtrar
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<Contact>>} Observable con la respuesta paginada
   */
  getContactsByStatus(status: ContactStatus, page: number = 0, size: number = 10): Observable<PageResponse<Contact>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Contact>>(`${this.API_URL}/by-status/${status}`, { params });
  }

  /**
   * @method getNewContacts
   * @description Obtiene los mensajes de contacto nuevos (sin leer)
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<Contact>>} Observable con la respuesta paginada
   */
  getNewContacts(page: number = 0, size: number = 10): Observable<PageResponse<Contact>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Contact>>(`${this.API_URL}/new`, { params });
  }

  /**
   * @method getRecentContacts
   * @description Obtiene los mensajes de contacto más recientes
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<Contact>>} Observable con la respuesta paginada
   */
  getRecentContacts(page: number = 0, size: number = 10): Observable<PageResponse<Contact>> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Contact>>(`${this.API_URL}/recent`, { params });
  }

  /**
   * @method searchContacts
   * @description Busca mensajes de contacto por término de búsqueda
   * @param {string} searchTerm - Término a buscar
   * @param {number} [page=0] - Número de página
   * @param {number} [size=10] - Tamaño de página
   * @returns {Observable<PageResponse<Contact>>} Observable con la respuesta paginada
   */
  searchContacts(searchTerm: string, page: number = 0, size: number = 10): Observable<PageResponse<Contact>> {
    const params = new HttpParams()
      .set('searchTerm', searchTerm)
      .set('page', page.toString())
      .set('size', size.toString());

    return this.http.get<PageResponse<Contact>>(`${this.API_URL}/search`, { params });
  }

  /**
   * @method updateContactStatus
   * @description Actualiza el estado de un mensaje de contacto
   * @param {number} id - ID del mensaje
   * @param {ContactStatus} status - Nuevo estado
   * @returns {Observable<Contact>} Observable con el mensaje actualizado
   */
  updateContactStatus(id: number, status: ContactStatus): Observable<Contact> {
    return this.http.put<Contact>(`${this.API_URL}/${id}/status`, { status });
  }

  /**
   * @method replyToContact
   * @description Responde a un mensaje de contacto
   * @param {number} id - ID del mensaje
   * @param {string} reply - Texto de la respuesta
   * @returns {Observable<any>} Observable con la respuesta del servidor
   */
  replyToContact(id: number, reply: string): Observable<any> {
    return this.http.post<any>(`${this.API_URL}/${id}/reply`, { reply });
  }

  /**
   * @method deleteContact
   * @description Elimina un mensaje de contacto
   * @param {number} id - ID del mensaje a eliminar
   * @returns {Observable<any>} Observable con la respuesta del servidor
   */
  deleteContact(id: number): Observable<any> {
    return this.http.delete(`${this.API_URL}/${id}`);
  }
}

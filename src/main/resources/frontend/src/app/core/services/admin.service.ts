import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../../../environments/environment';

/**
 * @interface CacheStatistics
 * @description Estadísticas del sistema de caché
 */
export interface CacheStatistics {
  /** Número total de cachés */
  totalCaches: number;
  /** Cachés activos */
  activeCaches: number;
  /** Tamaño total en memoria */
  totalSizeBytes: number;
  /** Estadísticas por caché */
  cacheDetails: CacheDetail[];
}

/**
 * @interface CacheDetail
 * @description Detalles de un caché específico
 */
export interface CacheDetail {
  /** Nombre del caché */
  name: string;
  /** Número de entradas */
  size: number;
  /** Tamaño en bytes */
  sizeBytes: number;
  /** Tasa de aciertos */
  hitRate: number;
  /** Estado activo/inactivo */
  active: boolean;
}

/**
 * @interface SystemStats
 * @description Estadísticas generales del sistema
 */
export interface SystemStats {
  /** Total de usuarios registrados */
  totalUsers: number;
  /** Total de reservas */
  totalBookings: number;
  /** Total de mensajes de contacto */
  totalContacts: number;
  /** Total de hoteles */
  totalHotels: number;
  /** Total de destinos */
  totalDestinations: number;
  /** Reservas del último mes */
  monthlyBookings: number;
  /** Nuevos usuarios del último mes */
  monthlyUsers: number;
  /** Mensajes nuevos */
  newContacts: number;
}

/**
 * @class AdminService
 * @description Servicio que gestiona todas las operaciones de administración del sistema.
 * Proporciona funcionalidades para gestionar caché, estadísticas y operaciones administrativas.
 */
@Injectable({
  providedIn: 'root'
})
export class AdminService {
  /** @property {string} API_URL - URL base para las operaciones de API de administración */
  private readonly API_URL = `${environment.apiUrl}/admin`;

  /**
   * @constructor
   * @param {HttpClient} http - Cliente HTTP para realizar peticiones a la API
   */
  constructor(private http: HttpClient) { }

  /**
   * @method clearAllCaches
   * @description Limpia todos los cachés del sistema
   * @returns {Observable<any>} Respuesta de la operación
   */
  clearAllCaches(): Observable<any> {
    return this.http.post(`${this.API_URL}/cache/clear-all`, {});
  }

  /**
   * @method clearSpecificCache
   * @description Limpia un caché específico por nombre
   * @param {string} cacheName - Nombre del caché a limpiar
   * @returns {Observable<any>} Respuesta de la operación
   */
  clearSpecificCache(cacheName: string): Observable<any> {
    return this.http.post(`${this.API_URL}/cache/clear/${cacheName}`, {});
  }

  /**
   * @method getCacheStatistics
   * @description Obtiene información completa de todos los cachés
   * @returns {Observable<any>} Información de cachés
   */
  getCacheStatistics(): Observable<any> {
    return this.http.get(`${this.API_URL}/cache/info`);
  }

  /**
   * @method getCacheInfo
   * @description Obtiene información de todos los cachés
   * @returns {Observable<any>} Información de cachés
   */
  getCacheInfo(): Observable<any> {
    return this.http.get(`${this.API_URL}/cache/info`);
  }

  /**
   * @method getContactStatistics
   * @description Obtiene estadísticas de los mensajes de contacto
   * @returns {Observable<any>} Estadísticas de contactos
   */
  getContactStatistics(): Observable<any> {
    return this.http.get(`${environment.apiUrl}/contact/statistics`);
  }

  /**
   * @method getSystemStatistics
   * @description Obtiene estadísticas generales del sistema (nuevo endpoint)
   * @returns {Observable<any>} Estadísticas del sistema
   */
  getSystemStatistics(): Observable<any> {
    return this.http.get(`${this.API_URL}/statistics`);
  }

  /**
   * @method getCacheHealth
   * @description Verifica el estado de salud de los cachés
   * @returns {Observable<any>} Estado de salud de los cachés
   */
  getCacheHealth(): Observable<any> {
    return this.http.get(`${this.API_URL}/cache/health`);
  }

  /**
   * @method exportData
   * @description Exporta datos del sistema en formato especificado
   * @param {string} format - Formato de exportación (csv, xlsx, json)
   * @param {string} dataType - Tipo de datos a exportar (bookings, contacts, users)
   * @returns {Observable<Blob>} Archivo de exportación
   */
  exportData(format: string, dataType: string): Observable<Blob> {
    return this.http.get(`${this.API_URL}/export/${dataType}?format=${format}`, {
      responseType: 'blob'
    });
  }

  /**
   * @method getDatabaseInfo
   * @description Obtiene información de la base de datos
   * @returns {Observable<any>} Información de la base de datos
   */
  getDatabaseInfo(): Observable<any> {
    return this.http.get(`${this.API_URL}/database/info`);
  }

  /**
   * @method runSystemMaintenance
   * @description Ejecuta tareas de mantenimiento del sistema
   * @returns {Observable<any>} Resultado de las tareas de mantenimiento
   */
  runSystemMaintenance(): Observable<any> {
    return this.http.post(`${this.API_URL}/maintenance/run`, {});
  }
}

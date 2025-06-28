import { Injectable } from '@angular/core';
import { HttpHeaders } from '@angular/common/http';
import { environment } from '../../../environments/environment';

/**
 * @class ApiBaseService
 * @description Servicio base que proporciona funcionalidades comunes para todos los servicios API.
 * Incluye métodos para configurar cabeceras HTTP, generar URLs API y funciones de logging.
 */
@Injectable({
  providedIn: 'root'
})
export class ApiBaseService {

  /**
   * @method getHttpOptions
   * @description Obtiene las opciones HTTP estándar para peticiones API
   * @returns {object} Objeto con las cabeceras HTTP configuradas
   */
  protected getHttpOptions() {
    return {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        'Accept': 'application/json',
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Allow-Methods': 'GET, POST, PUT, DELETE, OPTIONS',
        'Access-Control-Allow-Headers': 'Content-Type, Authorization'
      })
    };
  }

  /**
   * @method getApiUrl
   * @description Construye la URL completa para un endpoint API
   * @param {string} endpoint - Ruta del endpoint sin incluir la URL base
   * @returns {string} URL completa del endpoint
   */
  protected getApiUrl(endpoint: string): string {
    const url = `${environment.apiUrl}${endpoint}`;
    console.log(`[API] Making request to: ${url}`);
    return url;
  }

  /**
   * @method logRequest
   * @description Registra en consola los detalles de una petición API
   * @param {string} method - Método HTTP (GET, POST, PUT, DELETE)
   * @param {string} url - URL de la petición
   * @param {any} [data] - Datos opcionales de la petición
   */
  protected logRequest(method: string, url: string, data?: any) {
    console.log(`[API ${method}] ${url}`);
    if (data) {
      console.log(`[API ${method}] Data:`, data);
    }
  }

  /**
   * @method logResponse
   * @description Registra en consola los detalles de una respuesta API
   * @param {string} method - Método HTTP de la petición original
   * @param {string} url - URL de la petición original
   * @param {any} response - Respuesta recibida del servidor
   */
  protected logResponse(method: string, url: string, response: any) {
    console.log(`[API ${method}] Response from ${url}:`, response);
  }

  /**
   * @method logError
   * @description Registra en consola los errores de una petición API
   * @param {string} method - Método HTTP de la petición original
   * @param {string} url - URL de la petición original
   * @param {any} error - Objeto de error recibido
   */
  protected logError(method: string, url: string, error: any) {
    console.error(`[API ${method}] Error from ${url}:`, error);
    console.error(`[API ${method}] Error status:`, error.status);
    console.error(`[API ${method}] Error message:`, error.message);
  }
}

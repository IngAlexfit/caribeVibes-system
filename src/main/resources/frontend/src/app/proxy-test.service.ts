import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

/**
 * @class ProxyTestService
 * @description Servicio para probar la conexión y funcionalidad del proxy con el backend.
 * Proporciona métodos para verificar la comunicación entre el frontend y la API.
 */
@Injectable({
  providedIn: 'root'
})
export class ProxyTestService {

  /**
   * @constructor
   * @param {HttpClient} http - Servicio HttpClient de Angular para realizar peticiones HTTP
   */
  constructor(private http: HttpClient) { }

  /**
   * @method testEndpoint
   * @description Realiza una prueba básica al endpoint de test de la API
   * @returns {Observable<any>} Observable con la respuesta del servidor
   */
  testEndpoint(): Observable<any> {
    console.log('Testing proxy with HttpClient...');
    console.log('Making request to: /api/test');
    
    return this.http.get('/api/test');
  }

  /**
   * @method testRegister
   * @description Prueba el endpoint de registro enviando datos de usuario de prueba
   * @returns {Observable<any>} Observable con la respuesta del servidor al intento de registro
   */
  testRegister(): Observable<any> {
    console.log('Testing register endpoint...');
    console.log('Making POST request to: /api/auth/register');
    
    const testData = {
      firstName: 'Test',
      lastName: 'User',
      username: 'testuser123',
      email: 'test@example.com',
      password: 'password123',
      interests: ['Playa'],
      adventures: ['Buceo']
    };
    
    return this.http.post('/api/auth/register', testData);
  }

  /**
   * @method testHealth
   * @description Verifica la salud y disponibilidad del backend mediante el endpoint de health check
   * @returns {Observable<any>} Observable con el estado de salud del servidor
   */
  testHealth(): Observable<any> {
    console.log('Testing health endpoint...');
    console.log('Making GET request to: /api/health');
    
    return this.http.get('/api/health');
  }
}

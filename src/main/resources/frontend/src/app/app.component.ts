import { Component, OnInit } from '@angular/core';
import { AuthService } from './core/services/auth.service';
import { ProxyTestService } from './proxy-test.service';

/**
 * @class AppComponent
 * @description Componente principal de la aplicación Caribe Vibes.
 * Actúa como el punto de entrada y controla el estado de carga inicial.
 */
@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit {
  /** @property {string} title - Título de la aplicación */
  title = 'Caribe Vibes';
  
  /** @property {boolean} isLoading - Indica si la aplicación está en estado de carga inicial */
  isLoading = true;

  /**
   * @constructor
   * @param {AuthService} authService - Servicio de autenticación para verificar el token
   * @param {ProxyTestService} proxyTestService - Servicio para probar la conexión con el backend
   */
  constructor(
    private authService: AuthService,
    private proxyTestService: ProxyTestService
  ) {}

  /**
   * @method ngOnInit
   * @description Método del ciclo de vida que se ejecuta al inicializar el componente
   * Simula una carga inicial, verifica la validez del token y prueba la conexión con el backend
   */
  ngOnInit(): void {
    // Simular carga inicial
    setTimeout(() => {
      this.isLoading = false;
    }, 1000);

    // Verificar token existente
    this.authService.checkTokenValidity();
    
    // Test del proxy al cargar la aplicación
    this.testProxyConnection();
  }

  /**
   * @method testProxyConnection
   * @description Prueba la conexión con el backend mediante el endpoint de health
   * @private
   */
  private testProxyConnection(): void {
    console.log('=== TESTING PROXY CONNECTION ===');
    
    // Test básico
    this.proxyTestService.testHealth().subscribe({
      next: (response) => {
        console.log('✅ Proxy working - Health check successful:', response);
      },
      error: (error) => {
        console.error('❌ Proxy failed - Health check error:', error);
        console.error('Error URL:', error.url);
        console.error('Error Status:', error.status);
      }
    });
  }
}

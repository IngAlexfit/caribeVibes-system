/**
 * @fileoverview Este archivo contiene los polyfills necesarios para la aplicación Angular.
 * Los polyfills son fragmentos de código que permiten que características modernas
 * de JavaScript funcionen en navegadores antiguos que no tienen soporte nativo.
 */

/**
 * @description Zone.js es un polyfill necesario para que Angular funcione.
 * Proporciona un mecanismo para interceptar y rastrear operaciones asíncronas,
 * lo que permite a Angular detectar cuándo actualizar la UI después de operaciones asíncronas.
 */
import 'zone.js';

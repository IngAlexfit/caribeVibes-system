/**
 * @fileoverview Archivo principal para configurar el entorno de pruebas de Angular.
 * Este archivo es el punto de entrada para ejecutar todas las pruebas unitarias.
 */
import 'zone.js/testing';
import { getTestBed } from '@angular/core/testing';
import {
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting
} from '@angular/platform-browser-dynamic/testing';

/**
 * @description Declaración para usar require en TypeScript
 * Permite la carga dinámica de módulos durante las pruebas
 */
declare const require: any;

/**
 * @description Inicializa el entorno de pruebas de Angular.
 * Configura el módulo y plataforma de pruebas para ejecutar tests unitarios.
 */
getTestBed().initTestEnvironment(
  BrowserDynamicTestingModule,
  platformBrowserDynamicTesting(),
);

/**
 * @description Busca todos los archivos de prueba en el proyecto.
 * Utiliza una expresión regular para encontrar archivos con extensión .spec.ts
 */
const context = require.context('./', true, /\.spec\.ts$/);

/**
 * @description Carga todos los módulos de prueba encontrados.
 * Esto ejecutará automáticamente todas las pruebas en los archivos encontrados.
 */
context.keys().map(context);

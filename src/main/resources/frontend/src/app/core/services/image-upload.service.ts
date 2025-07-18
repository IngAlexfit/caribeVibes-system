import { Injectable } from '@angular/core';
import { HttpClient, HttpEvent, HttpEventType, HttpResponse } from '@angular/common/http';
import { Observable, throwError } from 'rxjs';
import { map, catchError } from 'rxjs/operators';
import { environment } from '../../../environments/environment';

/**
 * @interface ImageUploadResponse
 * @description Respuesta del backend al subir una imagen
 */
export interface ImageUploadResponse {
  url: string;
  filename: string;
  size: number;
  width: number;
  height: number;
}

/**
 * @interface ImageValidationConfig
 * @description Configuración para validación de imágenes
 */
export interface ImageValidationConfig {
  maxSizeBytes: number;
  minWidth: number;
  minHeight: number;
  maxWidth: number;
  maxHeight: number;
  allowedTypes: string[];
  allowedExtensions: string[];
}

/**
 * @interface ImageValidationResult
 * @description Resultado de validación de imagen
 */
export interface ImageValidationResult {
  isValid: boolean;
  errors: string[];
  dimensions?: { width: number; height: number };
  size?: number;
}

/**
 * @service ImageUploadService
 * @description Servicio para manejo de carga y validación de imágenes
 */
@Injectable({
  providedIn: 'root'
})
export class ImageUploadService {
  
  private readonly API_URL = `${environment.apiUrl}/api`;
  
  // Configuración por defecto para imágenes de hotel
  readonly DEFAULT_CONFIG: ImageValidationConfig = {
    maxSizeBytes: 5 * 1024 * 1024, // 5MB
    minWidth: 800,
    minHeight: 600,
    maxWidth: 2048,
    maxHeight: 1536,
    allowedTypes: ['image/jpeg', 'image/jpg', 'image/png', 'image/webp'],
    allowedExtensions: ['.jpg', '.jpeg', '.png', '.webp']
  };

  constructor(private http: HttpClient) {}

  /**
   * Valida un archivo de imagen antes de subirlo
   */
  validateImageFile(file: File, config: ImageValidationConfig = this.DEFAULT_CONFIG): Promise<ImageValidationResult> {
    return new Promise((resolve) => {
      const errors: string[] = [];
      
      // Validar tipo de archivo
      if (!config.allowedTypes.includes(file.type)) {
        errors.push(`Tipo de archivo no permitido. Use: ${config.allowedExtensions.join(', ')}`);
      }
      
      // Validar tamaño de archivo
      if (file.size > config.maxSizeBytes) {
        const maxSizeMB = config.maxSizeBytes / (1024 * 1024);
        errors.push(`El archivo es demasiado grande. Máximo: ${maxSizeMB}MB`);
      }
      
      // Si hay errores de tipo o tamaño, no continuar
      if (errors.length > 0) {
        resolve({
          isValid: false,
          errors,
          size: file.size
        });
        return;
      }
      
      // Validar dimensiones de la imagen
      const img = new Image();
      const objectURL = URL.createObjectURL(file);
      
      img.onload = () => {
        const width = img.width;
        const height = img.height;
        
        // Liberar memoria
        URL.revokeObjectURL(objectURL);
        
        if (width < config.minWidth || height < config.minHeight) {
          errors.push(`Dimensiones muy pequeñas. Mínimo: ${config.minWidth}x${config.minHeight}px`);
        }
        
        if (width > config.maxWidth || height > config.maxHeight) {
          errors.push(`Dimensiones muy grandes. Máximo: ${config.maxWidth}x${config.maxHeight}px`);
        }
        
        resolve({
          isValid: errors.length === 0,
          errors,
          dimensions: { width, height },
          size: file.size
        });
      };
      
      img.onerror = () => {
        URL.revokeObjectURL(objectURL);
        errors.push('No se pudo cargar la imagen. Archivo corrupto o formato inválido.');
        
        resolve({
          isValid: false,
          errors,
          size: file.size
        });
      };
      
      img.src = objectURL;
    });
  }

  /**
   * Crea una vista previa de la imagen
   */
  createImagePreview(file: File): Promise<string> {
    return new Promise((resolve, reject) => {
      const reader = new FileReader();
      
      reader.onload = (e: any) => {
        resolve(e.target.result);
      };
      
      reader.onerror = (error) => {
        reject('Error al crear vista previa de la imagen');
      };
      
      reader.readAsDataURL(file);
    });
  }

  /**
   * Sube una imagen de hotel al servidor
   */
  uploadHotelImage(file: File): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('image', file);
    formData.append('category', 'hotel');
    
    return this.http.post<ImageUploadResponse>(`${this.API_URL}/hotels/upload-image`, formData)
      .pipe(
        catchError(error => {
          console.error('Error uploading hotel image:', error);
          return throwError(() => new Error('Error al subir la imagen del hotel'));
        })
      );
  }

  /**
   * Sube una imagen de habitación al servidor
   */
  uploadRoomImage(file: File): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('image', file);
    formData.append('category', 'room');
    
    return this.http.post<ImageUploadResponse>(`${this.API_URL}/rooms/upload-image`, formData)
      .pipe(
        catchError(error => {
          console.error('Error uploading room image:', error);
          return throwError(() => new Error('Error al subir la imagen de la habitación'));
        })
      );
  }

  /**
   * Sube una imagen genérica al servidor
   */
  uploadImage(file: File, category: string = 'general'): Observable<ImageUploadResponse> {
    const formData = new FormData();
    formData.append('image', file);
    formData.append('category', category);
    
    return this.http.post<ImageUploadResponse>(`${this.API_URL}/upload/image`, formData)
      .pipe(
        catchError(error => {
          console.error('Error uploading image:', error);
          return throwError(() => new Error('Error al subir la imagen'));
        })
      );
  }

  /**
   * Elimina una imagen del servidor
   */
  deleteImage(imageUrl: string): Observable<any> {
    return this.http.delete(`${this.API_URL}/upload/image`, {
      body: { imageUrl }
    }).pipe(
      catchError(error => {
        console.error('Error deleting image:', error);
        return throwError(() => new Error('Error al eliminar la imagen'));
      })
    );
  }

  /**
   * Valida una URL de imagen
   */
  validateImageUrl(url: string): boolean {
    if (!url || url.trim() === '') {
      return true; // URL vacía es válida
    }
    
    const trimmedUrl = url.trim();
    
    // Caso 1: URL completa (http/https)
    if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://')) {
      try {
        const urlObject = new URL(trimmedUrl);
        
        // Verificar que sea HTTP o HTTPS
        if (!['http:', 'https:'].includes(urlObject.protocol)) {
          return false;
        }
        
        // Verificar extensión de archivo de imagen
        const pathname = urlObject.pathname.toLowerCase();
        const imageExtensions = ['.jpg', '.jpeg', '.png', '.webp', '.gif', '.bmp', '.svg'];
        
        return imageExtensions.some(ext => pathname.endsWith(ext));
      } catch {
        return false;
      }
    }
    
    // Caso 2: Ruta absoluta (empieza con /)
    if (trimmedUrl.startsWith('/')) {
      // Verificar que no contenga caracteres peligrosos
      if (trimmedUrl.includes('..') || trimmedUrl.includes('\\')) {
        return false;
      }
      
      // Verificar extensión de archivo de imagen
      const lowerUrl = trimmedUrl.toLowerCase();
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.webp', '.gif', '.bmp', '.svg'];
      
      return imageExtensions.some(ext => lowerUrl.endsWith(ext));
    }
    
    // Caso 3: Ruta relativa sin protocolo
    if (trimmedUrl.match(/^[a-zA-Z0-9._\-\/\\]+$/)) {
      // Verificar que no contenga secuencias peligrosas
      if (trimmedUrl.includes('..') || trimmedUrl.includes('\\\\')) {
        return false;
      }
      
      // Verificar extensión de archivo de imagen
      const lowerUrl = trimmedUrl.toLowerCase();
      const imageExtensions = ['.jpg', '.jpeg', '.png', '.webp', '.gif', '.bmp', '.svg'];
      
      return imageExtensions.some(ext => lowerUrl.endsWith(ext));
    }
    
    // Si no cumple ningún patrón, es inválida
    return false;
  }

  /**
   * Obtiene información sobre las dimensiones recomendadas
   */
  getDimensionsInfo(config: ImageValidationConfig = this.DEFAULT_CONFIG): string {
    return `Dimensiones recomendadas: ${config.minWidth}x${config.minHeight}px a ${config.maxWidth}x${config.maxHeight}px`;
  }

  /**
   * Obtiene información sobre el tamaño máximo de archivo
   */
  getMaxFileSizeInfo(config: ImageValidationConfig = this.DEFAULT_CONFIG): string {
    const maxSizeMB = config.maxSizeBytes / (1024 * 1024);
    return `Tamaño máximo: ${maxSizeMB}MB`;
  }

  /**
   * Obtiene los tipos de archivo permitidos
   */
  getAllowedTypesInfo(config: ImageValidationConfig = this.DEFAULT_CONFIG): string {
    return `Formatos permitidos: ${config.allowedExtensions.join(', ')}`;
  }

  /**
   * Obtiene ejemplos de URLs válidas
   */
  getUrlExamples(): string[] {
    return [
      'https://ejemplo.com/imagen.jpg',
      'http://mi-servidor.com/fotos/hotel.png',
      '/assets/images/hotel-principal.jpg',
      '/uploads/hotels/hotel_123.webp',
      'assets/images/hotel.png',
      'uploads/hotels/imagen.jpg'
    ];
  }

  /**
   * Valida y proporciona feedback detallado sobre una URL
   */
  validateUrlWithFeedback(url: string): { isValid: boolean; message?: string } {
    if (!url || url.trim() === '') {
      return { isValid: true };
    }
    
    const trimmedUrl = url.trim();
    
    // Verificar caracteres peligrosos
    if (trimmedUrl.includes('..') || trimmedUrl.includes('\\\\')) {
      return { 
        isValid: false, 
        message: 'La URL no puede contener secuencias peligrosas como ".." o "\\\\"' 
      };
    }
    
    // Verificar extensión
    const lowerUrl = trimmedUrl.toLowerCase();
    const imageExtensions = ['.jpg', '.jpeg', '.png', '.webp', '.gif', '.bmp', '.svg'];
    const hasValidExtension = imageExtensions.some(ext => lowerUrl.endsWith(ext));
    
    if (!hasValidExtension) {
      return { 
        isValid: false, 
        message: `La URL debe terminar con una extensión de imagen válida: ${imageExtensions.join(', ')}` 
      };
    }
    
    // URL web completa
    if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://')) {
      try {
        new URL(trimmedUrl);
        return { isValid: true };
      } catch {
        return { 
          isValid: false, 
          message: 'Formato de URL web inválido' 
        };
      }
    }
    
    // Ruta absoluta
    if (trimmedUrl.startsWith('/')) {
      return { isValid: true };
    }
    
    // Ruta relativa
    if (trimmedUrl.match(/^[a-zA-Z0-9._\-\/]+$/)) {
      return { isValid: true };
    }
    
    return { 
      isValid: false, 
      message: 'Formato de URL inválido. Use una URL web completa o una ruta de archivo válida.' 
    };
  }

  /**
   * Redimensiona una imagen (cliente-lado) - para casos especiales
   */
  resizeImage(file: File, maxWidth: number, maxHeight: number, quality: number = 0.8): Promise<Blob> {
    return new Promise((resolve, reject) => {
      const canvas = document.createElement('canvas');
      const ctx = canvas.getContext('2d');
      const img = new Image();
      
      img.onload = () => {
        // Calcular nuevas dimensiones manteniendo proporción
        let { width, height } = img;
        
        if (width > height) {
          if (width > maxWidth) {
            height = (height * maxWidth) / width;
            width = maxWidth;
          }
        } else {
          if (height > maxHeight) {
            width = (width * maxHeight) / height;
            height = maxHeight;
          }
        }
        
        canvas.width = width;
        canvas.height = height;
        
        // Dibujar imagen redimensionada
        ctx?.drawImage(img, 0, 0, width, height);
        
        // Convertir a blob
        canvas.toBlob(
          (blob) => {
            if (blob) {
              resolve(blob);
            } else {
              reject(new Error('Error al redimensionar la imagen'));
            }
          },
          file.type,
          quality
        );
      };
      
      img.onerror = () => {
        reject(new Error('Error al cargar la imagen para redimensionar'));
      };
      
      img.src = URL.createObjectURL(file);
    });
  }
}

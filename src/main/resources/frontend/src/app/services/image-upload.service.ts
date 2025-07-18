import { Injectable } from '@angular/core';

export interface ImageConfig {
  maxWidth: number;
  maxHeight: number;
  maxSizeMB: number;
  allowedTypes: string[];
}

export interface ValidationResult {
  isValid: boolean;
  message?: string;
}

@Injectable({
  providedIn: 'root'
})
export class ImageUploadService {

  constructor() { }

  /**
   * Valida una URL de imagen (web URL o ruta local)
   */
  validateImageUrl(url: string): boolean {
    if (!url || url.trim() === '') {
      return false;
    }

    const trimmedUrl = url.trim();

    // Validar URL web (http/https)
    if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://')) {
      try {
        const urlObj = new URL(trimmedUrl);
        return this.hasValidImageExtension(urlObj.pathname);
      } catch {
        return false;
      }
    }

    // Validar rutas locales (absolutas o relativas)
    if (trimmedUrl.startsWith('/') || 
        trimmedUrl.includes('/') || 
        trimmedUrl.includes('\\') ||
        trimmedUrl.match(/^[a-zA-Z]:/)) { // Rutas de Windows como C:
      return this.hasValidImageExtension(trimmedUrl);
    }

    // Validar nombre de archivo simple
    return this.hasValidImageExtension(trimmedUrl);
  }

  /**
   * Valida URL con feedback detallado
   */
  validateUrlWithFeedback(url: string): ValidationResult {
    if (!url || url.trim() === '') {
      return {
        isValid: false,
        message: 'La URL no puede estar vacía'
      };
    }

    const trimmedUrl = url.trim();

    // Validar URL web
    if (trimmedUrl.startsWith('http://') || trimmedUrl.startsWith('https://')) {
      try {
        const urlObj = new URL(trimmedUrl);
        if (!this.hasValidImageExtension(urlObj.pathname)) {
          return {
            isValid: false,
            message: 'La URL debe terminar en una extensión de imagen válida (.jpg, .jpeg, .png, .webp)'
          };
        }
        return { isValid: true };
      } catch {
        return {
          isValid: false,
          message: 'URL web inválida. Debe comenzar con http:// o https://'
        };
      }
    }

    // Validar rutas locales
    if (trimmedUrl.startsWith('/') || 
        trimmedUrl.includes('/') || 
        trimmedUrl.includes('\\') ||
        trimmedUrl.match(/^[a-zA-Z]:/)) {
      if (!this.hasValidImageExtension(trimmedUrl)) {
        return {
          isValid: false,
          message: 'La ruta debe terminar en una extensión de imagen válida (.jpg, .jpeg, .png, .webp)'
        };
      }
      return { isValid: true };
    }

    // Validar nombre de archivo simple
    if (!this.hasValidImageExtension(trimmedUrl)) {
      return {
        isValid: false,
        message: 'El archivo debe tener una extensión de imagen válida (.jpg, .jpeg, .png, .webp)'
      };
    }

    return { isValid: true };
  }

  /**
   * Verifica si una ruta tiene extensión de imagen válida
   */
  private hasValidImageExtension(path: string): boolean {
    const validExtensions = ['.jpg', '.jpeg', '.png', '.webp'];
    const lowerPath = path.toLowerCase();
    return validExtensions.some(ext => lowerPath.endsWith(ext));
  }

  /**
   * Obtiene ejemplos de URLs válidas
   */
  getUrlExamples(): string[] {
    return [
      'https://ejemplo.com/hotel.jpg',
      'https://images.unsplash.com/photo.webp',
      '/assets/images/hotel.png',
      './uploads/hotel.jpeg',
      'images/hotel.jpg'
    ];
  }

  /**
   * Valida las dimensiones de una imagen
   */
  validateImageDimensions(file: File, config: ImageConfig): Promise<ValidationResult> {
    return new Promise((resolve) => {
      const img = new Image();
      const url = URL.createObjectURL(file);
      
      img.onload = () => {
        URL.revokeObjectURL(url);
        
        if (img.width > config.maxWidth || img.height > config.maxHeight) {
          resolve({
            isValid: false,
            message: `Las dimensiones deben ser máximo ${config.maxWidth}x${config.maxHeight}px. Actual: ${img.width}x${img.height}px`
          });
        } else {
          resolve({ isValid: true });
        }
      };
      
      img.onerror = () => {
        URL.revokeObjectURL(url);
        resolve({
          isValid: false,
          message: 'No se pudo cargar la imagen para validar sus dimensiones'
        });
      };
      
      img.src = url;
    });
  }

  /**
   * Valida el tamaño del archivo
   */
  validateFileSize(file: File, config: ImageConfig): ValidationResult {
    const maxSizeBytes = config.maxSizeMB * 1024 * 1024;
    
    if (file.size > maxSizeBytes) {
      return {
        isValid: false,
        message: `El archivo es muy grande. Máximo permitido: ${config.maxSizeMB}MB`
      };
    }
    
    return { isValid: true };
  }

  /**
   * Valida el tipo de archivo
   */
  validateFileType(file: File, config: ImageConfig): ValidationResult {
    if (!config.allowedTypes.includes(file.type)) {
      return {
        isValid: false,
        message: `Tipo de archivo no permitido. Tipos válidos: ${config.allowedTypes.join(', ')}`
      };
    }
    
    return { isValid: true };
  }

  /**
   * Obtiene información sobre las dimensiones permitidas
   */
  getImageDimensionsInfo(config: ImageConfig): string {
    return `Dimensiones máximas: ${config.maxWidth}x${config.maxHeight}px`;
  }

  /**
   * Obtiene información sobre el tamaño máximo
   */
  getMaxFileSizeInfo(config: ImageConfig): string {
    return `Tamaño máximo: ${config.maxSizeMB}MB`;
  }

  /**
   * Obtiene información sobre tipos permitidos
   */
  getAllowedTypesInfo(config: ImageConfig): string {
    return `Tipos permitidos: ${config.allowedTypes.join(', ')}`;
  }
}

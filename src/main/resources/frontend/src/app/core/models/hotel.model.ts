/**
 * @interface Hotel
 * @description Define un hotel con todos sus detalles y características
 */
export interface Hotel {
  /** @property {number} id - Identificador único del hotel */
  id: number;
  /** @property {string} name - Nombre del hotel */
  name: string;
  /** @property {string} description - Descripción detallada del hotel */
  description?: string;
  /** @property {string} address - Dirección física del hotel */
  address?: string;
  /** @property {string} phoneNumber - Número de teléfono de contacto */
  phoneNumber?: string;
  /** @property {string} email - Correo electrónico de contacto */
  email?: string;
  /** @property {string} [websiteUrl] - URL del sitio web del hotel */
  websiteUrl?: string;
  /** @property {string} imageUrl - URL de la imagen principal del hotel */
  imageUrl?: string;
  /** @property {number} stars - Categoría del hotel en estrellas (1-5) */
  stars: number;
  /** @property {number} rating - Calificación promedio de usuarios (1-5) */
  rating?: number;
  /** @property {number} basePrice - Precio base del hotel */
  basePrice?: number;
  /** @property {boolean} isActive - Indica si el hotel está activo para reservas */
  isActive?: boolean;
  /** @property {number} destinationId - ID del destino donde se encuentra el hotel */
  destinationId?: number;
  /** @property {string} [destinationName] - Nombre del destino (para visualización) */
  destinationName?: string;
  /** @property {any} destination - Información del destino */
  destination?: any;
  /** @property {RoomType[]} [roomTypes] - Tipos de habitaciones disponibles */
  roomTypes?: RoomType[];
}

/**
 * @interface RoomType
 * @description Define un tipo de habitación dentro de un hotel
 */
export interface RoomType {
  /** @property {number} id - Identificador único del tipo de habitación */
  id: number;
  /** @property {string} name - Nombre del tipo de habitación (ej. Suite, Doble, etc.) */
  name: string;
  /** @property {string} description - Descripción detallada de la habitación */
  description?: string;
  /** @property {number} capacity - Capacidad máxima de huéspedes */
  capacity?: number;
  /** @property {number} maxOccupancy - Ocupación máxima */
  maxOccupancy?: number;
  /** @property {string} bedType - Tipo de cama */
  bedType?: string;
  /** @property {number} pricePerNight - Precio por noche */
  pricePerNight: number;
  /** @property {string} imageUrl - URL de la imagen de la habitación */
  imageUrl: string;
  /** @property {boolean} isActive - Indica si este tipo de habitación está disponible */
  isActive: boolean;
  /** @property {number} hotelId - ID del hotel al que pertenece */
  hotelId: number;
  /** @property {string[]} amenities - Comodidades incluidas en la habitación */
  amenities: string[];
}

/**
 * @interface HotelResponse
 * @description Define la respuesta del servidor con datos de un hotel
 */
export interface HotelResponse {
  /** @property {number} id - Identificador único del hotel */
  id: number;
  /** @property {string} name - Nombre del hotel */
  name: string;
  /** @property {string} description - Descripción detallada del hotel */
  description: string;
  /** @property {string} address - Dirección física del hotel */
  address: string;
  /** @property {string} phoneNumber - Número de teléfono de contacto */
  phoneNumber: string;
  /** @property {string} email - Correo electrónico de contacto */
  email: string;
  /** @property {string} [websiteUrl] - URL del sitio web del hotel */
  websiteUrl?: string;
  /** @property {string} imageUrl - URL de la imagen principal del hotel */
  imageUrl: string;
  /** @property {number} stars - Categoría del hotel en estrellas (1-5) */
  stars: number;
  /** @property {number} rating - Calificación promedio de usuarios (1-5) */
  rating: number;
  /** @property {string} destinationName - Nombre del destino donde se encuentra */
  destinationName: string;
  /** @property {RoomType[]} roomTypes - Tipos de habitaciones disponibles */
  roomTypes: RoomType[];
}

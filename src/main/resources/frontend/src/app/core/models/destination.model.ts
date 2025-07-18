/**
 * @interface Destination
 * @description Interfaz que define la estructura de un destino turístico en la aplicación
 */
export interface Destination {
  /** @property {number} id - Identificador único del destino */
  id: number;
  /** @property {string} slug - URL amigable para SEO */
  slug: string;
  /** @property {string} name - Nombre del destino */
  name: string;
  /** @property {string} description - Descripción corta del destino */
  description: string;
  /** @property {string} [longDescription] - Descripción detallada del destino (opcional) */
  longDescription?: string;
  /** @property {string} [location] - Ubicación geográfica del destino (opcional) */
  location?: string;
  /** @property {string} [category] - Categoría del destino (playa, montaña, etc.) (opcional) */
  category?: string;
  /** @property {string} imageUrl - URL de la imagen principal del destino */
  imageUrl: string;
  /** @property {string[]} tags - Etiquetas o palabras clave asociadas al destino */
  tags: string[];
  /** @property {Experience[]} experiences - Experiencias disponibles en el destino */
  experiences: Experience[];
  /** @property {Activity[]} [activities] - Actividades disponibles en el destino (opcional) */
  activities?: Activity[];
  /** @property {number} lowSeasonPrice - Precio en temporada baja */
  lowSeasonPrice: number;
  /** @property {number} highSeasonPrice - Precio en temporada alta */
  highSeasonPrice: number;
  /** @property {string} createdAt - Fecha de creación del registro */
  createdAt: string;
}

/**
 * @interface Activity
 * @description Interfaz que define una actividad turística que se puede realizar en un destino
 */
export interface Activity {
  /** @property {number} id - Identificador único de la actividad */
  id: number;
  /** @property {string} name - Nombre de la actividad */
  name: string;
  /** @property {string} description - Descripción de la actividad */
  description: string;
  /** @property {number} price - Precio de la actividad */
  price: number;
  /** @property {number} duration - Duración de la actividad en horas/minutos */
  duration: number;
  /** @property {string} category - Categoría de la actividad (deportiva, cultural, etc.) */
  category: string;
  /** @property {number} destinationId - ID del destino al que pertenece la actividad */
  destinationId: number;
  /** @property {boolean} isActive - Estado de la actividad (activa o inactiva) */
  isActive: boolean;
}

/**
 * @interface Experience
 * @description Interfaz que define una experiencia o tipo de vivencia ofrecida en un destino
 */
export interface Experience {
  /** @property {number} id - Identificador único de la experiencia */
  id: number;
  /** @property {string} slug - URL amigable para SEO */
  slug: string;
  /** @property {string} name - Nombre de la experiencia */
  name: string;
  /** @property {string} description - Descripción de la experiencia */
  description: string;
  /** @property {number} [price] - Precio de la experiencia (opcional) */
  price?: number;
  /** @property {string} [duration] - Duración de la experiencia (opcional) */
  duration?: string;
  /** @property {string} iconUrl - URL del icono que representa la experiencia */
  iconUrl: string;
  /** @property {string} color - Color asociado con la experiencia para UI */
  color: string;
  /** @property {number} displayOrder - Orden de visualización en la interfaz */
  displayOrder: number;
  /** @property {boolean} isActive - Estado de la experiencia (activa o inactiva) */
  isActive: boolean;
}

/**
 * @interface DestinationResponse
 * @description Interfaz que define la respuesta del API para un destino turístico
 * Extiende la interfaz base Destination para mantener compatibilidad con el backend
 */
export interface DestinationResponse {
  /** @property {number} id - Identificador único del destino */
  id: number;
  /** @property {string} slug - URL amigable para SEO */
  slug: string;
  /** @property {string} name - Nombre del destino */
  name: string;
  /** @property {string} description - Descripción corta del destino */
  description: string;
  /** @property {string} [longDescription] - Descripción detallada del destino (opcional) */
  longDescription?: string;
  /** @property {string} [location] - Ubicación geográfica del destino (opcional) */
  location?: string;
  /** @property {string} [category] - Categoría del destino (playa, montaña, etc.) (opcional) */
  category?: string;
  /** @property {string} country - Código del país del destino */
  country: string;
  /** @property {string} [countryName] - Nombre del país del destino (viene del backend) */
  countryName?: string;
  /** @property {boolean} [active] - Estado del destino (activo o inactivo) */
  active?: boolean;
  /** @property {string} imageUrl - URL de la imagen principal del destino */
  imageUrl: string;
  /** @property {string[]} tags - Etiquetas o palabras clave asociadas al destino */
  tags: string[];
  /** @property {Experience[]} experiences - Experiencias disponibles en el destino */
  experiences: Experience[];
  /** @property {Activity[]} [activities] - Actividades disponibles en el destino (opcional) */
  activities?: Activity[];
  /** @property {number} lowSeasonPrice - Precio en temporada baja */
  lowSeasonPrice: number;
  /** @property {number} highSeasonPrice - Precio en temporada alta */
  highSeasonPrice: number;
  /** @property {string} createdAt - Fecha de creación del registro */
  createdAt: string;
}

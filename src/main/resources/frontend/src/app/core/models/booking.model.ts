/**
 * @interface Booking
 * @description Define una reserva completa de alojamiento con todos sus detalles
 */
export interface Booking {
  /** @property {number} id - Identificador único de la reserva */
  id: number;
  /** @property {number} userId - ID del usuario que realizó la reserva */
  userId: number;
  /** @property {number} hotelId - ID del hotel reservado */
  hotelId: number;
  /** @property {number} roomTypeId - ID del tipo de habitación reservada */
  roomTypeId: number;
  /** @property {Date} checkInDate - Fecha de entrada */
  checkInDate: Date;
  /** @property {Date} checkOutDate - Fecha de salida */
  checkOutDate: Date;
  /** @property {number} totalPrice - Precio total de la reserva */
  totalPrice: number;
  /** @property {BookingStatus} status - Estado actual de la reserva */
  status: BookingStatus;
  /** @property {Date} bookingDate - Fecha en que se realizó la reserva */
  bookingDate: Date;
  /** @property {number} guests - Número de huéspedes */
  guests: number;
  /** @property {string} [specialRequests] - Solicitudes especiales del cliente */
  specialRequests?: string;
  /** @property {string} [hotelName] - Nombre del hotel (para visualización) */
  hotelName?: string;
  /** @property {string} [roomTypeName] - Nombre del tipo de habitación (para visualización) */
  roomTypeName?: string;
  /** @property {string} [userName] - Nombre del usuario (para visualización) */
  userName?: string;
  /** @property {BookingActivity[]} [activities] - Actividades incluidas en la reserva */
  activities?: BookingActivity[];
}

/**
 * @interface BookingActivity
 * @description Define una actividad asociada a una reserva
 */
export interface BookingActivity {
  /** @property {number} id - Identificador único de la actividad reservada */
  id: number;
  /** @property {number} bookingId - ID de la reserva asociada */
  bookingId?: number;
  /** @property {number} activityId - ID de la actividad */
  activityId?: number;
  /** @property {string} activityName - Nombre de la actividad */
  activityName?: string;
  /** @property {number} quantity - Cantidad de participantes */
  quantity: number;
  /** @property {number} pricePerPerson - Precio por persona */
  pricePerPerson: number;
  /** @property {number} totalPrice - Precio total de la actividad */
  totalPrice: number;
  /** @property {Date} [scheduledDate] - Fecha programada para la actividad */
  scheduledDate?: Date;
  /** @property {Activity} activity - Información de la actividad */
  activity?: Activity;
}

/**
 * @interface Activity
 * @description Define una actividad turística
 */
export interface Activity {
  /** @property {number} id - Identificador único de la actividad */
  id: number;
  /** @property {string} name - Nombre de la actividad */
  name: string;
  /** @property {string} description - Descripción de la actividad */
  description?: string;
  /** @property {number} price - Precio de la actividad */
  price: number;
  /** @property {number} duration - Duración de la actividad */
  duration?: number;
  /** @property {string} difficulty - Nivel de dificultad */
  difficulty?: string;
  /** @property {string} imageUrl - URL de la imagen */
  imageUrl?: string;
}

/**
 * @interface User
 * @description Define los datos básicos de un usuario
 */
export interface User {
  /** @property {number} id - Identificador único del usuario */
  id: number;
  /** @property {string} name - Nombre del usuario */
  name: string;
  /** @property {string} email - Email del usuario */
  email: string;
}

/**
 * @interface Hotel
 * @description Define los datos básicos de un hotel
 */
export interface Hotel {
  /** @property {number} id - Identificador único del hotel */
  id: number;
  /** @property {string} name - Nombre del hotel */
  name: string;
  /** @property {string} location - Ubicación del hotel */
  location: string;
  /** @property {string} [imageUrl] - URL de la imagen del hotel */
  imageUrl?: string;
  /** @property {number} [rating] - Calificación del hotel */
  rating?: number;
}

/**
 * @interface RoomType
 * @description Define los datos básicos de un tipo de habitación
 */
export interface RoomType {
  /** @property {number} id - Identificador único del tipo de habitación */
  id: number;
  /** @property {string} name - Nombre del tipo de habitación */
  name: string;
  /** @property {string} description - Descripción del tipo de habitación */
  description: string;
  /** @property {number} maxOccupancy - Capacidad máxima de huéspedes */
  maxOccupancy: number;
  /** @property {number} pricePerNight - Precio por noche */
  pricePerNight: number;
  /** @property {string} [imageUrl] - URL de la imagen del tipo de habitación */
  imageUrl?: string;
}

/**
 * @interface BookingRequest
 * @description Define los datos necesarios para crear una nueva reserva
 */
export interface BookingRequest {
  /** @property {number} hotelId - ID del hotel a reservar */
  hotelId: number;
  /** @property {number} roomTypeId - ID del tipo de habitación a reservar */
  roomTypeId: number;
  /** @property {string} checkInDate - Fecha de entrada (formato string) */
  checkInDate: string;
  /** @property {string} checkOutDate - Fecha de salida (formato string) */
  checkOutDate: string;
  /** @property {number} numGuests - Número de huéspedes */
  numGuests: number;
  /** @property {number} numRooms - Número de habitaciones a reservar */
  numRooms: number;
  /** @property {string} [specialRequests] - Solicitudes especiales */
  specialRequests?: string;
  // NOTA: Las actividades se agregan DESPUÉS de crear la reserva mediante endpoints separados
}

/**
 * @interface BookingResponse
 * @description Define la respuesta del servidor con los datos de una reserva
 */
export interface BookingResponse {
  /** @property {number} id - Identificador único de la reserva */
  id: number;
  /** @property {number} userId - ID del usuario que realizó la reserva */
  userId: number;
  /** @property {string} checkInDate - Fecha de entrada */
  checkInDate: string;
  /** @property {string} checkOutDate - Fecha de salida */
  checkOutDate: string;
  /** @property {number} totalPrice - Precio total de la reserva */
  totalPrice: number;
  /** @property {BookingStatus} status - Estado actual de la reserva */
  status: BookingStatus;
  /** @property {string} bookingDate - Fecha en que se realizó la reserva */
  bookingDate: string;
  /** @property {number} numGuests - Número de huéspedes */
  numGuests: number;
  /** @property {number} numRooms - Número de habitaciones */
  numRooms: number;
  /** @property {string} [specialRequests] - Solicitudes especiales */
  specialRequests?: string;
  /** @property {BookingActivity[]} activities - Actividades incluidas */
  activities: BookingActivity[];
  /** @property {User} user - Datos del usuario que realizó la reserva */
  user: User;
  /** @property {Hotel} hotel - Datos del hotel reservado */
  hotel: Hotel;
  /** @property {RoomType} roomType - Datos del tipo de habitación */
  roomType: RoomType;
  // Legacy fields for backward compatibility
  /** @property {string} hotelName - Nombre del hotel reservado */
  hotelName?: string;
  /** @property {string} roomTypeName - Nombre del tipo de habitación */
  roomTypeName?: string;
  /** @property {number} guests - Número de huéspedes (alias para numGuests) */
  guests?: number;
}

/**
 * @enum BookingStatus
 * @description Define los posibles estados de una reserva
 */
export enum BookingStatus {
  /** Reserva pendiente de confirmación */
  PENDING = 'PENDING',
  /** Reserva confirmada */
  CONFIRMED = 'CONFIRMED',
  /** Reserva cancelada */
  CANCELLED = 'CANCELLED',
  /** Reserva completada (estancia finalizada) */
  COMPLETED = 'COMPLETED'
}

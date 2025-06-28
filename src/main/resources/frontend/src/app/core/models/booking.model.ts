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
  bookingId: number;
  /** @property {number} activityId - ID de la actividad */
  activityId: number;
  /** @property {string} activityName - Nombre de la actividad */
  activityName: string;
  /** @property {number} activityPrice - Precio unitario de la actividad */
  activityPrice: number;
  /** @property {number} quantity - Cantidad de actividades reservadas */
  quantity: number;
  /** @property {number} totalPrice - Precio total de las actividades */
  totalPrice: number;
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
  /** @property {number} guests - Número de huéspedes */
  guests: number;
  /** @property {string} [specialRequests] - Solicitudes especiales */
  specialRequests?: string;
  /** @property {number[]} [activityIds] - IDs de actividades a incluir */
  activityIds?: number[];
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
  /** @property {string} hotelName - Nombre del hotel reservado */
  hotelName: string;
  /** @property {string} roomTypeName - Nombre del tipo de habitación */
  roomTypeName: string;
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
  /** @property {string} [specialRequests] - Solicitudes especiales */
  specialRequests?: string;
  /** @property {BookingActivity[]} activities - Actividades incluidas */
  activities: BookingActivity[];
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

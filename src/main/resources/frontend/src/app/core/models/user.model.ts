/**
 * @interface User
 * @description Interfaz que define la estructura de un usuario en el sistema
 */
export interface User {
  /** @property {number} id - Identificador único del usuario */
  id: number;
  /** @property {string} username - Nombre de usuario único */
  username?: string;
  /** @property {string} email - Correo electrónico del usuario */
  email: string;
  /** @property {string} firstName - Nombre del usuario */
  firstName: string;
  /** @property {string} lastName - Apellido del usuario */
  lastName: string;
  /** @property {string} phone - Teléfono del usuario */
  phone?: string;
  /** @property {string} fullName - Nombre completo del usuario */
  fullName?: string;
  /** @property {string[]} [roleNames] - Roles asignados al usuario (opcional) */
  roleNames?: string[]; // Made optional to handle cases where backend doesn't provide it
  /** @property {boolean} isActive - Estado del usuario (activo/inactivo) */
  isActive?: boolean;
  /** @property {Date} createdAt - Fecha de creación de la cuenta */
  createdAt?: Date;
  /** @property {UserPreferences} [preferences] - Preferencias del usuario (opcional) */
  preferences?: UserPreferences;
}

/**
 * @interface UserPreferences
 * @description Interfaz que define las preferencias de un usuario
 */
export interface UserPreferences {
  /** @property {string[]} interests - Intereses o temas de preferencia del usuario */
  interests: string[];
  /** @property {string[]} adventures - Tipos de aventuras que le interesan al usuario */
  adventures: string[];
}

/**
 * @interface AuthResponse
 * @description Interfaz que define la estructura de respuesta tras un proceso de autenticación
 */
export interface AuthResponse {
  /** @property {string} message - Mensaje descriptivo de la operación */
  message: string;
  /** @property {User} user - Datos del usuario autenticado */
  user: User;
  /** @property {string} token - Token JWT de autenticación */
  token: string;
  /** @property {string} tokenType - Tipo de token (generalmente "Bearer") */
  tokenType: string;
  /** @property {number} expiresIn - Tiempo de expiración del token en segundos (opcional) */
  expiresIn?: number;
  /** @property {string} expiresAt - Fecha y hora de expiración del token (opcional) */
  expiresAt?: string;
}

/**
 * @interface LoginRequest
 * @description Interfaz que define la estructura de datos para una solicitud de inicio de sesión
 */
export interface LoginRequest {
  /** @property {string} email - Correo electrónico del usuario */
  email: string;
  /** @property {string} password - Contraseña del usuario */
  password: string;
}

/**
 * @interface RegisterRequest
 * @description Interfaz que define la estructura de datos para una solicitud de registro de usuario
 */
export interface RegisterRequest {
  /** @property {string} username - Nombre de usuario único */
  username: string;
  /** @property {string} firstName - Nombre del usuario */
  firstName: string;
  /** @property {string} lastName - Apellido del usuario */
  lastName: string;
  /** @property {string} email - Correo electrónico del usuario */
  email: string;
  /** @property {string} password - Contraseña elegida */
  password: string;
  /** @property {string} confirmPassword - Confirmación de la contraseña */
  confirmPassword: string;
  /** @property {boolean} acceptTerms - Indica si el usuario aceptó los términos y condiciones */
  acceptTerms: boolean;
  /** @property {string[]} [interests] - Intereses seleccionados por el usuario (opcional) */
  interests?: string[];
  /** @property {string[]} [adventures] - Tipos de aventuras seleccionados (opcional) */
  adventures?: string[];
}

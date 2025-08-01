/**
 * @interface Contact
 * @description Interfaz que define la estructura de un mensaje de contacto
 */
export interface Contact {
  /** @property {number} id - Identificador único del mensaje */
  id: number;
  /** @property {string} name - Nombre de la persona que envía el mensaje */
  name: string;
  /** @property {string} email - Correo electrónico de contacto */
  email: string;
  /** @property {string} [phoneNumber] - Número de teléfono de contacto (opcional) */
  phoneNumber?: string;
  /** @property {string} subject - Asunto del mensaje */
  subject: string;
  /** @property {string} message - Contenido del mensaje */
  message: string;
  /** @property {ContactStatus} status - Estado actual del mensaje */
  status: ContactStatus;
  /** @property {Date} createdAt - Fecha en que se creó el mensaje */
  createdAt: Date;
  /** @property {Date} [updatedAt] - Fecha de última actualización (opcional) */
  updatedAt?: Date;
  /** @property {Date} [respondedAt] - Fecha de respuesta (opcional) */
  respondedAt?: Date;
  /** @property {string} [respondedBy] - Quién respondió (opcional) */
  respondedBy?: string;
  /** @property {string} [responseMessage] - Mensaje de respuesta (opcional) */
  responseMessage?: string;
  /** @property {string} [inquiryType] - Tipo de consulta (opcional) */
  inquiryType?: string;
  /** @property {boolean} active - Si el mensaje está activo */
  active: boolean;
  /** @property {string} [summary] - Resumen del mensaje (opcional) */
  summary?: string;
}

/**
 * @interface ContactRequest
 * @description Interfaz que define la estructura de datos para enviar un nuevo mensaje de contacto
 */
export interface ContactRequest {
  /** @property {string} name - Nombre de la persona que envía el mensaje */
  name: string;
  /** @property {string} email - Correo electrónico de contacto */
  email: string;
  /** @property {string} subject - Asunto del mensaje */
  subject: string;
  /** @property {string} message - Contenido del mensaje */
  message: string;
  /** @property {string} [phone] - Teléfono de contacto (opcional) */
  phone?: string;
}

/**
 * @enum {string} ContactStatus
 * @description Enumeración que define los posibles estados de un mensaje de contacto
 */
export enum ContactStatus {
  /** Mensaje nuevo, aún no leído */
  NEW = 'NEW',
  /** Mensaje que ha sido leído pero no respondido */
  READ = 'READ',
  /** Mensaje en proceso de respuesta */
  IN_PROGRESS = 'IN_PROGRESS',
  /** Mensaje que ha sido respondido */
  RESPONDED = 'RESPONDED',
  /** Mensaje cerrado */
  CLOSED = 'CLOSED'
}



/**
 * @interface ApiResponse<T>
 * @description Interfaz genérica para respuestas estándar de la API
 * @template T - Tipo de datos contenidos en la respuesta
 */
export interface ApiResponse<T> {
  /** @property {boolean} success - Indica si la operación fue exitosa */
  success: boolean;
  /** @property {string} message - Mensaje descriptivo de la operación */
  message: string;
  /** @property {T} [data] - Datos retornados (opcional) */
  data?: T;
  /** @property {string} [error] - Mensaje de error si la operación falló (opcional) */
  error?: string;
}

/**
 * @interface PageResponse<T>
 * @description Interfaz genérica para respuestas paginadas de la API
 * @template T - Tipo de datos contenidos en la página
 */
export interface PageResponse<T> {
  /** @property {T[]} content - Array con el contenido de la página actual */
  content: T[];
  /** @property {object} pageable - Información sobre la paginación actual */
  pageable: {
    /** @property {number} pageNumber - Número de página actual (base 0) */
    pageNumber: number;
    /** @property {number} pageSize - Cantidad de elementos por página */
    pageSize: number;
    /** @property {object} sort - Información sobre ordenamiento */
    sort: {
      /** @property {boolean} sorted - Indica si está ordenado */
      sorted: boolean;
      /** @property {boolean} unsorted - Indica si no está ordenado */
      unsorted: boolean;
    };
  };
  /** @property {number} totalElements - Número total de elementos */
  totalElements: number;
  /** @property {number} totalPages - Número total de páginas */
  totalPages: number;
  /** @property {number} size - Tamaño de la página actual */
  size: number;
  /** @property {number} number - Número de la página actual (base 0) */
  number: number;
  /** @property {object} sort - Información sobre ordenamiento */
  sort: {
    /** @property {boolean} sorted - Indica si está ordenado */
    sorted: boolean;
    /** @property {boolean} unsorted - Indica si no está ordenado */
    unsorted: boolean;
  };
  /** @property {number} numberOfElements - Cantidad de elementos en la página actual */
  numberOfElements: number;
  /** @property {boolean} first - Indica si es la primera página */
  first: boolean;
  /** @property {boolean} last - Indica si es la última página */
  last: boolean;
  /** @property {boolean} empty - Indica si la página está vacía */
  empty: boolean;
}

/**
 * @interface EmailReplyRequest
 * @description Interfaz para el request de respuesta por email
 */
export interface EmailReplyRequest {
  /** @property {string} replyMessage - Mensaje de respuesta */
  replyMessage: string;
  /** @property {string} [adminName] - Nombre del administrador que responde */
  adminName?: string;
  /** @property {boolean} [sendCopyToAdmin] - Si enviar copia al admin */
  sendCopyToAdmin?: boolean;
}

/**
 * @interface EmailReplyResponse
 * @description Interfaz para la respuesta del email reply
 */
export interface EmailReplyResponse {
  /** @property {boolean} success - Si el email se envió exitosamente */
  success: boolean;
  /** @property {string} message - Mensaje de respuesta */
  message: string;
  /** @property {boolean} [adminCopySent] - Si se envió copia al admin */
  adminCopySent?: boolean;
}

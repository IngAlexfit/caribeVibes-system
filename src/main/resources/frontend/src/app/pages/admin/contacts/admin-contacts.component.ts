import { Component, OnInit, OnDestroy } from '@angular/core';
import { Subject } from 'rxjs';
import { takeUntil } from 'rxjs/operators';
import { ContactService } from '../../../core/services/contact.service';
import { Contact, ContactStatus, PageResponse } from '../../../core/models/common.model';
import Swal from 'sweetalert2';

/**
 * @interface FilterOptions
 * @description Opciones de filtrado para los contactos
 */
interface FilterOptions {
  /** Estado del contacto */
  status: string;
  /** Término de búsqueda */
  searchTerm: string;
  /** Fecha desde */
  dateFrom: string;
  /** Fecha hasta */
  dateTo: string;
}

/**
 * @class AdminContactsComponent
 * @description Componente para la gestión administrativa de mensajes de contacto.
 * Permite ver, filtrar, responder y gestionar todos los mensajes de contacto del sistema.
 * @implements {OnInit, OnDestroy}
 */
@Component({
  selector: 'app-admin-contacts',
  templateUrl: './admin-contacts.component.html',
  styleUrls: ['./admin-contacts.component.scss']
})
export class AdminContactsComponent implements OnInit, OnDestroy {
  /** @property {Subject<void>} destroy$ - Subject para manejar la destrucción del componente */
  private destroy$ = new Subject<void>();

  /** @property {boolean} isLoading - Estado de carga */
  isLoading = true;

  /** @property {boolean} isLoadingAction - Estado de carga para acciones */
  isLoadingAction = false;

  /** @property {Contact[]} contacts - Lista de contactos */
  contacts: Contact[] = [];

  /** @property {PageResponse<Contact>|null} contactsPage - Información de paginación */
  contactsPage: PageResponse<Contact> | null = null;

  /** @property {Contact|null} selectedContact - Contacto seleccionado para detalles */
  selectedContact: Contact | null = null;

  /** @property {any} statistics - Estadísticas de contactos */
  statistics: any = null;

  /** @property {FilterOptions} filters - Filtros aplicados */
  filters: FilterOptions = {
    status: 'all',
    searchTerm: '',
    dateFrom: '',
    dateTo: ''
  };

  /** @property {number} currentPage - Página actual */
  currentPage = 0;

  /** @property {number} pageSize - Tamaño de página */
  pageSize = 10;

  /** @property {string} sortBy - Campo de ordenamiento */
  sortBy = 'createdAt';

  /** @property {string} sortDir - Dirección de ordenamiento */
  sortDir = 'desc';

  /** @property {string} errorMessage - Mensaje de error */
  errorMessage = '';

  /** @property {boolean} showFilters - Mostrar panel de filtros */
  showFilters = false;

  /** @property {string} replyText - Texto de respuesta */
  replyText = '';

  /** @property {string} adminName - Nombre del administrador */
  adminName = '';

  /** @property {boolean} sendCopyToAdmin - Enviar copia al admin */
  sendCopyToAdmin = false;

  /** @property {ContactStatus} ContactStatus - Enum de estados para uso en template */
  ContactStatus = ContactStatus;

  /**
   * @constructor
   * @param {ContactService} contactService - Servicio de contactos
   */
  constructor(private contactService: ContactService) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente
   */
  ngOnInit(): void {
    this.loadContacts();
    this.loadStatistics();
  }

  /**
   * @method ngOnDestroy
   * @description Limpia las suscripciones al destruir el componente
   */
  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.complete();
  }

  /**
   * @method loadContacts
   * @description Carga la lista de contactos según los filtros aplicados
   */
  loadContacts(): void {
    this.isLoading = true;
    this.errorMessage = '';

    let request;

    if (this.filters.searchTerm) {
      request = this.contactService.searchContacts(
        this.filters.searchTerm,
        this.currentPage,
        this.pageSize
      );
    } else if (this.filters.status !== 'all') {
      request = this.contactService.getContactsByStatus(
        this.filters.status as ContactStatus,
        this.currentPage,
        this.pageSize
      );
    } else {
      request = this.contactService.getAllContacts(
        this.currentPage,
        this.pageSize,
        this.sortBy,
        this.sortDir
      );
    }

    request.pipe(takeUntil(this.destroy$)).subscribe({
      next: (response) => {
        this.contactsPage = response;
        this.contacts = response.content;
        this.isLoading = false;
      },
      error: (error) => {
        console.error('Error cargando contactos:', error);
        this.errorMessage = 'Error al cargar los contactos. Intente nuevamente.';
        this.isLoading = false;
      }
    });
  }

  /**
   * @method loadStatistics
   * @description Carga las estadísticas de contactos
   */
  loadStatistics(): void {
    this.contactService.getContactStatistics()
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (stats) => {
          this.statistics = stats;
        },
        error: (error) => {
          console.error('Error cargando estadísticas:', error);
        }
      });
  }

  /**
   * @method onPageChange
   * @description Maneja el cambio de página
   * @param {number} page - Nueva página
   */
  onPageChange(page: number): void {
    this.currentPage = page;
    this.loadContacts();
  }

  /**
   * @method onPageSizeChange
   * @description Maneja el cambio de tamaño de página
   * @param {number} size - Nuevo tamaño
   */
  onPageSizeChange(size: number): void {
    this.pageSize = size;
    this.currentPage = 0;
    this.loadContacts();
  }

  /**
   * @method applyFilters
   * @description Aplica los filtros y recarga los contactos
   */
  applyFilters(): void {
    this.currentPage = 0;
    this.loadContacts();
  }

  /**
   * @method clearFilters
   * @description Limpia todos los filtros
   */
  clearFilters(): void {
    this.filters = {
      status: 'all',
      searchTerm: '',
      dateFrom: '',
      dateTo: ''
    };
    this.applyFilters();
  }

  /**
   * @method selectContact
   * @description Selecciona un contacto para ver detalles
   * @param {Contact} contact - Contacto seleccionado
   */
  selectContact(contact: Contact): void {
    this.selectedContact = contact;
    
    // Marcar como leído si no lo está
    if (contact.status === ContactStatus.NEW) {
      this.markAsRead(contact.id, false);
    }
  }

  /**
   * @method markAsRead
   * @description Marca un contacto como leído
   * @param {number} contactId - ID del contacto
   * @param {boolean} [showNotification=true] - Mostrar notificación
   */
  markAsRead(contactId: number, showNotification: boolean = true): void {
    this.contactService.markAsRead(contactId)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // Actualizar el contacto en la lista
          const contactIndex = this.contacts.findIndex(c => c.id === contactId);
          if (contactIndex !== -1) {
            this.contacts[contactIndex].status = ContactStatus.READ;
          }
          
          // Actualizar el contacto seleccionado
          if (this.selectedContact && this.selectedContact.id === contactId) {
            this.selectedContact.status = ContactStatus.READ;
          }

          if (showNotification) {
            Swal.fire({
              title: 'Marcado como leído',
              text: 'El mensaje ha sido marcado como leído.',
              icon: 'success',
              timer: 2000,
              showConfirmButton: false
            });
          }

          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error marcando como leído:', error);
          if (showNotification) {
            Swal.fire({
              title: 'Error',
              text: 'No se pudo marcar el mensaje como leído.',
              icon: 'error'
            });
          }
        }
      });
  }

  /**
   * @method replyToContact
   * @description Responde a un contacto
   * @param {number} contactId - ID del contacto
   */
  replyToContact(contactId: number): void {
    if (!this.replyText.trim()) {
      Swal.fire({
        title: 'Error',
        text: 'Por favor escriba una respuesta.',
        icon: 'warning'
      });
      return;
    }

    this.isLoadingAction = true;

    this.contactService.replyToContact(contactId, this.replyText)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // Actualizar el estado del contacto
          const contactIndex = this.contacts.findIndex(c => c.id === contactId);
          if (contactIndex !== -1) {
            this.contacts[contactIndex].status = ContactStatus.RESPONDED;
          }

          if (this.selectedContact && this.selectedContact.id === contactId) {
            this.selectedContact.status = ContactStatus.RESPONDED;
          }

          this.replyText = '';
          this.isLoadingAction = false;

          Swal.fire({
            title: 'Respuesta enviada',
            text: 'La respuesta ha sido enviada exitosamente.',
            icon: 'success'
          });

          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error enviando respuesta:', error);
          this.isLoadingAction = false;
          Swal.fire({
            title: 'Error',
            text: 'No se pudo enviar la respuesta. Intente nuevamente.',
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method sendEmailReply
   * @description Envía una respuesta por email a un contacto
   * @param {number} contactId - ID del contacto
   */
  sendEmailReply(contactId: number): void {
    if (!this.replyText.trim()) {
      Swal.fire({
        title: 'Error',
        text: 'Por favor escriba una respuesta.',
        icon: 'warning'
      });
      return;
    }

    this.isLoadingAction = true;

    const replyData = {
      replyMessage: this.replyText,
      adminName: this.adminName.trim() || 'Equipo de Caribe Vibes',
      sendCopyToAdmin: this.sendCopyToAdmin
    };

    this.contactService.sendEmailReply(contactId, replyData)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: (response) => {
          // Actualizar el estado del contacto
          const contactIndex = this.contacts.findIndex(c => c.id === contactId);
          if (contactIndex !== -1) {
            this.contacts[contactIndex].status = ContactStatus.RESPONDED;
          }

          if (this.selectedContact && this.selectedContact.id === contactId) {
            this.selectedContact.status = ContactStatus.RESPONDED;
          }

          this.replyText = '';
          this.adminName = '';
          this.sendCopyToAdmin = false;
          this.isLoadingAction = false;

          let successMessage = 'La respuesta ha sido enviada por email exitosamente.';
          if (response.adminCopySent) {
            successMessage += ' Se envió una copia al administrador.';
          }

          Swal.fire({
            title: 'Email enviado',
            text: successMessage,
            icon: 'success'
          });

          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error enviando email:', error);
          this.isLoadingAction = false;
          
          const errorMessage = error.error?.error || 'No se pudo enviar el email. Intente nuevamente.';
          
          Swal.fire({
            title: 'Error al enviar email',
            text: errorMessage,
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method sendAutoReply
   * @description Envía una respuesta automática
   * @param {number} contactId - ID del contacto
   * @param {string} templateType - Tipo de template
   */
  sendAutoReply(contactId: number, templateType: string = 'GENERAL'): void {
    this.isLoadingAction = true;

    this.contactService.sendAutoReply(contactId, templateType)
      .pipe(takeUntil(this.destroy$))
      .subscribe({
        next: () => {
          // Actualizar el estado del contacto
          const contactIndex = this.contacts.findIndex(c => c.id === contactId);
          if (contactIndex !== -1) {
            this.contacts[contactIndex].status = ContactStatus.RESPONDED;
          }

          if (this.selectedContact && this.selectedContact.id === contactId) {
            this.selectedContact.status = ContactStatus.RESPONDED;
          }

          this.isLoadingAction = false;

          Swal.fire({
            title: 'Respuesta automática enviada',
            text: 'Se ha enviado una respuesta automática al cliente.',
            icon: 'success'
          });

          this.loadStatistics();
        },
        error: (error) => {
          console.error('Error enviando respuesta automática:', error);
          this.isLoadingAction = false;
          Swal.fire({
            title: 'Error',
            text: 'No se pudo enviar la respuesta automática.',
            icon: 'error'
          });
        }
      });
  }

  /**
   * @method deleteContact
   * @description Elimina un contacto
   * @param {number} contactId - ID del contacto
   */
  deleteContact(contactId: number): void {
    Swal.fire({
      title: '¿Eliminar mensaje?',
      text: 'Esta acción no se puede deshacer.',
      icon: 'warning',
      showCancelButton: true,
      confirmButtonText: 'Eliminar',
      cancelButtonText: 'Cancelar',
      confirmButtonColor: '#d33'
    }).then((result) => {
      if (result.isConfirmed) {
        this.contactService.deleteContact(contactId)
          .pipe(takeUntil(this.destroy$))
          .subscribe({
            next: () => {
              // Remover de la lista
              this.contacts = this.contacts.filter(c => c.id !== contactId);
              
              // Si era el contacto seleccionado, deseleccionar
              if (this.selectedContact && this.selectedContact.id === contactId) {
                this.selectedContact = null;
              }

              Swal.fire({
                title: 'Eliminado',
                text: 'El mensaje ha sido eliminado.',
                icon: 'success'
              });

              this.loadStatistics();
            },
            error: (error) => {
              console.error('Error eliminando contacto:', error);
              Swal.fire({
                title: 'Error',
                text: 'No se pudo eliminar el mensaje.',
                icon: 'error'
              });
            }
          });
      }
    });
  }

  /**
   * @method getStatusBadgeClass
   * @description Obtiene la clase CSS para el badge de estado
   * @param {ContactStatus} status - Estado del contacto
   * @returns {string} Clase CSS
   */
  getStatusBadgeClass(status: ContactStatus): string {
    switch (status) {
      case ContactStatus.NEW:
        return 'badge bg-warning';
      case ContactStatus.READ:
        return 'badge bg-info';
      case ContactStatus.IN_PROGRESS:
        return 'badge bg-primary';
      case ContactStatus.RESPONDED:
        return 'badge bg-success';
      case ContactStatus.CLOSED:
        return 'badge bg-dark';
      default:
        return 'badge bg-secondary';
    }
  }

  /**
   * @method getStatusText
   * @description Obtiene el texto del estado
   * @param {ContactStatus} status - Estado del contacto
   * @returns {string} Texto del estado
   */
  getStatusText(status: ContactStatus): string {
    switch (status) {
      case ContactStatus.NEW:
        return 'Nuevo';
      case ContactStatus.READ:
        return 'Leído';
      case ContactStatus.IN_PROGRESS:
        return 'En Proceso';
      case ContactStatus.RESPONDED:
        return 'Respondido';
      case ContactStatus.CLOSED:
        return 'Cerrado';
      default:
        return 'Desconocido';
    }
  }

  /**
   * @method getPriorityClass
   * @description Obtiene la clase de prioridad basada en la urgencia del mensaje
   * @param {Contact} contact - Contacto
   * @returns {string} Clase CSS
   */
  getPriorityClass(contact: Contact): string {
    // Lógica simple: mensajes nuevos son más urgentes
    if (contact.status === ContactStatus.NEW) {
      return 'border-start border-warning border-3';
    }
    return '';
  }

  /**
   * @method formatDate
   * @description Formatea una fecha para mostrar
   * @param {string | Date} date - Fecha en formato ISO o objeto Date
   * @returns {string} Fecha formateada
   */
  formatDate(date: string | Date): string {
    const dateObj = typeof date === 'string' ? new Date(date) : date;
    return dateObj.toLocaleDateString('es-ES', {
      year: 'numeric',
      month: 'short',
      day: 'numeric',
      hour: '2-digit',
      minute: '2-digit'
    });
  }

  /**
   * @method refreshData
   * @description Recarga todos los datos
   */
  refreshData(): void {
    this.loadContacts();
    this.loadStatistics();
  }
}

import { Component, OnInit, AfterViewInit } from '@angular/core';
import { Router } from '@angular/router';
import { DestinationService } from '../../core/services/destination.service';
import { HotelService } from '../../core/services/hotel.service';
import { ContactService } from '../../core/services/contact.service';
import { DestinationResponse } from '../../core/models/destination.model';
import { HotelResponse } from '../../core/models/hotel.model';
import { ContactRequest } from '../../core/models/common.model';
import Swal from 'sweetalert2';

/** @type {any} Declaración para acceder a la biblioteca Bootstrap */
declare var bootstrap: any;

/**
 * @class HomeComponent
 * @description Componente principal de la página de inicio.
 * Muestra información destacada como destinos populares, hoteles recomendados,
 * testimonios y un formulario de contacto.
 * @implements {OnInit}
 * @implements {AfterViewInit}
 */
@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrls: ['./home.component.scss']
})
export class HomeComponent implements OnInit, AfterViewInit {
  /** @property {DestinationResponse[]} popularDestinations - Lista de destinos populares a mostrar */
  popularDestinations: DestinationResponse[] = [];
  
  /** @property {HotelResponse[]} topRatedHotels - Lista de hoteles mejor valorados */
  topRatedHotels: HotelResponse[] = [];
  
  /** @property {boolean} isLoading - Indicador de carga de datos */
  isLoading = true;
  
  /** @property {ContactRequest} contactForm - Modelo para el formulario de contacto */
  contactForm: ContactRequest = {
    name: '',
    email: '',
    subject: '',
    message: ''
  };

  /**
   * @constructor
   * @param {DestinationService} destinationService - Servicio para obtener datos de destinos
   * @param {HotelService} hotelService - Servicio para obtener datos de hoteles
   * @param {ContactService} contactService - Servicio para gestionar los mensajes de contacto
   * @param {Router} router - Servicio de enrutamiento para la navegación
   */
  constructor(
    private destinationService: DestinationService,
    private hotelService: HotelService,
    private contactService: ContactService,
    private router: Router
  ) {}

  /**
   * @method ngOnInit
   * @description Inicializa el componente y carga los datos necesarios
   */
  ngOnInit(): void {
    this.loadData();
  }

  /**
   * @method ngAfterViewInit
   * @description Se ejecuta después de que la vista ha sido inicializada
   * Inicializa los carousels con un pequeño retraso para asegurar que el DOM esté listo
   */
  ngAfterViewInit(): void {
    // Inicializar el carousel después de que la vista se ha renderizado
    setTimeout(() => {
      this.initCarousels();
    }, 500);
  }

  /**
   * @method initCarousels
   * @description Inicializa todos los carousels de Bootstrap presentes en la página
   * @private
   */
  private initCarousels(): void {
    try {
      // Inicializar todos los carousels de la página
      const carouselElements = document.querySelectorAll('.carousel');
      if (carouselElements.length > 0) {
        carouselElements.forEach(carouselElem => {
          console.log('Initializing carousel:', carouselElem.id);
          const carousel = new bootstrap.Carousel(carouselElem, {
            interval: 3000,
            ride: true,
            pause: 'hover'
          });
        });
        console.log('Carousels initialized successfully');
      } else {
        console.warn('No carousel elements found');
      }
    } catch (error) {
      console.error('Error initializing carousels:', error);
    }
  }

  /**
   * @method loadData
   * @description Carga los datos iniciales del componente de forma paralela
   * @private
   */
  private loadData(): void {
    Promise.all([
      this.loadPopularDestinations(),
      this.loadTopRatedHotels()
    ]).finally(() => {
      this.isLoading = false;
    });
  }

  /**
   * @method loadPopularDestinations
   * @description Carga los destinos populares desde el servicio
   * @returns {Promise<void>} Promesa que se resuelve cuando los datos son cargados
   * @private
   */
  private loadPopularDestinations(): Promise<void> {
    return new Promise((resolve) => {
      this.destinationService.getPopularDestinations(6).subscribe({
        next: (destinations) => {
          this.popularDestinations = destinations;
          resolve();
        },
        error: (error) => {
          console.error('Error loading popular destinations:', error);
          resolve();
        }
      });
    });
  }

  /**
   * @method loadTopRatedHotels
   * @description Carga los hoteles mejor valorados desde el servicio
   * @returns {Promise<void>} Promesa que se resuelve cuando los datos son cargados
   * @private
   */
  private loadTopRatedHotels(): Promise<void> {
    return new Promise((resolve) => {
      this.hotelService.getTopRatedHotels(6).subscribe({
        next: (hotels) => {
          this.topRatedHotels = hotels;
          resolve();
        },
        error: (error) => {
          console.error('Error loading top rated hotels:', error);
          resolve();
        }
      });
    });
  }

  /**
   * @method onContactSubmit
   * @description Maneja el envío del formulario de contacto
   * Valida los datos y envía la solicitud al servicio correspondiente
   */
  onContactSubmit(): void {
    if (this.isValidContactForm()) {
      this.contactService.createContact(this.contactForm).subscribe({
        next: () => {
          // Mostrar mensaje de éxito con SweetAlert
          Swal.fire({
            icon: 'success',
            title: '¡Mensaje Enviado!',
            text: 'Tu mensaje ha sido enviado exitosamente. Te contactaremos pronto.',
            confirmButtonText: 'Perfecto',
            confirmButtonColor: '#28a745',
            timer: 4000,
            timerProgressBar: true
          });
          this.resetContactForm();
        },
        error: (error) => {
          console.error('Error sending contact message:', error);
          // Mostrar mensaje de error con SweetAlert
          Swal.fire({
            icon: 'error',
            title: 'Error al Enviar',
            text: 'Hubo un problema al enviar tu mensaje. Por favor intenta nuevamente.',
            confirmButtonText: 'Reintentar',
            confirmButtonColor: '#dc3545'
          });
        }
      });
    }
  }

  /**
   * @method isValidContactForm
   * @description Verifica si el formulario de contacto tiene todos los campos requeridos
   * @returns {boolean} true si el formulario es válido, false en caso contrario
   * @private
   */
  private isValidContactForm(): boolean {
    return !!(this.contactForm.name && 
              this.contactForm.email && 
              this.contactForm.subject && 
              this.contactForm.message);
  }

  /**
   * @method resetContactForm
   * @description Reinicia el formulario de contacto a sus valores iniciales
   * @private
   */
  private resetContactForm(): void {
    this.contactForm = {
      name: '',
      email: '',
      subject: '',
      message: ''
    };
  }
  
  /**
   * @method navigateToDestinations
   * @description Navega a la página de destinos
   */
  navigateToDestinations(): void {
    this.router.navigate(['/destinations']);
  }
  
  /**
   * @method navigateToDestination
   * @description Navega a la página de detalle de un destino específico
   * @param {number} id - ID del destino a mostrar
   */
  navigateToDestination(id: number): void {
    this.router.navigate(['/destinations', id]);
  }
  
  /**
   * @method navigateToHotels
   * @description Navega a la página de hoteles
   */
  navigateToHotels(): void {
    this.router.navigate(['/hotels']);
  }
  
  /**
   * @method navigateToHotel
   * @description Navega a la página de detalle de un hotel específico
   * @param {number} id - ID del hotel a mostrar
   */
  navigateToHotel(id: number): void {
    this.router.navigate(['/hotels', id]);
  }
}

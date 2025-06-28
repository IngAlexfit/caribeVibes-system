import { Component } from '@angular/core';

/**
 * @class FooterComponent
 * @description Componente que muestra el pie de página de la aplicación.
 * Contiene información de contacto, enlaces útiles, formulario de newsletter y copyright.
 */
@Component({
  selector: 'app-footer',
  templateUrl: './footer.component.html',
  styleUrls: ['./footer.component.scss']
})
export class FooterComponent {
  /** @property {number} currentYear - Año actual para mostrar en el copyright */
  currentYear = new Date().getFullYear();

  /**
   * @method onNewsletterSubmit
   * @description Maneja el envío del formulario de suscripción al newsletter
   * @param {Event} event - Evento del formulario
   */
  onNewsletterSubmit(event: Event): void {
    event.preventDefault();
    // Aquí puedes implementar la lógica para suscribirse al newsletter
    console.log('Newsletter subscription submitted');
  }
}

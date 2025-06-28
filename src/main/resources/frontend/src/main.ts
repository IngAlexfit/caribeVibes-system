import { platformBrowserDynamic } from '@angular/platform-browser-dynamic';
import { AppModule } from './app/app.module';

/**
 * @description Punto de entrada principal de la aplicación Angular.
 * Inicializa el módulo raíz (AppModule) y arranca la aplicación en el navegador.
 * En caso de error durante el arranque, muestra el error en la consola.
 */
platformBrowserDynamic().bootstrapModule(AppModule)
  .catch(err => console.error(err));

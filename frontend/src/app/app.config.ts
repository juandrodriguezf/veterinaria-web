import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import { provideHttpClient, withInterceptors } from '@angular/common/http';
import { routes } from './app.routes';
import { authInterceptor } from './service/auth.interceptor';
import { errorInterceptor } from './service/error.interceptor';
import { SesionService } from './service/sesion.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptors([authInterceptor, errorInterceptor])),
    // Restaura la identidad desde /usuario/me antes de que el router
    // resuelva los guards: así el rol del JWT está disponible al navegar.
    provideAppInitializer(() => inject(SesionService).restaurarSesion()),
  ],
};

import {
  ApplicationConfig,
  inject,
  provideAppInitializer,
  provideZoneChangeDetection,
} from '@angular/core';
import { provideRouter } from '@angular/router';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { routes } from './app.routes';
import { AuthInterceptor } from './service/auth.interceptor';
import { ErrorInterceptor } from './service/error.interceptor';
import { SesionService } from './service/sesion.service';

export const appConfig: ApplicationConfig = {
  providers: [
    provideZoneChangeDetection({ eventCoalescing: true }),
    provideRouter(routes),
    provideHttpClient(withInterceptorsFromDi()),
    { provide: HTTP_INTERCEPTORS, useClass: AuthInterceptor, multi: true },
    { provide: HTTP_INTERCEPTORS, useClass: ErrorInterceptor, multi: true },
    // Restaura la identidad desde /usuario/me antes de que el router
    // resuelva los guards: así el rol del JWT está disponible al navegar.
    provideAppInitializer(() => inject(SesionService).restaurarSesion()),
  ],
};

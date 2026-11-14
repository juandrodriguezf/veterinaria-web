import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { SesionService } from './sesion.service';

/**
 * INTERCEPTOR - Errores de autenticación
 * Si el backend responde 401 (token vencido, alterado o cuenta eliminada)
 * se limpia la sesión y se vuelve al login. El propio login queda excluido
 * para no borrar la pantalla mientras el usuario corrige sus credenciales.
 */
export const errorInterceptor: HttpInterceptorFn = (req, next) => {
  const sesion = inject(SesionService);
  const router = inject(Router);

  return next(req).pipe(
    catchError((error: HttpErrorResponse) => {
      if (error.status === 401 && !req.url.endsWith('/login')) {
        sesion.cerrar();
        if (router.url !== '/login') {
          void router.navigate(['/login']);
        }
      }
      return throwError(() => error);
    }),
  );
};

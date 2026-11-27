import {
  HttpErrorResponse,
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Router } from '@angular/router';
import { Observable, catchError, throwError } from 'rxjs';
import { SesionService } from './sesion.service';

/**
 * INTERCEPTOR - Errores de autenticación
 * Si el backend responde 401 (token vencido, alterado o cuenta eliminada)
 * se limpia la sesión y se vuelve al login. El propio login queda excluido
 * para no borrar la pantalla mientras el usuario corrige sus credenciales.
 */
@Injectable()
export class ErrorInterceptor implements HttpInterceptor {
  private sesion = inject(SesionService);
  private router = inject(Router);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    return next.handle(req).pipe(
      catchError((error: HttpErrorResponse) => {
        if (error.status === 401 && !req.url.endsWith('/login')) {
          this.sesion.cerrar();
          if (this.router.url !== '/login') {
            void this.router.navigate(['/login']);
          }
        }
        return throwError(() => error);
      }),
    );
  }
}

import {
  HttpEvent,
  HttpHandler,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { SesionService } from './sesion.service';

/**
 * INTERCEPTOR - Autenticación
 * Adjunta el token JWT a cada petición saliente con el encabezado
 * Authorization: Bearer <token>. El login queda excluido porque todavía
 * no existe sesión cuando se envía.
 */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  private sesionService = inject(SesionService);

  intercept(
    req: HttpRequest<unknown>,
    next: HttpHandler,
  ): Observable<HttpEvent<unknown>> {
    const token = this.sesionService.token();

    if (!token || req.url.endsWith('/login')) {
      return next.handle(req);
    }

    return next.handle(
      req.clone({
        setHeaders: { Authorization: `Bearer ${token}` },
      }),
    );
  }
}

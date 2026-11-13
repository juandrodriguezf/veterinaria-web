import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { SesionService } from './sesion.service';

/**
 * INTERCEPTOR - Autenticación
 * Adjunta el token JWT a cada petición saliente con el encabezado
 * Authorization: Bearer <token>. El login queda excluido porque todavía
 * no existe sesión cuando se envía.
 */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const sesionService = inject(SesionService);
  const token = sesionService.token();

  if (!token || req.url.endsWith('/login')) {
    return next(req);
  }

  return next(
    req.clone({
      setHeaders: { Authorization: `Bearer ${token}` },
    }),
  );
};

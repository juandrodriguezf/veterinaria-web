import { inject } from '@angular/core';
import { CanActivateFn, Router } from '@angular/router';
import { rutaInicioPorRol, SesionService } from './sesion.service';

/**
 * GUARD - Sesión
 * Exige un token vigente; sin él, redirige al login. La identidad se
 * restaura antes del arranque del router, así el rol ya está disponible.
 */
export const authGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  return sesion.tokenVigente() ? true : router.parseUrl('/login');
};

/**
 * GUARD - Rol
 * Permite la ruta solo si el rol en memoria (firmado en el JWT) está en la
 * lista. Si no, devuelve al usuario a la página inicial de su propio rol.
 */
export function rolesGuard(roles: string[]): CanActivateFn {
  return () => {
    const sesion = inject(SesionService);
    const router = inject(Router);
    if (!sesion.tokenVigente()) {
      return router.parseUrl('/login');
    }
    const rol = sesion.rol();
    return roles.includes(rol) ? true : router.parseUrl(rutaInicioPorRol(rol));
  };
}

/**
 * GUARD - Inicio
 * Al abrir la página principal, si hay un JWT válido redirige al portal del
 * rol correspondiente (administrador, veterinario o cliente).
 */
export const homeRedirectGuard: CanActivateFn = () => {
  const sesion = inject(SesionService);
  const router = inject(Router);
  if (!sesion.tokenVigente()) {
    return true;
  }
  const rol = sesion.rol();
  return rol ? router.parseUrl(rutaInicioPorRol(rol)) : true;
};

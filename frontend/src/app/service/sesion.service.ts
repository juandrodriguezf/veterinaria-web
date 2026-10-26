import { Injectable, computed, signal } from '@angular/core';
import { ResultadoLogin } from '../models/resultado-login.model';

/**
 * Datos mínimos que se conservan del usuario autenticado. No se guarda la
 * contraseña ni el objeto completo: solo lo necesario para identificar la
 * cuenta y mostrar su nombre en el portal.
 */
export interface SesionUsuario {
  rol: string;
  id: number;
  nombre: string;
}

/**
 * SERVICIO - Sesión
 * Conserva la identidad del usuario autenticado en localStorage, de modo que
 * el portal sepa qué veterinario, administrador o cliente está usando la
 * aplicación. El backend sigue siendo la fuente de verdad de los datos; esta
 * sesión solo evita volver a pedir el nombre y el id después del login.
 */
@Injectable({
  providedIn: 'root',
})
export class SesionService {
  /** Clave única de la sesión dentro de localStorage. */
  private readonly clave = 'vetopia.sesion';

  /** Sesión actual; se carga al iniciar el servicio para sobrevivir recargas. */
  private readonly sesionActual = signal<SesionUsuario | null>(this.leerSesion());

  /** Sesión completa, solo lectura, para los componentes que la necesiten. */
  readonly sesion = this.sesionActual.asReadonly();

  /** Nombre del usuario autenticado (cadena vacía si no hay sesión). */
  readonly nombre = computed(() => this.sesionActual()?.nombre ?? '');

  /** Identificador del usuario autenticado (null si no hay sesión). */
  readonly id = computed(() => this.sesionActual()?.id ?? null);

  /** Rol del usuario autenticado (cadena vacía si no hay sesión). */
  readonly rol = computed(() => this.sesionActual()?.rol ?? '');

  /** Indica si la sesión actual pertenece a un veterinario. */
  readonly esVeterinario = computed(() => {
    const sesion = this.sesionActual();
    return sesion !== null && sesion.rol === 'VETERINARIO';
  });

  /** Indica si la sesión actual pertenece a un administrador. */
  readonly esAdministrador = computed(() => {
    const sesion = this.sesionActual();
    return sesion !== null && sesion.rol === 'ADMINISTRADOR';
  });

  /** Guarda los datos mínimos que devolvió el login y actualiza la señal. */
  guardar(resultado: ResultadoLogin) {
    const sesion: SesionUsuario = {
      rol: resultado.rol,
      id: resultado.id,
      nombre: resultado.nombre ?? '',
    };
    localStorage.setItem(this.clave, JSON.stringify(sesion));
    this.sesionActual.set(sesion);
  }

  /** Elimina la sesión del almacenamiento local y limpia la señal. */
  cerrar() {
    localStorage.removeItem(this.clave);
    this.sesionActual.set(null);
  }

  /** Lee y valida la sesión guardada; si está corrupta o incompleta la borra. */
  private leerSesion(): SesionUsuario | null {
    const guardada = localStorage.getItem(this.clave);
    if (!guardada) {
      return null;
    }
    try {
      const datos = JSON.parse(guardada) as Partial<SesionUsuario>;
      if (
        typeof datos.rol === 'string' &&
        Number.isInteger(datos.id) &&
        typeof datos.nombre === 'string'
      ) {
        return { rol: datos.rol, id: datos.id as number, nombre: datos.nombre };
      }
    } catch {
      // Sesión ilegible: se descarta y se vuelve a pedir el login.
    }
    localStorage.removeItem(this.clave);
    return null;
  }
}

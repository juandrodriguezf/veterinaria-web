import { Injectable, computed, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { firstValueFrom } from 'rxjs';
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

/** Página inicial de cada rol, usada al restaurar la sesión y en los guards. */
export function rutaInicioPorRol(rol: string): string {
  switch (rol) {
    case 'ADMINISTRADOR':
      return '/admin/dashboard';
    case 'VETERINARIO':
      return '/mascotas';
    case 'CLIENTE':
      return '/cliente/mascotas';
    default:
      return '/';
  }
}

/**
 * SERVICIO - Sesión
 * En localStorage vive únicamente el token JWT; el rol, el id y el nombre
 * se mantienen en memoria y se recuperan del backend (`/usuario/me`) al
 * abrir la aplicación. Aunque alguien edite el almacenamiento, no puede
 * cambiar el id ni el rol: el backend valida la firma del token y deriva
 * la identidad de él.
 */
@Injectable({
  providedIn: 'root',
})
export class SesionService {
  private http = inject(HttpClient);

  /** Clave única del token dentro de localStorage. */
  private readonly claveToken = 'vetopia.token';

  /** Clave anterior (rol/id/nombre); se elimina al iniciar sesión. */
  private readonly claveAnterior = 'vetopia.sesion';

  /** URL de la identidad del usuario autenticado. */
  private readonly urlPerfil = 'http://localhost:8080/usuario/me';

  /** Token vigente; se carga al iniciar el servicio para sobrevivir recargas. */
  private readonly tokenActual = signal<string | null>(this.leerToken());

  /** Identidad en memoria: nunca se persiste en localStorage. */
  private readonly usuarioActual = signal<SesionUsuario | null>(null);

  /** Token JWT, solo lectura. */
  readonly token = this.tokenActual.asReadonly();

  /** Sesión completa en memoria, solo lectura. */
  readonly sesion = this.usuarioActual.asReadonly();

  /** Indica si el token existe y no ha expirado (sin validar la firma). */
  readonly tokenVigente = computed(() => {
    const token = this.tokenActual();
    return token !== null && !this.tokenExpirado(token);
  });

  /** Nombre del usuario autenticado (cadena vacía si no hay sesión). */
  readonly nombre = computed(() => this.usuarioActual()?.nombre ?? '');

  /** Identificador del usuario autenticado (null si no hay sesión). */
  readonly id = computed(() => this.usuarioActual()?.id ?? null);

  /** Rol del usuario autenticado (cadena vacía si no hay sesión). */
  readonly rol = computed(() => this.usuarioActual()?.rol ?? '');

  /** Indica si la sesión actual pertenece a un veterinario. */
  readonly esVeterinario = computed(() => this.rol() === 'VETERINARIO');

  /** Indica si la sesión actual pertenece a un administrador. */
  readonly esAdministrador = computed(() => this.rol() === 'ADMINISTRADOR');

  /**
   * Restaura la sesión al abrir la aplicación: si hay token vigente pide
   * la identidad al backend; si el token expiró o es inválido lo elimina.
   */
  async restaurarSesion(): Promise<void> {
    const token = this.tokenActual();
    if (!token || this.tokenExpirado(token)) {
      this.cerrar();
      return;
    }
    try {
      const perfil = await firstValueFrom(this.http.get<SesionUsuario>(this.urlPerfil));
      this.usuarioActual.set({
        rol: perfil.rol,
        id: perfil.id,
        nombre: perfil.nombre ?? '',
      });
    } catch {
      this.cerrar();
    }
  }

  /**
   * Guarda el token y mantiene la identidad solo en memoria. El id y el rol
   * salen de la respuesta del backend, no de un valor editable por el usuario.
   */
  guardar(resultado: ResultadoLogin) {
    if (resultado.token) {
      localStorage.setItem(this.claveToken, resultado.token);
      this.tokenActual.set(resultado.token);
    }
    this.usuarioActual.set({
      rol: resultado.rol,
      id: resultado.id,
      nombre: resultado.nombre ?? '',
    });
    // Limpieza de la sesión anterior, que guardaba id y rol en localStorage.
    localStorage.removeItem(this.claveAnterior);
  }

  /** Elimina el token del almacenamiento local y limpia la sesión en memoria. */
  cerrar() {
    localStorage.removeItem(this.claveToken);
    localStorage.removeItem(this.claveAnterior);
    this.tokenActual.set(null);
    this.usuarioActual.set(null);
  }

  /** Lee el token guardado; si está vacío lo descarta. */
  private leerToken(): string | null {
    const token = localStorage.getItem(this.claveToken);
    if (!token || !token.trim()) {
      return null;
    }
    return token;
  }

  /** Comprueba la fecha de expiración del payload sin validar la firma. */
  private tokenExpirado(token: string): boolean {
    try {
      const payload = token.split('.')[1];
      const normalizado = payload.replace(/-/g, '+').replace(/_/g, '/');
      const datos = JSON.parse(atob(normalizado)) as { exp?: number };
      if (!datos.exp) {
        return true;
      }
      return datos.exp * 1000 <= Date.now();
    } catch {
      return true;
    }
  }
}

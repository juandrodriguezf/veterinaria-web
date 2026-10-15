import type { Mascota } from './mascota.model';

/**
 * Cliente. El formulario lo usa como cuerpo (con contrasena y la colección
 * de mascotas vacía); la API lo devuelve sin contrasena y con el conteo de
 * mascotas (mascotaCount), que ya no hay que calcular cruzando listados.
 */
export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  correo: string;
  contrasena?: string;
  celular?: string;
  estado: string;
  mascotas?: Mascota[];
  mascotaCount?: number;
}

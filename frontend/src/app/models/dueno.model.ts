import type { Mascota } from './mascota.model';

export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  correo: string;
  contrasena: string;
  celular?: string;
  estado: string;
  mascotas: Mascota[];
}

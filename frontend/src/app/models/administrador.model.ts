import type { Veterinario } from './veterinario.model';

export interface Administrador {
  id: number;
  cedula: string;
  correo: string;
  contrasena: string;
  nombre: string;
  veterinarios: Veterinario[];
}

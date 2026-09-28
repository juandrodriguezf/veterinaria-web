import type { Administrador } from './administrador.model';
import type { Tratamiento } from './tratamiento.model';

export interface Veterinario {
  id: number;
  cedula: string;
  contrasena: string;
  correo: string;
  especialidad?: string;
  numeroAtenciones: number;
  nombre: string;
  estado: string;
  urlFoto?: string;
  administrador?: Administrador;
  tratamientos: Tratamiento[];
}

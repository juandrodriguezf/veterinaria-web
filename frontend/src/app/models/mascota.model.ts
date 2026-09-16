import type { Dueno } from './dueno.model';
import type { Tratamiento } from './tratamiento.model';

export interface Mascota {
  id: number;
  nombre: string;
  especie: string;
  raza?: string;
  edad?: number;
  sexo?: string;
  imagen?: string;
  pesoKg?: number;
  color?: string;
  fechaIngreso?: string;
  enfermedad?: string;
  dueno: Dueno;
  estado: string;
  tratamientos: Tratamiento[];
}

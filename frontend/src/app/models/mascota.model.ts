import type { DuenoResumen } from './dueno-resumen.model';
import type { Tratamiento } from './tratamiento.model';

/**
 * Mascota tal como la devuelve la API. Los listados traen el resumen (sin
 * dueño); la ficha trae el dueño resumido. El historial de tratamientos
 * llega en una consulta aparte y el componente lo asigna.
 */
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
  dueno?: DuenoResumen;
  estado: string;
  tratamientos?: Tratamiento[];
}

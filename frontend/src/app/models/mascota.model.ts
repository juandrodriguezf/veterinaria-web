import type { Dueno } from './dueno.model';
import type { Tratamiento } from './tratamiento.model';

/**
 * Mascota tal como la devuelve la API. El dueño NO viaja anidado: solo
 * llega su id en `duenoId`, así el listado no arrastra al cliente. La
 * ficha pide el cliente aparte con ese id y el formulario envía `dueno`
 * al crear/editar. El historial de tratamientos llega en una consulta
 * aparte y el componente lo asigna.
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
  duenoId?: number;
  /** Solo se envía al crear/editar; la API nunca lo devuelve anidado. */
  dueno?: Dueno;
  estado: string;
  tratamientos?: Tratamiento[];
}

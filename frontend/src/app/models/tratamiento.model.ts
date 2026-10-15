import type { Droga } from './droga.model';
import type { Veterinario } from './veterinario.model';

/**
 * Tratamiento tal como lo devuelve la API. La mascota va marcada como
 * WRITE_ONLY en el backend (solo se envía al crear el tratamiento), y la
 * droga y el veterinario llegan recortados a lo que la pantalla usa.
 */
export interface Tratamiento {
  id: number;
  fecha: string;
  droga: Droga;
  veterinario: Veterinario;
}

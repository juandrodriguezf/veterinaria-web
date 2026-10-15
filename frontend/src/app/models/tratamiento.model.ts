import type { DrogaResumen } from './droga-resumen.model';
import type { VeterinarioResumen } from './veterinario-resumen.model';

/**
 * Tratamiento tal como lo devuelve la API: la mascota solo viaja por su
 * id y la droga y el veterinario van como resúmenes (el backend ya no
 * anida las entidades completas).
 */
export interface Tratamiento {
  id: number;
  fecha: string;
  mascotaId?: number;
  droga: DrogaResumen;
  veterinario: VeterinarioResumen;
}

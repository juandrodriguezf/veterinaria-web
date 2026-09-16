import type { Droga } from './droga.model';
import type { Mascota } from './mascota.model';
import type { Veterinario } from './veterinario.model';

export interface Tratamiento {
  id: number;
  fecha: string;
  mascota: Mascota;
  droga: Droga;
  veterinario: Veterinario;
}

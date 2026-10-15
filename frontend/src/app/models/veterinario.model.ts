/**
 * Veterinario tal como lo devuelve la API: solo id, nombre y especialidad.
 * El resto de campos no se expone porque ninguna pantalla los usa.
 */
export interface Veterinario {
  id: number;
  nombre: string;
  especialidad?: string;
}

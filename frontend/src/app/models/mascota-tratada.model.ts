/**
 * MODELO - Mascota tratada
 * Resumen por mascota que arma la página de Tratadas a partir del historial
 * de tratamientos del veterinario: cuántas atenciones recibió y cuándo fue
 * la última. No es una entidad del backend, es una vista del listado.
 */
export interface MascotaTratada {
  mascotaId: number | null;
  nombre: string;
  totalTratamientos: number;
  ultimaFecha: string;
}

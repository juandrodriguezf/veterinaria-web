/**
 * MODELO - Tratamiento
 * Tal como lo devuelve la API: datos planos de la droga, del veterinario y
 * de la mascota (el backend ya no anida las entidades, ver @JsonProperty en
 * Tratamiento). Los nombres quedan nulos cuando la mascota o el veterinario
 * fueron retirados del sistema.
 */
export interface Tratamiento {
  id: number;
  fecha: string;
  drogaId: number | null;
  drogaNombre: string | null;
  veterinarioId: number | null;
  veterinarioNombre: string | null;
  veterinarioEspecialidad: string | null;
  mascotaId: number | null;
  mascotaNombre: string | null;
}

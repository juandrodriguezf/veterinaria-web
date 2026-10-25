/**
 * MODELO - Veterinario
 * Tal como lo devuelve la API del portal del administrador: datos del
 * perfil y estado laboral. La contraseña solo viaja hacia el backend
 * (JsonProperty WRITE_ONLY), por eso es opcional.
 */
export interface Veterinario {
  id: number;
  cedula: string;
  nombre: string;
  correo: string;
  especialidad?: string;
  estado: string;
  urlFoto?: string;
  numeroAtenciones?: number;
  contrasena?: string;
}

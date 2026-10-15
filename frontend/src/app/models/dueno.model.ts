/**
 * Cliente. La API lo devuelve sin contraseña (@JsonProperty WRITE_ONLY) y
 * con el conteo de mascotas (mascotaCount) que ya trae el listado, sin
 * tener que cruzar las mascotas. El formulario sí envía la contraseña.
 */
export interface Dueno {
  id: number;
  cedula: string;
  nombre: string;
  correo: string;
  contrasena?: string;
  celular?: string;
  estado: string;
  mascotaCount?: number;
}

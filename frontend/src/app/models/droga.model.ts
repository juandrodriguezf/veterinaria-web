/**
 * Droga tal como la devuelve la API: solo id, nombre y stock disponible.
 * Los precios y las unidades vendidas no se exponen porque ninguna
 * pantalla los usa.
 */
export interface Droga {
  id: number;
  nombre: string;
  unidadesDisponibles: number;
}

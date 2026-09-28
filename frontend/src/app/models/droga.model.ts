import type { Tratamiento } from './tratamiento.model';

export interface Droga {
  id: number;
  nombre: string;
  precioCompra: number;
  precioVenta: number;
  unidadesDisponibles: number;
  unidadesVendidas: number;
  tratamientos: Tratamiento[];
}

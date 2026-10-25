/**
 * MODELO - Dashboard
 * Interfaces que reflejan el DTO ResumenDashboard del backend
 * (GET /administrador/dashboard). Los nombres coinciden campo por campo
 * con el JSON, igual que los demás modelos del proyecto.
 */

/** Fila de conteo del dashboard: medicamento y cantidad asociada. */
export interface ConteoMedicamento {
  nombre: string;
  cantidad: number;
}

/** Indicadores (KPIs) que muestra el portal del administrador. */
export interface ResumenDashboard {
  tratamientosUltimoMes: number;
  tratamientosPorMedicamento: ConteoMedicamento[];
  veterinariosActivos: number;
  veterinariosInactivos: number;
  mascotasTotales: number;
  mascotasActivas: number;
  ventasTotales: number;
  gananciasTotales: number;
  topMedicamentos: ConteoMedicamento[];
}

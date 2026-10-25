import { Component, input } from '@angular/core';
import { ConteoMedicamento } from '../../../../models/dashboard.model';

/**
 * Tabla de tratamientos del último mes agrupados por medicamento. Recibe
 * las filas ya calculadas por el backend; solo las presenta.
 */
@Component({
  selector: 'app-kpi-medicamentos',
  templateUrl: './kpi-medicamentos.component.html',
  styleUrl: './kpi-medicamentos.component.scss',
})
export class KpiMedicamentosComponent {
  /** Filas medicamento - cantidad del último mes. */
  filas = input<ConteoMedicamento[]>([]);
}

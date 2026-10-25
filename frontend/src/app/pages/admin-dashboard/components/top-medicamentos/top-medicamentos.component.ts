import { Component, input } from '@angular/core';
import { ConteoMedicamento } from '../../../../models/dashboard.model';

/**
 * Top 3 de medicamentos con más unidades vendidas. Pinta una barra
 * proporcional al primero del ranking para comparar de un vistazo.
 */
@Component({
  selector: 'app-top-medicamentos',
  templateUrl: './top-medicamentos.component.html',
  styleUrl: './top-medicamentos.component.scss',
})
export class TopMedicamentosComponent {
  /** Filas del top, ya ordenadas por el backend. */
  filas = input<ConteoMedicamento[]>([]);

  /** Mayor cantidad del top, para escalar las barras. */
  mayor(): number {
    return this.filas().reduce((maximo, fila) => Math.max(maximo, fila.cantidad), 0);
  }

  /** Ancho de la barra respecto al primero del ranking (0 a 100). */
  porcentaje(fila: ConteoMedicamento): number {
    const mayor = this.mayor();
    return mayor === 0 ? 0 : Math.round((fila.cantidad / mayor) * 100);
  }
}

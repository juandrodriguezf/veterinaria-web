import { Component, input } from '@angular/core';
import { Tratamiento } from '../../../../models/tratamiento.model';

/**
 * Historial de tratamientos en el detalle del cliente: la lista de
 * medicamentos suministrados con el médico tratante.
 */
@Component({
  selector: 'app-detalle-historial',
  templateUrl: './detalle-historial.component.html',
})
export class DetalleHistorialComponent {
  tratamientos = input<Tratamiento[]>();
}

import { Component, input } from '@angular/core';
import { DuenoResumen } from '../../../../models/dueno-resumen.model';

/**
 * Tarjeta del propietario en la ficha clínica: nombre, teléfono y correo
 * del dueño (llegó como resumen en el detalle de la mascota).
 */
@Component({
  selector: 'app-ficha-propietario',
  templateUrl: './ficha-propietario.component.html',
})
export class FichaPropietarioComponent {
  dueno = input<DuenoResumen>();
}

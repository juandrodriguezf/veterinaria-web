import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { EdadDescripcionPipe } from '../../../../pipes/edad-descripcion.pipe';
import { PesoDescripcionPipe } from '../../../../pipes/peso-descripcion.pipe';

/**
 * Bloque de datos de la mascota en el detalle del cliente: foto y ficha
 * resumida (especie, edad, peso, color y fecha de ingreso).
 */
@Component({
  selector: 'app-detalle-mascota',
  imports: [EdadDescripcionPipe, PesoDescripcionPipe],
  templateUrl: './detalle-mascota.component.html',
})
export class DetalleMascotaComponent {
  mascota = input.required<Mascota>();

  numeroPaciente = input.required<string>();
}

import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { descripcionEdad, descripcionPeso, formatearFechaLarga } from '../../../../utils/mascota.utils';

/**
 * Bloque de datos de la mascota en el detalle del cliente: foto y ficha
 * resumida (especie, edad, peso, color y fecha de ingreso).
 */
@Component({
  selector: 'app-detalle-mascota',
  templateUrl: './detalle-mascota.component.html',
})
export class DetalleMascotaComponent {
  mascota = input.required<Mascota>();

  numeroPaciente = input.required<string>();

  readonly descripcionEdad = descripcionEdad;

  readonly descripcionPeso = descripcionPeso;

  readonly formatearFechaLarga = formatearFechaLarga;
}

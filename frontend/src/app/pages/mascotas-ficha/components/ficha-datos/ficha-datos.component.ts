import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { EdadDescripcionPipe } from '../../../../pipes/edad-descripcion.pipe';
import { PesoDescripcionPipe } from '../../../../pipes/peso-descripcion.pipe';

/**
 * Tarjeta con los datos de la mascota (foto, especie/raza, edad, peso y
 * sexo) para la ficha clínica.
 */
@Component({
  selector: 'app-ficha-datos',
  imports: [EdadDescripcionPipe, PesoDescripcionPipe],
  templateUrl: './ficha-datos.component.html',
})
export class FichaDatosComponent {
  mascota = input.required<Mascota>();
}

import { Component, input } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';
import { descripcionEdad, descripcionPeso } from '../../../../utils/mascota.utils';

/**
 * Tarjeta con los datos de la mascota (foto, especie/raza, edad, peso y
 * sexo) para la ficha clínica.
 */
@Component({
  selector: 'app-ficha-datos',
  templateUrl: './ficha-datos.component.html',
})
export class FichaDatosComponent {
  mascota = input.required<Mascota>();

  readonly descripcionEdad = descripcionEdad;

  readonly descripcionPeso = descripcionPeso;
}

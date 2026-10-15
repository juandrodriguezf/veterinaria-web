import { Component, input, output } from '@angular/core';
import { Mascota } from '../../../../models/mascota.model';

/**
 * Cabecera de la ficha clínica: título y estado del paciente con el
 * interruptor para darla de alta o reingresarla.
 */
@Component({
  selector: 'app-ficha-encabezado',
  templateUrl: './ficha-encabezado.component.html',
})
export class FichaEncabezadoComponent {
  mascota = input.required<Mascota>();

  alternar = output<void>();
}

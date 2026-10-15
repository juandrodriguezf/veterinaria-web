import { Component, input } from '@angular/core';
import { Tratamiento } from '../../../../models/tratamiento.model';

/**
 * Historial médico cronológico de la ficha clínica: la línea de tiempo de
 * tratamientos recibidos por la mascota.
 */
@Component({
  selector: 'app-ficha-historial',
  templateUrl: './ficha-historial.component.html',
})
export class FichaHistorialComponent {
  tratamientos = input<Tratamiento[]>();
}

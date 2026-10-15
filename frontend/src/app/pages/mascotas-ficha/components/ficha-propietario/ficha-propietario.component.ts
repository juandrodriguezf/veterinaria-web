import { Component, input } from '@angular/core';
import { Dueno } from '../../../../models/dueno.model';

/**
 * Tarjeta del propietario en la ficha clínica: nombre, teléfono y correo
 * del dueño. El dueño ya no viaja anidado en la mascota, así que el
 * componente padre lo pide aparte por su id y lo pasa aquí.
 */
@Component({
  selector: 'app-ficha-propietario',
  templateUrl: './ficha-propietario.component.html',
})
export class FichaPropietarioComponent {
  dueno = input<Dueno>();
}

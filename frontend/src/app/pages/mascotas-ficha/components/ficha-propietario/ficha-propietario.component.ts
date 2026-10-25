import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

/**
 * Tarjeta del propietario en la ficha clínica: nombre, teléfono y correo
 * del dueño. El dueño ya no viaja anidado en la mascota, así que el
 * componente padre lo pide aparte por su id y lo pasa aquí. El enlace
 * "Ver perfil completo" abre la pantalla de edición del cliente, que es
 * donde el portal muestra todos sus datos.
 */
@Component({
  selector: 'app-ficha-propietario',
  imports: [RouterLink],
  templateUrl: './ficha-propietario.component.html',
})
export class FichaPropietarioComponent {
  dueno = input<Dueno>();
}

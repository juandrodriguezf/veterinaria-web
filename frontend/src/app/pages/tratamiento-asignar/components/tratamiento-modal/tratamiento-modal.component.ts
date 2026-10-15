import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Droga } from '../../../../models/droga.model';

/**
 * Modal de confirmación que aparece tras asignar un tratamiento, con el
 * stock restante de la droga y el enlace de vuelta a la ficha.
 */
@Component({
  selector: 'app-tratamiento-modal',
  imports: [RouterLink],
  templateUrl: './tratamiento-modal.component.html',
})
export class TratamientoModalComponent {
  droga = input.required<Droga>();

  mascotaId = input<number>();

  cerrar = output<void>();
}

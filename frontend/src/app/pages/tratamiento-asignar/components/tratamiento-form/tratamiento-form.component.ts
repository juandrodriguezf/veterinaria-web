import { Component, input, output } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Droga } from '../../../../models/droga.model';
import { Mascota } from '../../../../models/mascota.model';

/**
 * Formulario de asignación de tratamiento: responsable, mascota activa,
 * fecha y medicamento. Recibe el FormGroup de la página y avisa al
 * enviarlo.
 */
@Component({
  selector: 'app-tratamiento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './tratamiento-form.component.html',
})
export class TratamientoFormComponent {
  form = input.required<FormGroup>();

  mascotas = input<Mascota[]>([]);

  drogas = input<Droga[]>([]);

  nombreVeterinario = input<string>('');

  aplicar = output<void>();
}

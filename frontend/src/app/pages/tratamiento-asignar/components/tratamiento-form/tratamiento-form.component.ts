import { Component, input, output } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Droga } from '../../../../models/droga.model';
import { Mascota } from '../../../../models/mascota.model';

/**
 * Formulario de asignación de tratamiento: responsable, mascota activa,
 * fecha y medicamento. Recibe el FormGroup de la página y avisa al
 * enviarlo. El selector de medicamentos trae un buscador y solo ofrece
 * drogas con stock; sin ninguna disponible, el formulario queda bloqueado.
 */
@Component({
  selector: 'app-tratamiento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './tratamiento-form.component.html',
})
export class TratamientoFormComponent {
  form = input.required<FormGroup>();

  /** Buscador del selector de medicamentos (control suelto, fuera del FormGroup). */
  filtro = input.required<FormControl>();

  mascotas = input<Mascota[]>([]);

  drogas = input<Droga[]>([]);

  /** Sin medicamentos con stock no se puede aplicar el tratamiento. */
  sinMedicamentos = input<boolean>(false);

  nombreVeterinario = input<string>('');

  aplicar = output<void>();
}

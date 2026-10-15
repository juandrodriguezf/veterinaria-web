import { Component, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { Dueno } from '../../../../models/dueno.model';

/**
 * Tarjeta con los campos del formulario de mascota (nombre, especie, raza,
 * sexo, edad, peso, color, imagen, dueño y estado). Recibe el FormGroup de
 * la página.
 */
@Component({
  selector: 'app-mascota-campos',
  imports: [ReactiveFormsModule],
  templateUrl: './mascota-campos.component.html',
})
export class MascotaCamposComponent {
  form = input.required<FormGroup>();

  duenos = input<Dueno[]>([]);

  isEdit = input<boolean>(false);
}

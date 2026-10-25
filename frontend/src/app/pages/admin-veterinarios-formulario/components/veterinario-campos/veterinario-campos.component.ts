import { Component, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Tarjeta con los campos del formulario de veterinario (cédula, nombre,
 * correo, especialidad, contraseña y estado). Recibe el FormGroup de la
 * página y solo presenta los controles y sus mensajes de validación.
 */
@Component({
  selector: 'app-veterinario-campos',
  imports: [ReactiveFormsModule],
  templateUrl: './veterinario-campos.component.html',
})
export class VeterinarioCamposComponent {
  form = input.required<FormGroup>();
}

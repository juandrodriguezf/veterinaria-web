import { Component, input } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';

/**
 * Tarjeta con los campos del formulario de cliente (cédula, nombre, correo,
 * contraseña, teléfono y estado). Recibe el FormGroup de la página.
 */
@Component({
  selector: 'app-cliente-campos',
  imports: [ReactiveFormsModule],
  templateUrl: './cliente-campos.component.html',
})
export class ClienteCamposComponent {
  form = input.required<FormGroup>();
}

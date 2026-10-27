import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { descripcionEdad } from '../../../../utils/mascota.utils';

/**
 * Tabla de mascotas del portal del cliente.
 * Recibe el listado de la página y solo se encarga de presentarlo,
 * incluido el estado vacío y el enlace al detalle de cada mascota.
 */
@Component({
  selector: 'app-cliente-mascota-tabla',
  imports: [RouterLink],
  templateUrl: './cliente-mascota-tabla.component.html',
})
export class ClienteMascotaTablaComponent {
  mascotas = input<Mascota[]>([]);

  nombre = input<string>('');

  idUsuario = input.required<number>();

  readonly descripcionEdad = descripcionEdad;

  numeroPaciente(mascota: Mascota): string {
    return String(mascota.id).padStart(4, '0');
  }
}

import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { descripcionEdad } from '../../../../utils/mascota.utils';

@Component({
  selector: 'app-mascota-tabla',
  imports: [RouterLink],
  templateUrl: './mascota-tabla.component.html',
  styleUrl: './mascota-tabla.component.scss',
})
export class MascotaTablaComponent {
  mascotas = input<Mascota[]>();

  nombre = input<string>();

  mascotaSeleccionada = output<Mascota>();

  readonly descripcionEdad = descripcionEdad;

  seleccionar(mascota: Mascota) {
    this.mascotaSeleccionada.emit(mascota);
  }

  numeroPaciente(mascota: Mascota) {
    return mascota.id.toString().padStart(4, '0');
  }
}

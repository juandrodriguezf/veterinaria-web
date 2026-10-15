import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';
import { EdadDescripcionPipe } from '../../../../pipes/edad-descripcion.pipe';

@Component({
  selector: 'app-mascota-tabla',
  imports: [RouterLink, EdadDescripcionPipe],
  templateUrl: './mascota-tabla.component.html',
  styleUrl: './mascota-tabla.component.scss',
})
export class MascotaTablaComponent {
  mascotas = input<Mascota[]>();

  nombre = input<string>();

  mascotaSeleccionada = output<Mascota>();

  seleccionar(mascota: Mascota) {
    this.mascotaSeleccionada.emit(mascota);
  }

  numeroPaciente(mascota: Mascota) {
    return mascota.id.toString().padStart(4, '0');
  }
}

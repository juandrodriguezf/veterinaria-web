import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Mascota } from '../../../../models/mascota.model';

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

  seleccionar(mascota: Mascota) {
    this.mascotaSeleccionada.emit(mascota);
  }

  numeroPaciente(mascota: Mascota) {
    return mascota.id.toString().padStart(4, '0');
  }

  descripcionEdad(mascota: Mascota) {
    if (mascota.edad === undefined) {
      return 'No registrada';
    }
    if (mascota.edad < 1) {
      const meses = Math.max(1, Math.round(mascota.edad * 12));
      return meses + (meses === 1 ? ' mes' : ' meses');
    }
    const anios = Math.floor(mascota.edad);
    return anios + (anios === 1 ? ' año' : ' años');
  }
}

import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

@Component({
  selector: 'app-cliente-tabla',
  imports: [RouterLink],
  templateUrl: './cliente-tabla.component.html',
  styleUrl: './cliente-tabla.component.scss',
})
export class ClienteTablaComponent {
  duenos = input<Dueno[]>();

  nombre = input<string>();

  duenoSeleccionado = output<Dueno>();

  duenoEliminado = output<Dueno>();

  seleccionar(dueno: Dueno) {
    this.duenoSeleccionado.emit(dueno);
  }

  solicitarEliminacion(dueno: Dueno) {
    this.duenoEliminado.emit(dueno);
  }

  cantidadMascotas(dueno: Dueno) {
    const total = dueno.mascotas.length;
    return total === 1 ? '1 mascota' : total + ' mascotas';
  }
}
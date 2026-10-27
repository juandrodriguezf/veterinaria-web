import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Dueno } from '../../../../models/dueno.model';

/**
 * Tabla de clientes del portal del veterinario.
 * Presenta el listado y emite las acciones de alternar estado y eliminar.
 */
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
    const total = dueno.mascotaCount ?? 0;
    return total === 1 ? '1 mascota' : total + ' mascotas';
  }
}
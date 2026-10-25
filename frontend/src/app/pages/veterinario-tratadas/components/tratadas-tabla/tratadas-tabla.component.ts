import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { MascotaTratada } from '../../../../models/mascota-tratada.model';

/**
 * Tabla de mascotas atendidas por el veterinario: una fila por mascota con
 * el total de tratamientos y la fecha de la última atención.
 */
@Component({
  selector: 'app-tratadas-tabla',
  imports: [RouterLink],
  templateUrl: './tratadas-tabla.component.html',
  styleUrl: './tratadas-tabla.component.scss',
})
export class TratadasTablaComponent {
  filas = input<MascotaTratada[]>();

  /** Término de búsqueda, para el mensaje del estado vacío. */
  termino = input<string>();

  /** Texto de tratamientos, sin mostrar "undefined". */
  tratamientos(fila: MascotaTratada) {
    return fila.totalTratamientos === 1 ? '1 tratamiento' : fila.totalTratamientos + ' tratamientos';
  }

  /** Código del paciente con ceros a la izquierda (por ejemplo 0002). */
  codigoPaciente(fila: MascotaTratada) {
    return fila.mascotaId === null ? '' : fila.mascotaId.toString().padStart(4, '0');
  }
}

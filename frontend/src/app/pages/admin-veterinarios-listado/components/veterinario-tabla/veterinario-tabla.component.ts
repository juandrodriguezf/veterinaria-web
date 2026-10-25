import { Component, input, output } from '@angular/core';
import { RouterLink } from '@angular/router';
import { Veterinario } from '../../../../models/veterinario.model';

/**
 * Tabla de veterinarios del portal del administrador. Emite el
 * veterinario seleccionado (para activar/desactivar) y el que se quiere
 * eliminar; la página se encarga de las peticiones y la confirmación.
 */
@Component({
  selector: 'app-veterinario-tabla',
  imports: [RouterLink],
  templateUrl: './veterinario-tabla.component.html',
  styleUrl: './veterinario-tabla.component.scss',
})
export class VeterinarioTablaComponent {
  veterinarios = input<Veterinario[]>();

  /** Término de búsqueda, para el mensaje del estado vacío. */
  termino = input<string>();

  veterinarioSeleccionado = output<Veterinario>();

  veterinarioEliminado = output<Veterinario>();

  seleccionar(veterinario: Veterinario) {
    this.veterinarioSeleccionado.emit(veterinario);
  }

  solicitarEliminacion(veterinario: Veterinario) {
    this.veterinarioEliminado.emit(veterinario);
  }

  /** Texto de atenciones acumuladas, sin mostrar "undefined". */
  atenciones(veterinario: Veterinario) {
    const total = veterinario.numeroAtenciones ?? 0;
    return total === 1 ? '1 atención' : total + ' atenciones';
  }
}

import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formatea la edad de una mascota en años o meses legibles ("5 años",
 * "8 meses", "No registrada"). Estaba duplicado en la ficha, el detalle
 * y la tabla; ahora vive en un solo pipe.
 */
@Pipe({ name: 'edadDescripcion' })
export class EdadDescripcionPipe implements PipeTransform {
  transform(edad: number | undefined | null): string {
    if (edad === undefined || edad === null) {
      return 'No registrada';
    }
    if (edad < 1) {
      const meses = Math.max(1, Math.round(edad * 12));
      return meses + (meses === 1 ? ' mes' : ' meses');
    }
    const anios = Math.floor(edad);
    return anios + (anios === 1 ? ' año' : ' años');
  }
}

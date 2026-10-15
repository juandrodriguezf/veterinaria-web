import { Pipe, PipeTransform } from '@angular/core';

/**
 * Formatea el peso de una mascota en kilogramos ("32.0 kg") o devuelve
 * "No registrado" cuando no hay dato. Estaba duplicado en la ficha y el
 * detalle; ahora vive en un solo pipe.
 */
@Pipe({ name: 'pesoDescripcion' })
export class PesoDescripcionPipe implements PipeTransform {
  transform(peso: number | undefined | null): string {
    return peso === undefined || peso === null ? 'No registrado' : peso.toFixed(1) + ' kg';
  }
}

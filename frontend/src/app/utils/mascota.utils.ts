/**
 * Formatea la edad de una mascota en años o meses legibles ("5 años",
 * "8 meses", "No registrada"). Estaba duplicado en la ficha, el detalle
 * y la tabla.
 */
export function descripcionEdad(edad: number | undefined | null): string {
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

/** Formatea el peso de una mascota en kilogramos ("32.0 kg") o "No registrado". */
export function descripcionPeso(peso: number | undefined | null): string {
  return peso === undefined || peso === null ? 'No registrado' : peso.toFixed(1) + ' kg';
}

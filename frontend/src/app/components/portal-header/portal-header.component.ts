import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Encabezado del portal del veterinario. Se compartía copiado en seis
 * pantallas; aquí queda en un solo componente con dos variantes: la
 * compacta (solo logo, enlaces y salir) y la completa, cuando se pasa el
 * nombre del veterinario, que añade la campana y el avatar.
 */
@Component({
  selector: 'app-portal-header',
  imports: [RouterLink],
  templateUrl: './portal-header.component.html',
  styleUrl: './portal-header.component.scss',
})
export class PortalHeaderComponent {
  /** Pestaña activa del portal (para resaltar el enlace correspondiente). */
  activo = input<'mascotas' | 'clientes' | 'tratamientos'>();

  /** Nombre del veterinario; si llega, se muestran la campana y el avatar. */
  usuario = input<string>('');

  /** Clases del enlace según sea o no la pestaña activa. */
  clasePestana(pestana: 'mascotas' | 'clientes' | 'tratamientos') {
    return this.activo() === pestana
      ? 'text-portal-primary font-portal-body-md text-portal-body-md font-bold border-b-2 border-portal-primary pb-1'
      : 'text-gray-400 hover:text-portal-primary transition-colors font-portal-body-md text-portal-body-md';
  }
}

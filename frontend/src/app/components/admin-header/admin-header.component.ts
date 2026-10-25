import { Component, input } from '@angular/core';
import { RouterLink } from '@angular/router';

/**
 * Encabezado del portal del administrador. Sigue el mismo patrón de
 * portal-header (veterinario) y cliente-header (cliente): un único
 * componente que recibe la sección activa y el nombre del administrador.
 */
@Component({
  selector: 'app-admin-header',
  imports: [RouterLink],
  templateUrl: './admin-header.component.html',
  styleUrl: './admin-header.component.scss',
})
export class AdminHeaderComponent {
  /** Pestaña activa del portal (para resaltar el enlace correspondiente). */
  activo = input<'dashboard' | 'veterinarios'>();

  /** Nombre del administrador que se muestra junto al logo. */
  usuario = input<string>('');

  /** Clases del enlace según sea o no la pestaña activa. */
  clasePestana(pestana: 'dashboard' | 'veterinarios') {
    return this.activo() === pestana
      ? 'text-portal-primary font-portal-body-md text-portal-body-md font-bold border-b-2 border-portal-primary pb-1'
      : 'text-gray-400 hover:text-portal-primary transition-colors font-portal-body-md text-portal-body-md';
  }
}

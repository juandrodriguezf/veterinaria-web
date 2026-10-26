import { Component, HostListener, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SesionService } from '../../service/sesion.service';

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
  private sesionService = inject(SesionService);

  /** Pestaña activa del portal (para resaltar el enlace correspondiente). */
  activo = input<'dashboard' | 'veterinarios'>();

  /** Nombre del administrador que se muestra junto al logo. */
  usuario = input<string>('');

  /** Controla la apertura del menú móvil. */
  menuAbierto = signal(false);

  /** Abre o cierra el menú móvil. */
  alternarMenu() {
    this.menuAbierto.update((abierto) => !abierto);
  }

  /** Cierra el menú móvil al navegar o al pulsar Escape. */
  cerrarMenu() {
    this.menuAbierto.set(false);
  }

  /** Elimina la sesión antes de que el enlace Logout navegue al inicio. */
  cerrarSesion() {
    this.sesionService.cerrar();
    this.cerrarMenu();
  }

  @HostListener('document:keydown.escape')
  cerrarConEscape() {
    this.cerrarMenu();
  }

  /** Clases del enlace según sea o no la pestaña activa. */
  clasePestana(pestana: 'dashboard' | 'veterinarios') {
    return this.activo() === pestana
      ? 'text-portal-primary font-portal-body-md text-portal-body-md font-bold border-b-2 border-portal-primary pb-1'
      : 'text-gray-400 hover:text-portal-primary transition-colors font-portal-body-md text-portal-body-md';
  }

  /** Clases del enlace apilado del menú móvil. */
  clasePestanaMovil(pestana: 'dashboard' | 'veterinarios') {
    const base =
      'block py-3 border-b border-white/5 font-portal-body-md text-portal-body-md';
    return this.activo() === pestana
      ? `${base} text-portal-primary font-bold`
      : `${base} text-gray-300 hover:text-portal-primary transition-colors`;
  }
}

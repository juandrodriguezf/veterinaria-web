import { Component, HostListener, inject, input, signal } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SesionService } from '../../service/sesion.service';

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
  private sesionService = inject(SesionService);

  /** Pestaña activa del portal (para resaltar el enlace correspondiente). */
  activo = input<'mascotas' | 'clientes' | 'tratamientos' | 'tratadas'>();

  /** Nombre del veterinario; si llega, se muestran la campana y el avatar. */
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
  clasePestana(pestana: 'mascotas' | 'clientes' | 'tratamientos' | 'tratadas') {
    return this.activo() === pestana
      ? 'text-portal-primary font-portal-body-md text-portal-body-md font-bold border-b-2 border-portal-primary pb-1'
      : 'text-gray-400 hover:text-portal-primary transition-colors font-portal-body-md text-portal-body-md';
  }

  /** Clases del enlace apilado del menú móvil. */
  clasePestanaMovil(pestana: 'mascotas' | 'clientes' | 'tratamientos' | 'tratadas') {
    const base =
      'block py-3 border-b border-white/5 font-portal-body-md text-portal-body-md';
    return this.activo() === pestana
      ? `${base} text-portal-primary font-bold`
      : `${base} text-gray-300 hover:text-portal-primary transition-colors`;
  }
}

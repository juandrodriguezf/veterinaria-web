import { Component, HostListener, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar-landing',
  imports: [RouterLink],
  templateUrl: './navbar-landing.component.html',
  styleUrl: './navbar-landing.component.scss',
})
export class NavbarLandingComponent {
  desplazado = false;

  readonly enlaces = [
    { texto: 'Servicios', fragmento: 'servicios', activo: true },
    { texto: 'Equipo', fragmento: 'equipo', activo: false },
    { texto: 'Timeline', fragmento: 'timeline', activo: false },
    { texto: 'Ubicación', fragmento: 'ubicacion', activo: false },
  ];

  /** Controla la apertura del menú móvil. */
  menuAbierto = signal(false);

  @HostListener('window:scroll')
  alDesplazar(): void {
    this.desplazado = window.scrollY > 50;
  }

  /** Abre o cierra el menú móvil. */
  alternarMenu(): void {
    this.menuAbierto.update((abierto) => !abierto);
  }

  /** Cierra el menú móvil al navegar o al pulsar Escape. */
  cerrarMenu(): void {
    this.menuAbierto.set(false);
  }

  @HostListener('document:keydown.escape')
  cerrarConEscape(): void {
    this.cerrarMenu();
  }

  /** Clases del enlace apilado del menú móvil. */
  claseEnlaceMovil(activo: boolean): string {
    const base =
      'block py-3 border-b border-white/10 font-label-sm text-label-sm uppercase tracking-widest';
    return activo
      ? `${base} text-primary font-bold`
      : `${base} text-on-surface-variant hover:text-primary transition-colors`;
  }
}

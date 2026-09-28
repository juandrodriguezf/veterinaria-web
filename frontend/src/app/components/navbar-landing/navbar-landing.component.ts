import { Component, HostListener } from '@angular/core';
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
    { texto: 'Experiencia', fragmento: 'experiencia', activo: false },
    { texto: 'Servicios', fragmento: 'servicios', activo: true },
    { texto: 'Timeline', fragmento: 'timeline', activo: false },
    { texto: 'Contacto', fragmento: 'contacto', activo: false },
  ];

  @HostListener('window:scroll')
  alDesplazar(): void {
    this.desplazado = window.scrollY > 50;
  }
}

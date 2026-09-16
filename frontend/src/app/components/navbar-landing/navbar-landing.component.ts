import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-navbar-landing',
  imports: [RouterLink],
  templateUrl: './navbar-landing.component.html',
})
export class NavbarLandingComponent {
  readonly enlaces = [
    { texto: 'Experiencia', fragmento: 'experiencia', activo: false },
    { texto: 'Servicios', fragmento: 'servicios', activo: true },
    { texto: 'Timeline', fragmento: 'timeline', activo: false },
    { texto: 'Contacto', fragmento: 'contacto', activo: false },
  ];
}

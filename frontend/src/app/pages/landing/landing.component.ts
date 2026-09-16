import { Component } from '@angular/core';
import { RouterLink } from '@angular/router';
import { NavbarLandingComponent } from '../../components/navbar-landing/navbar-landing.component';
import { FooterLandingComponent } from '../../components/footer-landing/footer-landing.component';

@Component({
  selector: 'app-landing',
  imports: [RouterLink, NavbarLandingComponent, FooterLandingComponent],
  templateUrl: './landing.component.html',
  host: {
    class:
      'block overflow-x-hidden font-body-md text-body-md selection:bg-mint-accent selection:text-deep-carbon',
  },
})
export class LandingComponent {
  readonly equipo = [
    {
      nombre: 'Dra. Laura Gómez',
      rol: 'Medicina Veterinaria',
      posicion: 'object-center',
      imagen:
        'https://static.wixstatic.com/media/18bd0e_362fbe37c0fa49b9ac549267aef40bc3~mv2.jpg/v1/fill/w_744,h_950,fp_0.64_0.38,q_85,usm_0.66_1.00_0.01,enc_avif,quality_auto/IMG_2808.jpg',
    },
    {
      nombre: 'Dr. Carlos Ruiz',
      rol: 'Veterinario Cirujano',
      posicion: 'object-center',
      imagen:
        'https://img.magnific.com/psd-premium/veterinario-sonriente-sosteniendo-perro-pequeno_1269612-43094.jpg',
    },
    {
      nombre: 'Dra. Felipe Torres',
      rol: 'Cardiología veterinaria',
      posicion: 'object-[center_30%]',
      imagen: 'https://thumbs.dreamstime.com/b/veterinario-y-perro-9461018.jpg',
    },
    {
      nombre: 'Dr. Jorge Méndez',
      rol: 'Dermatología veterinaria',
      posicion: 'object-[center_30%]',
      imagen:
        'https://st2.depositphotos.com/3662505/6206/i/450/depositphotos_62061453-stock-photo-veterinary.jpg',
    },
    {
      nombre: 'Dra. Sofía Herrera',
      rol: 'Nutrición veterinaria',
      posicion: 'object-[center_30%]',
      imagen: 'https://img.magnific.com/fotos-premium/medico-veterinario-clinica-veterinaria_1368-127913.jpg',
    },
  ];
}

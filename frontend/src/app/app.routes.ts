import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { MascotasListadoComponent } from './pages/mascotas-listado/mascotas-listado.component';
import { MascotasFormularioComponent } from './pages/mascotas-formulario/mascotas-formulario.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'mascotas',
    component: MascotasListadoComponent,
  },
  {
    path: 'mascotas/new',
    component: MascotasFormularioComponent,
  },
  {
    path: 'mascotas/update/:id',
    component: MascotasFormularioComponent,
  },
];

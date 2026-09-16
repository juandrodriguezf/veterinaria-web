import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { MascotaListadoComponent } from './pages/mascotas-listado/mascota-listado.component';
import { MascotaFormularioComponent } from './pages/mascotas-formulario/mascota-formulario.component';

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
  },
  {
    path: 'mascotas',
    component: MascotaListadoComponent,
  },
  {
    path: 'mascotas/nueva',
    component: MascotaFormularioComponent,
  },
  {
    path: 'mascotas/editar/:id',
    component: MascotaFormularioComponent,
  },
];

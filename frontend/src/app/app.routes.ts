import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { MascotasListadoComponent } from './pages/mascotas-listado/mascotas-listado.component';
import { MascotasFichaComponent } from './pages/mascotas-ficha/mascotas-ficha.component';
import { MascotasFormularioComponent } from './pages/mascotas-formulario/mascotas-formulario.component';
import { ClientesListadoComponent } from './pages/clientes-listado/clientes-listado.component';
import { ClientesFormularioComponent } from './pages/clientes-formulario/clientes-formulario.component';

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
  {
    path: 'mascotas/:id',
    component: MascotasFichaComponent,
  },
  {
    path: 'clientes',
    component: ClientesListadoComponent,
  },
  {
    path: 'clientes/new',
    component: ClientesFormularioComponent,
  },
  {
    path: 'clientes/update/:id',
    component: ClientesFormularioComponent,
  },
];

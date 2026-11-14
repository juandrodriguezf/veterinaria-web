import { Routes } from '@angular/router';
import { LandingComponent } from './pages/landing/landing.component';
import { LoginComponent } from './pages/login/login.component';
import { MascotasListadoComponent } from './pages/mascotas-listado/mascotas-listado.component';
import { MascotasFichaComponent } from './pages/mascotas-ficha/mascotas-ficha.component';
import { MascotasFormularioComponent } from './pages/mascotas-formulario/mascotas-formulario.component';
import { ClientesListadoComponent } from './pages/clientes-listado/clientes-listado.component';
import { ClientesFormularioComponent } from './pages/clientes-formulario/clientes-formulario.component';
import { ClienteMascotasComponent } from './pages/cliente-mascotas/cliente-mascotas.component';
import { ClienteDetalleComponent } from './pages/cliente-detalle/cliente-detalle.component';
import { TratamientoAsignarComponent } from './pages/tratamiento-asignar/tratamiento-asignar.component';
import { VeterinarioTratadasComponent } from './pages/veterinario-tratadas/veterinario-tratadas.component';
import { AdminDashboardComponent } from './pages/admin-dashboard/admin-dashboard.component';
import { AdminVeterinariosListadoComponent } from './pages/admin-veterinarios-listado/admin-veterinarios-listado.component';
import { AdminVeterinariosFormularioComponent } from './pages/admin-veterinarios-formulario/admin-veterinarios-formulario.component';
import { ErrorComponent } from './pages/error/error.component';
import { authGuard, homeRedirectGuard, rolesGuard } from './service/auth.guard';

/** Acceso del portal del administrador. */
const soloAdministrador = [authGuard, rolesGuard(['ADMINISTRADOR'])];

/** Acceso del portal del veterinario. */
const soloVeterinario = [authGuard, rolesGuard(['VETERINARIO'])];

/** Acceso del portal del cliente. */
const soloCliente = [authGuard, rolesGuard(['CLIENTE'])];

export const routes: Routes = [
  {
    path: '',
    component: LandingComponent,
    canActivate: [homeRedirectGuard],
  },
  {
    path: 'login',
    component: LoginComponent,
  },
  {
    path: 'admin/dashboard',
    component: AdminDashboardComponent,
    canActivate: soloAdministrador,
  },
  {
    path: 'admin/veterinarios',
    component: AdminVeterinariosListadoComponent,
    canActivate: soloAdministrador,
  },
  {
    path: 'admin/veterinarios/new',
    component: AdminVeterinariosFormularioComponent,
    canActivate: soloAdministrador,
  },
  {
    path: 'admin/veterinarios/update/:id',
    component: AdminVeterinariosFormularioComponent,
    canActivate: soloAdministrador,
  },
  {
    path: 'mascotas',
    component: MascotasListadoComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'mascotas/new',
    component: MascotasFormularioComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'mascotas/update/:id',
    component: MascotasFormularioComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'mascotas/:id',
    component: MascotasFichaComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'clientes',
    component: ClientesListadoComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'clientes/new',
    component: ClientesFormularioComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'clientes/update/:id',
    component: ClientesFormularioComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'cliente/mascotas',
    component: ClienteMascotasComponent,
    canActivate: soloCliente,
  },
  {
    path: 'cliente/mascotas/detalle/:id',
    component: ClienteDetalleComponent,
    canActivate: soloCliente,
  },
  {
    path: 'tratamiento/asignar',
    component: TratamientoAsignarComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'veterinario/tratadas',
    component: VeterinarioTratadasComponent,
    canActivate: soloVeterinario,
  },
  {
    path: 'error',
    component: ErrorComponent,
  },
  {
    path: '**',
    component: ErrorComponent,
  },
];

import { Component, OnInit, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { ClienteHeaderComponent } from '../../components/cliente-header/cliente-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { ClienteMascotaTablaComponent } from './components/cliente-mascota-tabla/cliente-mascota-tabla.component';

/**
 * Página "Mis mascotas" del portal del cliente.
 * El backend identifica al dueño por el JWT: no hay id en la URL, así que
 * el cliente no puede cambiar la ruta para ver las mascotas de otro.
 */
@Component({
  selector: 'app-cliente-mascotas',
  imports: [
    FormsModule,
    RouterLink,
    FooterPortalComponent,
    ClienteHeaderComponent,
    ClienteMascotaTablaComponent,
  ],
  templateUrl: './cliente-mascotas.component.html',
  styleUrl: './cliente-mascotas.component.scss',
})
export class ClienteMascotasComponent implements OnInit {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  private router = inject(Router);

  dueno?: Dueno;

  mascotas: Mascota[] = [];

  nombre = '';

  /** true cuando no se pudo cargar el perfil del cliente. */
  errorCarga = false;

  /** true cuando falla la consulta de mascotas. */
  errorMascotas = false;

  /** Mensaje del backend para el estado de error. */
  mensajeError = '';

  /** true mientras se resuelve el perfil o su listado de mascotas. */
  cargando = true;

  ngOnInit() {
    // Primero se valida el perfil: el backend lo resuelve desde el token.
    this.duenoService.obtenerMiPerfil().subscribe({
      next: (data) => {
        this.dueno = data;
        this.cargar();
      },
      error: (error) => {
        // Un cliente inactivo (409) vuelve al login; el 401 lo maneja el interceptor.
        if (error?.status === 409) {
          this.router.navigate(['/login']);
          return;
        }
        this.errorCarga = true;
        this.cargando = false;
        this.mensajeError = error?.error?.mensaje ?? 'No encontramos tu perfil de cliente.';
      },
    });
  }

  /** Carga las mascotas propias (todas o filtradas por nombre). */
  cargar() {
    this.cargando = true;
    this.errorMascotas = false;
    this.mensajeError = '';
    this.mascotaService.listarMias(this.nombre).subscribe({
      next: (data) => {
        this.mascotas = data;
        this.cargando = false;
      },
      error: (error) => {
        this.cargando = false;
        this.errorMascotas = true;
        this.mensajeError = error?.error?.mensaje ?? 'No se pudieron cargar las mascotas. Intenta de nuevo.';
      },
    });
  }

  buscar() {
    if (!this.errorCarga) {
      this.cargar();
    }
  }

  limpiar() {
    if (!this.errorCarga) {
      this.nombre = '';
      this.cargar();
    }
  }
}

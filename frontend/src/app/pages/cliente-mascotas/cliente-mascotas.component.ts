import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { ClienteHeaderComponent } from '../../components/cliente-header/cliente-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-cliente-mascotas',
  imports: [FormsModule, RouterLink, FooterPortalComponent, ClienteHeaderComponent],
  templateUrl: './cliente-mascotas.component.html',
  styleUrl: './cliente-mascotas.component.scss',
})
export class ClienteMascotasComponent {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  private activatedRoute = inject(ActivatedRoute);

  private router = inject(Router);

  idUsuario = 0;

  dueno?: Dueno;

  mascotas: Mascota[] = [];

  nombre = '';

  /** true cuando el cliente de la URL no existe o el id no es válido. */
  errorCarga = false;

  /** true cuando falla la consulta de mascotas de un cliente válido. */
  errorMascotas = false;

  /** Mensaje del backend para el estado de error. */
  mensajeError = '';

  /** true mientras se resuelve el cliente o su listado de mascotas. */
  cargando = true;

  ngOnInit() {
    // El id del cliente viaja en la ruta; el login lo puso ahí al autenticar.
    this.idUsuario = Number(this.activatedRoute.snapshot.params['idUsuario']);
    // Un id no numérico o no positivo no llega a la API: se informa de una vez.
    if (!Number.isInteger(this.idUsuario) || this.idUsuario <= 0) {
      this.errorCarga = true;
      this.cargando = false;
      this.mensajeError = 'El identificador del cliente no es válido.';
      return;
    }
    // Primero se valida el dueño: sin él no tiene sentido consultar mascotas
    // ni mostrar el estado vacío como si fuera un cliente válido.
    this.duenoService.obtenerActivo(this.idUsuario).subscribe({
      next: (data) => {
        this.dueno = data;
        this.cargar();
      },
      error: (error) => {
        // Un cliente inactivo (409) o un id invalido (400) no entra al portal.
        if (error?.status === 409 || error?.status === 400) {
          this.router.navigate(['/login']);
          return;
        }
        this.errorCarga = true;
        this.cargando = false;
        this.mensajeError = error?.error?.mensaje ?? 'No encontramos el cliente solicitado.';
      },
    });
  }

  /** Carga las mascotas del cliente (todas o filtradas por nombre). */
  cargar() {
    this.cargando = true;
    this.errorMascotas = false;
    this.mensajeError = '';
    this.mascotaService.listarPorDueno(this.idUsuario, this.nombre).subscribe({
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

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }

  descripcionEdad(mascota: Mascota) {
    if (mascota.edad === undefined) {
      return 'No registrada';
    }
    if (mascota.edad < 1) {
      const meses = Math.max(1, Math.round(mascota.edad * 12));
      return meses + (meses === 1 ? ' mes' : ' meses');
    }
    const anios = Math.floor(mascota.edad);
    return anios + (anios === 1 ? ' año' : ' años');
  }
}

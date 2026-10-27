import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { mergeMap } from 'rxjs';
import { ClienteHeaderComponent } from '../../components/cliente-header/cliente-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { DetalleHistorialComponent } from './components/detalle-historial/detalle-historial.component';
import { DetalleMascotaComponent } from './components/detalle-mascota/detalle-mascota.component';

/**
 * Detalle de una mascota del cliente autenticado.
 * Valida el cliente activo, la pertenencia de la mascota y carga su historial.
 */
@Component({
  selector: 'app-cliente-detalle',
  imports: [RouterLink, FooterPortalComponent, ClienteHeaderComponent, DetalleMascotaComponent, DetalleHistorialComponent],
  templateUrl: './cliente-detalle.component.html',
  styleUrl: './cliente-detalle.component.scss',
})
export class ClienteDetalleComponent {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  private tratamientoService = inject(TratamientoService);

  private activatedRoute = inject(ActivatedRoute);

  private router = inject(Router);

  idUsuario = 0;

  dueno?: Dueno;

  mascota?: Mascota;

  mensajeError = '';

  ngOnInit() {
    this.idUsuario = Number(this.activatedRoute.snapshot.params['idUsuario']);
    const id = Number(this.activatedRoute.snapshot.params['id']);
    // Primero se valida que el cliente siga activo; solo entonces se
    // consultan la mascota propia y su historial de tratamientos.
    this.duenoService
      .obtenerActivo(this.idUsuario)
      .pipe(
        mergeMap((dueno) => {
          this.dueno = dueno;
          return this.mascotaService.obtenerPropia(id, this.idUsuario);
        }),
        mergeMap((mascota) => {
          this.mascota = mascota;
          this.mascota.tratamientos = [];
          return this.tratamientoService.listarTratamientosPorMascota(mascota.id);
        }),
      )
      .subscribe({
        next: (tratamientos) => {
          if (this.mascota) {
            this.mascota.tratamientos = tratamientos;
          }
        },
        error: (error) => {
          // Un cliente inactivo (409) o un id invalido (400) vuelve al login.
          if (error?.status === 409 || error?.status === 400) {
            this.router.navigate(['/login']);
            return;
          }
          // Id inexistente o de otro dueño: el panel informativo.
          this.mensajeError = 'No encontramos ninguna mascota registrada a tu nombre.';
        },
      });
  }

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }
}

import { Component, OnInit, inject } from '@angular/core';
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
 * La ruta solo lleva el id de la mascota: el dueño sale del JWT y el
 * backend valida que la mascota le pertenezca antes de devolverla.
 */
@Component({
  selector: 'app-cliente-detalle',
  imports: [RouterLink, FooterPortalComponent, ClienteHeaderComponent, DetalleMascotaComponent, DetalleHistorialComponent],
  templateUrl: './cliente-detalle.component.html',
  styleUrl: './cliente-detalle.component.scss',
})
export class ClienteDetalleComponent implements OnInit {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  private tratamientoService = inject(TratamientoService);

  private activatedRoute = inject(ActivatedRoute);

  private router = inject(Router);

  dueno?: Dueno;

  mascota?: Mascota;

  mensajeError = '';

  ngOnInit() {
    const id = Number(this.activatedRoute.snapshot.params['id']);
    // Primero se valida el perfil; solo entonces se consultan la mascota
    // propia y su historial de tratamientos.
    this.duenoService
      .obtenerMiPerfil()
      .pipe(
        mergeMap((dueno) => {
          this.dueno = dueno;
          return this.mascotaService.obtenerMia(id);
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
          // Un cliente inactivo (409) vuelve al login; el 401 lo maneja el interceptor.
          if (error?.status === 409) {
            this.router.navigate(['/login']);
            return;
          }
          // 403/404: la mascota no existe o no pertenece al cliente.
          this.mensajeError = 'No encontramos ninguna mascota registrada a tu nombre.';
        },
      });
  }

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }
}

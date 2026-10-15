import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
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

  idUsuario = 0;

  dueno?: Dueno;

  mascota?: Mascota;

  mensajeError = '';

  ngOnInit() {
    this.idUsuario = Number(this.activatedRoute.snapshot.params['idUsuario']);
    const id = Number(this.activatedRoute.snapshot.params['id']);
    this.duenoService.obtenerPorId(this.idUsuario).subscribe((data) => (this.dueno = data));
    // Consultas encadenadas con mergeMap (patrón del video): primero la
    // mascota propia (el backend valida la pertenencia) y, con esa
    // respuesta, su historial de tratamientos.
    this.mascotaService
      .obtenerPropia(id, this.idUsuario)
      .pipe(
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
        error: () => {
          // Id inexistente, inválido o de otro dueño: el panel informativo.
          this.mensajeError = 'No encontramos ninguna mascota registrada a tu nombre.';
        },
      });
  }

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }
}

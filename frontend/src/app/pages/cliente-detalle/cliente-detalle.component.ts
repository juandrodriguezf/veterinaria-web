import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { mergeMap } from 'rxjs';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { TratamientoService } from '../../service/tratamiento.service';

@Component({
  selector: 'app-cliente-detalle',
  imports: [RouterLink, FooterPortalComponent],
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

  descripcionPeso(mascota: Mascota) {
    return mascota.pesoKg === undefined ? 'No registrado' : mascota.pesoKg.toFixed(1) + ' kg';
  }

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }
}

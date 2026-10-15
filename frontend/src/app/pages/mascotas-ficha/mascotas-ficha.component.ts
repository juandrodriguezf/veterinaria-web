import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { mergeMap } from 'rxjs';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { TratamientoService } from '../../service/tratamiento.service';

@Component({
  selector: 'app-mascotas-ficha',
  imports: [FooterPortalComponent, PortalHeaderComponent, RouterLink],
  templateUrl: './mascotas-ficha.component.html',
  styleUrl: './mascotas-ficha.component.scss',
})
export class MascotasFichaComponent {
  private mascotaService = inject(MascotaService);

  private tratamientoService = inject(TratamientoService);

  private activatedRoute = inject(ActivatedRoute);

  nombreVeterinario: string = 'Carlos Gutiérrez';

  mascota?: Mascota;

  mensajeError: string = '';

  ngOnInit() {
    const id = Number(this.activatedRoute.snapshot.params['id']);
    // Consultas encadenadas con mergeMap (patrón del video): primero la
    // mascota y, con esa respuesta, su historial de tratamientos. Así se
    // evita anidar un subscribe dentro de otro.
    this.mascotaService
      .obtenerMascotaPorId(id)
      .pipe(
        mergeMap((mascota) => {
          this.mascota = mascota;
          // La colección de tratamientos va con @JsonIgnore: se pide aparte.
          this.mascota.tratamientos = [];
          return this.tratamientoService.listarTratamientosPorMascota(mascota.id);
        }),
      )
      .subscribe((tratamientos) => {
        if (this.mascota) {
          this.mascota.tratamientos = tratamientos;
        }
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

  descripcionSexo(mascota: Mascota) {
    return mascota.sexo ? mascota.sexo : 'No registrado';
  }

  alternarEstado() {
    if (this.mascota) {
      this.mascotaService.alternarEstado(this.mascota.id).subscribe((estado) => {
        if (this.mascota) {
          this.mascota.estado = estado;
        }
      });
    }
  }
}

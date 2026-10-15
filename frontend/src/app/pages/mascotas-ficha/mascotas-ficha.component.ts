import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { mergeMap } from 'rxjs';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { FichaDatosComponent } from './components/ficha-datos/ficha-datos.component';
import { FichaEncabezadoComponent } from './components/ficha-encabezado/ficha-encabezado.component';
import { FichaHistorialComponent } from './components/ficha-historial/ficha-historial.component';
import { FichaPropietarioComponent } from './components/ficha-propietario/ficha-propietario.component';

@Component({
  selector: 'app-mascotas-ficha',
  imports: [
    RouterLink,
    FooterPortalComponent,
    PortalHeaderComponent,
    FichaEncabezadoComponent,
    FichaDatosComponent,
    FichaPropietarioComponent,
    FichaHistorialComponent,
  ],
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

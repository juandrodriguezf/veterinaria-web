import { Component, inject } from '@angular/core';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { mergeMap, forkJoin, of } from 'rxjs';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { SesionService } from '../../service/sesion.service';
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

  private duenoService = inject(DuenoService);

  private activatedRoute = inject(ActivatedRoute);

  private sesionService = inject(SesionService);

  private router = inject(Router);

  /** Nombre del veterinario autenticado, para la cabecera del portal. */
  get nombreVeterinario(): string {
    return this.sesionService.nombre();
  }

  mascota?: Mascota;

  mensajeError: string = '';

  ngOnInit() {
    if (!this.sesionService.esVeterinario()) {
      this.router.navigate(['/login']);
      return;
    }
    const id = Number(this.activatedRoute.snapshot.params['id']);
    // Consultas encadenadas (patrón del video): primero la mascota y, con
    // su id, el dueño (que ya no viaja anidado) y el historial de
    // tratamientos en paralelo. Así se evita anidar un subscribe dentro de
    // otro.
    this.mascotaService
      .obtenerMascotaPorId(id)
      .pipe(
        mergeMap((mascota) => {
          this.mascota = mascota;
          this.mascota.tratamientos = [];
          const dueno$ = mascota.duenoId
            ? this.duenoService.obtenerPorId(mascota.duenoId)
            : of(undefined);
          return forkJoin({
            dueno: dueno$,
            tratamientos: this.tratamientoService.listarTratamientosPorMascota(mascota.id),
          });
        }),
      )
      .subscribe(({ dueno, tratamientos }) => {
        if (this.mascota) {
          this.mascota.dueno = dueno;
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

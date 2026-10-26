import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
import { SesionService } from '../../service/sesion.service';
import { MascotaTablaComponent } from './components/mascota-tabla/mascota-tabla.component';
import { PageTitleComponent } from './components/page-title/page-title.component';

@Component({
  selector: 'app-mascotas-listado',
  imports: [
    FooterPortalComponent,
    FormsModule,
    MascotaTablaComponent,
    PageTitleComponent,
    PortalHeaderComponent,
    RouterLink,
  ],
  templateUrl: './mascotas-listado.component.html',
  styleUrl: './mascotas-listado.component.scss',
})
export class MascotasListadoComponent {
  private mascotaService = inject(MascotaService);

  private sesionService = inject(SesionService);

  private router = inject(Router);

  /** Nombre del veterinario autenticado, para la cabecera del portal. */
  get nombreVeterinario(): string {
    return this.sesionService.nombre();
  }

  mascotas: Mascota[] = [];

  nombre: string = '';

  /** Total de mascotas sin filtro, para no confundirlo con los resultados. */
  totalMascotas = 0;

  ngOnInit() {
    if (!this.sesionService.esVeterinario()) {
      this.router.navigate(['/login']);
      return;
    }
    this.cargar();
  }

  /** Carga el listado completo o el filtrado por nombre (según el término). */
  cargar() {
    const filtro = this.nombre.trim();
    const peticion = filtro
      ? this.mascotaService.buscarPorNombre(this.nombre)
      : this.mascotaService.listarMascotas();
    peticion.subscribe((data) => {
      this.mascotas = data;
      if (!filtro) {
        this.totalMascotas = data.length;
      }
    });
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.cargar();
  }

  alternarEstado(mascota: Mascota) {
    if (
      mascota.estado === 'Activo' &&
      !window.confirm(
        `¿Desactivar a ${mascota.nombre}? No podrá recibir tratamientos hasta que la actives de nuevo.`,
      )
    ) {
      return;
    }
    this.mascotaService.alternarEstado(mascota.id).subscribe(() => this.cargar());
  }
}

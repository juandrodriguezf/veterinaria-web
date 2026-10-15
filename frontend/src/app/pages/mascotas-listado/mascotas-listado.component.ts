import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';
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

  nombreVeterinario: string = 'Carlos Gutiérrez';

  mascotas: Mascota[] = [];

  nombre: string = '';

  ngOnInit() {
    this.cargar();
  }

  /** Carga el listado completo o el filtrado por nombre (según el término). */
  cargar() {
    const peticion = this.nombre.trim()
      ? this.mascotaService.buscarPorNombre(this.nombre)
      : this.mascotaService.listarMascotas();
    peticion.subscribe((data) => (this.mascotas = data));
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.cargar();
  }

  alternarEstado(mascota: Mascota) {
    this.mascotaService.alternarEstado(mascota.id).subscribe(() => this.cargar());
  }
}

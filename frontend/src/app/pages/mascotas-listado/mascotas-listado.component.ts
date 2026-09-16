import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
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
    this.mascotas = this.mascotaService.listarMascotas();
  }

  buscar() {
    this.mascotas = this.mascotaService.buscarPorNombre(this.nombre);
  }

  limpiar() {
    this.nombre = '';
    this.mascotas = this.mascotaService.listarMascotas();
  }

  alternarEstado(mascota: Mascota) {
    this.mascotaService.alternarEstado(mascota.id);
  }
}

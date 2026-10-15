import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';
import { ClienteTablaComponent } from './components/cliente-tabla/cliente-tabla.component';
import { ClientesPageTitleComponent } from './components/page-title/page-title.component';

@Component({
  selector: 'app-clientes-listado',
  imports: [
    ClienteTablaComponent,
    ClientesPageTitleComponent,
    FooterPortalComponent,
    FormsModule,
    RouterLink,
  ],
  templateUrl: './clientes-listado.component.html',
  styleUrl: './clientes-listado.component.scss',
})
export class ClientesListadoComponent {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  nombreVeterinario: string = 'Carlos Gutiérrez';

  duenos: Dueno[] = [];

  nombre: string = '';

  /**
   * Caché de mascotas: la colección del dueño va con @JsonIgnore en el
   * backend, así que el contador de mascotas de la tabla se arma aquí
   * cruzando este listado (consulta aparte) con cada dueño.
   */
  private mascotas: Mascota[] = [];

  ngOnInit() {
    this.mascotaService.listarMascotas().subscribe((mascotas) => {
      this.mascotas = mascotas;
      this.cargar();
    });
  }

  cargar() {
    if (this.nombre.trim()) {
      this.duenoService.buscarPorNombre(this.nombre).subscribe((duenos) => this.mostrar(duenos));
    } else {
      this.duenoService.listarDuenos().subscribe((duenos) => this.mostrar(duenos));
    }
  }

  /** Adjunta a cada dueño sus mascotas (desde la caché) para el contador. */
  private mostrar(duenos: Dueno[]) {
    duenos.forEach((dueno) => {
      dueno.mascotas = this.mascotas.filter((mascota) => mascota.dueno?.id === dueno.id);
    });
    this.duenos = duenos;
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.cargar();
  }

  alternarEstado(dueno: Dueno) {
    this.duenoService.alternarEstado(dueno.id).subscribe(() => this.cargar());
  }

  eliminar(dueno: Dueno) {
    const total = dueno.mascotas.length;
    const mensaje =
      total === 0
        ? `¿Eliminar a ${dueno.nombre}?`
        : `¿Eliminar a ${dueno.nombre}? También se eliminará${total === 1 ? '' : 'n'} ${
            total === 1 ? 'su mascota' : `sus ${total} mascotas`
          }.`;
    if (!window.confirm(mensaje)) {
      return;
    }
    // Se recarga al confirmar el borrado, para reflejar la cascada.
    this.duenoService.eliminar(dueno.id).subscribe(() => this.cargar());
  }
}

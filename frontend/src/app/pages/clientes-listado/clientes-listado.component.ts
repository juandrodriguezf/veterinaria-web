import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { ClienteTablaComponent } from './components/cliente-tabla/cliente-tabla.component';
import { ClientesPageTitleComponent } from './components/page-title/page-title.component';

@Component({
  selector: 'app-clientes-listado',
  imports: [
    ClienteTablaComponent,
    ClientesPageTitleComponent,
    FooterPortalComponent,
    FormsModule,
    PortalHeaderComponent,
    RouterLink,
  ],
  templateUrl: './clientes-listado.component.html',
  styleUrl: './clientes-listado.component.scss',
})
export class ClientesListadoComponent {
  private duenoService = inject(DuenoService);

  nombreVeterinario: string = 'Carlos Gutiérrez';

  duenos: Dueno[] = [];

  nombre: string = '';

  /** Total de clientes sin filtro, para no confundirlo con los resultados. */
  totalDuenos = 0;

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    const filtro = this.nombre.trim();
    const peticion = filtro
      ? this.duenoService.buscarPorNombre(this.nombre)
      : this.duenoService.listarDuenos();
    peticion.subscribe((duenos) => {
      this.duenos = duenos;
      if (!filtro) {
        this.totalDuenos = duenos.length;
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

  alternarEstado(dueno: Dueno) {
    if (
      dueno.estado === 'Activo' &&
      !window.confirm(
        `¿Desactivar a ${dueno.nombre}? No podrá acceder al portal hasta que lo actives de nuevo.`,
      )
    ) {
      return;
    }
    this.duenoService.alternarEstado(dueno.id).subscribe(() => this.cargar());
  }

  eliminar(dueno: Dueno) {
    const total = dueno.mascotaCount ?? 0;
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

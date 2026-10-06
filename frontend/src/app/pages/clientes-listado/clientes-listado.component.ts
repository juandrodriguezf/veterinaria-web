import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
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

  ngOnInit() {
    this.cargar();
  }

  cargar() {
    this.duenos = this.nombre.trim()
      ? this.duenoService.buscarPorNombre(this.nombre)
      : this.duenoService.listarDuenos();
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.cargar();
  }

  alternarEstado(dueno: Dueno) {
    this.duenoService.alternarEstado(dueno.id);
    this.cargar();
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
    this.mascotaService.eliminarPorDueno(dueno.id);
    this.duenoService.eliminar(dueno.id);
    this.cargar();
  }
}
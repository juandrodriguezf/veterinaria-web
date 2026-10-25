import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { AdminHeaderComponent } from '../../components/admin-header/admin-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Veterinario } from '../../models/veterinario.model';
import { VeterinarioService } from '../../service/veterinario.service';
import { VeterinarioTablaComponent } from './components/veterinario-tabla/veterinario-tabla.component';

/**
 * Página de listado de veterinarios del portal del administrador. Permite
 * buscar por cédula o nombre, activar/desactivar y eliminar; el alta y la
 * edición se resuelven en el formulario.
 */
@Component({
  selector: 'app-admin-veterinarios-listado',
  imports: [
    AdminHeaderComponent,
    FooterPortalComponent,
    FormsModule,
    RouterLink,
    VeterinarioTablaComponent,
  ],
  templateUrl: './admin-veterinarios-listado.component.html',
  styleUrl: './admin-veterinarios-listado.component.scss',
})
export class AdminVeterinariosListadoComponent {
  private veterinarioService = inject(VeterinarioService);

  /** Nombre del administrador (sin sesión, igual que el portal veterinario). */
  nombreAdministrador: string = 'Dirección Vetopia';

  /** Veterinarios del listado (todos o los que coinciden con el término). */
  veterinarios: Veterinario[] = [];

  /** Término de la barra de búsqueda (cédula o nombre). */
  termino: string = '';

  ngOnInit() {
    this.cargar();
  }

  /** Carga el listado completo o el filtrado, según el término. */
  cargar() {
    const peticion = this.termino.trim()
      ? this.veterinarioService.buscar(this.termino)
      : this.veterinarioService.listarVeterinarios();
    peticion.subscribe((veterinarios) => (this.veterinarios = veterinarios));
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.termino = '';
    this.cargar();
  }

  /** Alterna Activo/Inactivo y recarga para reflejar el nuevo estado. */
  alternarEstado(veterinario: Veterinario) {
    this.veterinarioService.alternarEstado(veterinario.id).subscribe(() => this.cargar());
  }

  /** Elimina al veterinario; sus tratamientos quedan sin responsable. */
  eliminar(veterinario: Veterinario) {
    const mensaje = `¿Eliminar a ${veterinario.nombre}? Sus tratamientos quedarán sin responsable, pero el historial clínico se conserva.`;
    if (!window.confirm(mensaje)) {
      return;
    }
    this.veterinarioService.eliminar(veterinario.id).subscribe(() => this.cargar());
  }
}

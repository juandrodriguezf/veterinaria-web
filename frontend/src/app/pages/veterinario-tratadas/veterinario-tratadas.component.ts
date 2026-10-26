import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { MascotaTratada } from '../../models/mascota-tratada.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { SesionService } from '../../service/sesion.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { TratadasTablaComponent } from './components/tratadas-tabla/tratadas-tabla.component';

/**
 * Página "Tratadas" del portal del veterinario. Pide los tratamientos del
 * veterinario autenticado, los agrupa por mascota y permite filtrar por
 * nombre. Si no hay una sesión de veterinario, vuelve al login.
 */
@Component({
  selector: 'app-veterinario-tratadas',
  imports: [FooterPortalComponent, FormsModule, PortalHeaderComponent, TratadasTablaComponent],
  templateUrl: './veterinario-tratadas.component.html',
  styleUrl: './veterinario-tratadas.component.scss',
})
export class VeterinarioTratadasComponent {
  private tratamientoService = inject(TratamientoService);

  private sesionService = inject(SesionService);

  private router = inject(Router);

  /** Nombre del veterinario autenticado, para la cabecera y el resumen. */
  get nombreVeterinario(): string {
    return this.sesionService.nombre();
  }

  /** Historial completo del veterinario (una fila por tratamiento). */
  tratamientos: Tratamiento[] = [];

  /** Término de la barra de búsqueda (nombre de la mascota). */
  termino: string = '';

  /** Mensaje de error si la petición falla. */
  mensajeError = '';

  ngOnInit() {
    this.cargar();
  }

  /** Pide el historial del veterinario autenticado al backend. */
  cargar() {
    this.mensajeError = '';
    const veterinarioId = this.sesionService.id();
    if (!this.sesionService.esVeterinario() || veterinarioId === null) {
      this.router.navigate(['/login']);
      return;
    }
    this.tratamientoService.listarTratamientosPorVeterinario(veterinarioId).subscribe({
      next: (tratamientos) => (this.tratamientos = tratamientos),
      error: () => {
        this.mensajeError = 'No se pudo cargar el historial de pacientes atendidos.';
      },
    });
  }

  /**
   * Agrupa los tratamientos por mascota: total de atenciones y fecha de la
   * última. Si la mascota fue eliminada, se agrupa con el nombre que trae
   * el historial (o "Mascota retirada").
   */
  mascotasTratadas(): MascotaTratada[] {
    const agrupadas = new Map<string, MascotaTratada>();
    for (const tratamiento of this.tratamientos) {
      const clave =
        tratamiento.mascotaId !== null
          ? `id-${tratamiento.mascotaId}`
          : `nombre-${tratamiento.mascotaNombre ?? 'retirada'}`;
      const existente = agrupadas.get(clave);
      if (existente) {
        existente.totalTratamientos += 1;
        if (tratamiento.fecha > existente.ultimaFecha) {
          existente.ultimaFecha = tratamiento.fecha;
        }
      } else {
        agrupadas.set(clave, {
          mascotaId: tratamiento.mascotaId,
          nombre: tratamiento.mascotaNombre ?? 'Mascota retirada',
          totalTratamientos: 1,
          ultimaFecha: tratamiento.fecha,
        });
      }
    }
    return [...agrupadas.values()].sort((a, b) => b.ultimaFecha.localeCompare(a.ultimaFecha));
  }

  /** Aplica el filtro de la barra de búsqueda sobre el nombre de la mascota. */
  mascotasFiltradas(): MascotaTratada[] {
    const texto = this.termino.trim().toLowerCase();
    const filas = this.mascotasTratadas();
    return texto ? filas.filter((fila) => fila.nombre.toLowerCase().includes(texto)) : filas;
  }

  /** Limpia la búsqueda para volver a ver todas las mascotas atendidas. */
  limpiar() {
    this.termino = '';
  }
}

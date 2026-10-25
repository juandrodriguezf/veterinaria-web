import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { MascotaTratada } from '../../models/mascota-tratada.model';
import { Tratamiento } from '../../models/tratamiento.model';
import { TratamientoService } from '../../service/tratamiento.service';
import { TratadasTablaComponent } from './components/tratadas-tabla/tratadas-tabla.component';

/**
 * Página "Tratadas" del portal del veterinario. Pide los tratamientos del
 * veterinario, los agrupa por mascota y permite filtrar por nombre.
 * Mientras no exista sesión, el portal trabaja con el veterinario 1,
 * igual que al asignar un tratamiento.
 */
@Component({
  selector: 'app-veterinario-tratadas',
  imports: [FooterPortalComponent, FormsModule, PortalHeaderComponent, TratadasTablaComponent],
  templateUrl: './veterinario-tratadas.component.html',
  styleUrl: './veterinario-tratadas.component.scss',
})
export class VeterinarioTratadasComponent {
  private tratamientoService = inject(TratamientoService);

  /** Nombre del veterinario del portal (sin sesión, igual que en las demás páginas). */
  nombreVeterinario: string = 'Carlos Gutiérrez';

  /** Veterinario responsable fijo mientras no haya sesión. */
  veterinarioId = 1;

  /** Historial completo del veterinario (una fila por tratamiento). */
  tratamientos: Tratamiento[] = [];

  /** Término de la barra de búsqueda (nombre de la mascota). */
  termino: string = '';

  /** Mensaje de error si la petición falla. */
  mensajeError = '';

  ngOnInit() {
    this.cargar();
  }

  /** Pide el historial del veterinario al backend. */
  cargar() {
    this.mensajeError = '';
    this.tratamientoService.listarTratamientosPorVeterinario(this.veterinarioId).subscribe({
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

import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Droga } from '../../models/droga.model';
import { Mascota } from '../../models/mascota.model';
import { DrogaService } from '../../service/droga.service';
import { MascotaService } from '../../service/mascota.service';
import { SesionService } from '../../service/sesion.service';
import { TratamientoService } from '../../service/tratamiento.service';
import { TratamientoFormComponent } from './components/tratamiento-form/tratamiento-form.component';
import { TratamientoModalComponent } from './components/tratamiento-modal/tratamiento-modal.component';

@Component({
  selector: 'app-tratamiento-asignar',
  imports: [FooterPortalComponent, PortalHeaderComponent, TratamientoFormComponent, TratamientoModalComponent],
  templateUrl: './tratamiento-asignar.component.html',
  styleUrl: './tratamiento-asignar.component.scss',
})
export class TratamientoAsignarComponent {
  private mascotaService = inject(MascotaService);

  private drogaService = inject(DrogaService);

  private tratamientoService = inject(TratamientoService);

  private activatedRoute = inject(ActivatedRoute);

  private sesionService = inject(SesionService);

  private router = inject(Router);

  /** Nombre del veterinario autenticado, para la cabecera y el formulario. */
  get nombreVeterinario(): string {
    return this.sesionService.nombre();
  }

  mascotas: Mascota[] = [];

  /** Inventario con unidades disponibles: sin stock no se puede asignar. */
  drogas: Droga[] = [];

  fechaHoy = new Date().toISOString().substring(0, 10);

  mensajeError = '';

  /** Droga devuelta por el backend tras descontar el inventario (modal de confirmación). */
  drogaAplicada?: Droga;

  /** Mascota tratada, para el enlace "Volver a la Ficha" del modal. */
  mascotaIdAplicada?: number;

  form = new FormGroup({
    mascotaId: new FormControl('', [Validators.required]),
    fecha: new FormControl(this.fechaHoy, [Validators.required]),
    drogaId: new FormControl('', [Validators.required]),
  });

  ngOnInit() {
    // Esta pantalla es exclusiva del veterinario: sin sesión válida se
    // vuelve al login en vez de consultar datos con un id inventado.
    if (!this.sesionService.esVeterinario()) {
      this.router.navigate(['/login']);
      return;
    }
    // Solo las mascotas activas (hospitalizadas) pueden recibir tratamiento.
    this.mascotaService.listarMascotasActivas().subscribe((data) => {
      this.mascotas = data;
      this.preseleccionarMascota();
    });
    this.cargarDrogas();
  }

  /**
   * Si se llegó desde la ficha de una mascota (query param mascotaId), la
   * deja seleccionada en el formulario. Solo aplica si la mascota está
   * activa, porque el selector únicamente ofrece mascotas hospitalizadas.
   */
  private preseleccionarMascota() {
    const mascotaId = Number(this.activatedRoute.snapshot.queryParamMap.get('mascotaId'));
    if (mascotaId && this.mascotas.some((mascota) => mascota.id === mascotaId)) {
      this.form.patchValue({ mascotaId: String(mascotaId) });
    }
  }

  /** Sin medicamentos con stock no hay nada que asignar: el formulario se bloquea. */
  get sinMedicamentos(): boolean {
    return this.drogas.length === 0;
  }

  aplicar() {
    const formValue = this.form.value;
    this.mensajeError = '';
    const veterinarioId = this.sesionService.id();
    if (!this.sesionService.esVeterinario() || veterinarioId === null) {
      this.router.navigate(['/login']);
      return;
    }
    // Comando: el backend registra el tratamiento, descuenta el stock y
    // devuelve la droga actualizada; el componente reacciona a esa respuesta.
    this.tratamientoService
      .asignar({
        fecha: formValue.fecha ?? '',
        mascota: { id: Number(formValue.mascotaId) },
        droga: { id: Number(formValue.drogaId) },
        veterinario: { id: veterinarioId },
      })
      .subscribe({
        next: (droga) => {
          this.drogaAplicada = droga;
          this.mascotaIdAplicada = Number(formValue.mascotaId);
          // Refresca el inventario mostrado en el selector.
          this.cargarDrogas();
        },
        error: (error) => {
          this.mensajeError =
            error?.error?.mensaje ??
            error?.error?.message ??
            'No se pudo aplicar el tratamiento. Verifica el stock del medicamento.';
        },
      });
  }

  cerrarConfirmacion() {
    this.drogaAplicada = undefined;
    this.mascotaIdAplicada = undefined;
    this.form.reset({ mascotaId: '', fecha: this.fechaHoy, drogaId: '' });
  }

  /** Carga el inventario dejando solo los medicamentos con unidades disponibles. */
  private cargarDrogas() {
    this.drogaService.listarDrogas().subscribe((data) => {
      this.drogas = data.filter((droga) => (droga.unidadesDisponibles ?? 0) > 0);
    });
  }
}

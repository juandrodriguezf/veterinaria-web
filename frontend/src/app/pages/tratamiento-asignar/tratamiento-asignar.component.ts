import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, Validators } from '@angular/forms';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Droga } from '../../models/droga.model';
import { Mascota } from '../../models/mascota.model';
import { DrogaService } from '../../service/droga.service';
import { MascotaService } from '../../service/mascota.service';
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

  nombreVeterinario = 'Carlos Gutiérrez';

  mascotas: Mascota[] = [];

  /** Inventario con unidades disponibles: sin stock no se puede asignar. */
  drogas: Droga[] = [];

  /** Medicamentos que coinciden con el texto del buscador. */
  drogasFiltradas: Droga[] = [];

  /** Buscador del selector de medicamentos. */
  filtroMedicamento = new FormControl('');

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
    // Solo las mascotas activas (hospitalizadas) pueden recibir tratamiento.
    this.mascotaService.listarMascotasActivas().subscribe((data) => (this.mascotas = data));
    this.cargarDrogas();
    this.filtroMedicamento.valueChanges.subscribe(() => this.aplicarFiltroMedicamento());
  }

  /** Sin medicamentos con stock no hay nada que asignar: el formulario se bloquea. */
  get sinMedicamentos(): boolean {
    return this.drogas.length === 0;
  }

  aplicar() {
    const formValue = this.form.value;
    this.mensajeError = '';
    // Comando: el backend registra el tratamiento, descuenta el stock y
    // devuelve la droga actualizada; el componente reacciona a esa respuesta.
    this.tratamientoService
      .asignar({
        fecha: formValue.fecha ?? '',
        mascota: { id: Number(formValue.mascotaId) },
        droga: { id: Number(formValue.drogaId) },
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
            error?.error?.message ?? 'No se pudo aplicar el tratamiento. Verifica el stock del medicamento.';
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
      this.aplicarFiltroMedicamento();
    });
  }

  /** Reduce el selector de medicamentos al texto del buscador (sin distinguir mayúsculas). */
  private aplicarFiltroMedicamento() {
    const texto = (this.filtroMedicamento.value ?? '').trim().toLowerCase();
    this.drogasFiltradas = texto
      ? this.drogas.filter((droga) => droga.nombre.toLowerCase().includes(texto))
      : this.drogas;
  }
}

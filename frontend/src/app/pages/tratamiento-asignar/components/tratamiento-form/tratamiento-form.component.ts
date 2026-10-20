import { Component, ElementRef, HostListener, OnDestroy, OnInit, computed, inject, input, output, signal } from '@angular/core';
import { FormGroup, ReactiveFormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';
import { Subscription } from 'rxjs';
import { Droga } from '../../../../models/droga.model';
import { Mascota } from '../../../../models/mascota.model';

/**
 * Formulario de asignación de tratamiento: responsable, mascota activa,
 * fecha y medicamento. Recibe el FormGroup de la página y avisa al
 * enviarlo. El medicamento se elige con un combobox: al escribir se
 * despliega la lista filtrada de drogas con stock; sin ninguna
 * disponible, el formulario queda bloqueado.
 */
@Component({
  selector: 'app-tratamiento-form',
  imports: [ReactiveFormsModule, RouterLink],
  templateUrl: './tratamiento-form.component.html',
})
export class TratamientoFormComponent implements OnInit, OnDestroy {
  /** Máximo de medicamentos que se muestran a la vez en el panel. */
  private static readonly MAXIMO = 50;

  private elementRef = inject(ElementRef);

  form = input.required<FormGroup>();

  mascotas = input<Mascota[]>([]);

  /** Inventario con unidades disponibles: sin stock no se puede asignar. */
  drogas = input<Droga[]>([]);

  /** Sin medicamentos con stock no se puede aplicar el tratamiento. */
  sinMedicamentos = input<boolean>(false);

  nombreVeterinario = input<string>('');

  aplicar = output<void>();

  /** Texto del buscador (control suelto, fuera del FormGroup). */
  busqueda = signal('');

  /** Si el panel de medicamentos está desplegado. */
  abierto = signal(false);

  /** Índice resaltado dentro de las coincidencias, para navegar con teclado. */
  resaltado = signal(-1);

  private suscripcion?: Subscription;

  ngOnInit() {
    // Al reiniciar el formulario (cierre del modal) se limpia el buscador.
    this.suscripcion = this.form()
      .get('drogaId')!
      .valueChanges.subscribe((valor) => {
        if (!valor) {
          this.busqueda.set('');
        }
      });
  }

  ngOnDestroy() {
    this.suscripcion?.unsubscribe();
  }

  /** Medicamentos que coinciden con el texto, limitados al máximo del panel. */
  coincidencias = computed(() => this.filtrar().slice(0, TratamientoFormComponent.MAXIMO));

  /** Total de coincidencias antes del tope, para el aviso del panel. */
  totalCoincidencias = computed(() => this.filtrar().length);

  alEnfocar() {
    this.abierto.set(true);
    this.resaltado.set(-1);
  }

  /** Filtra en vivo y descarta la selección anterior mientras se escribe. */
  alEscribir(valor: string) {
    this.busqueda.set(valor);
    this.abierto.set(true);
    this.resaltado.set(-1);
    // Sin emitir evento: el suscriptor de drogaId limpia el buscador solo
    // cuando el formulario se reinicia de verdad (cierre del modal).
    this.form().patchValue({ drogaId: '' }, { emitEvent: false });
  }

  seleccionar(droga: Droga) {
    this.abierto.set(false);
    this.resaltado.set(-1);
    this.busqueda.set(droga.nombre);
    this.form().patchValue({ drogaId: String(droga.id) });
  }

  /** Flechas mueven el resaltado, Enter selecciona y Escape cierra. */
  alTeclear(evento: KeyboardEvent) {
    const lista = this.coincidencias();
    if (evento.key === 'ArrowDown') {
      evento.preventDefault();
      if (!this.abierto()) {
        this.alEnfocar();
      }
      if (lista.length) {
        this.resaltado.set(Math.min(this.resaltado() + 1, lista.length - 1));
      }
    } else if (evento.key === 'ArrowUp') {
      evento.preventDefault();
      this.resaltado.set(Math.max(this.resaltado() - 1, 0));
    } else if (evento.key === 'Enter') {
      if (this.abierto() && this.resaltado() >= 0 && lista[this.resaltado()]) {
        evento.preventDefault();
        this.seleccionar(lista[this.resaltado()]);
      }
    } else if (evento.key === 'Escape') {
      this.abierto.set(false);
      this.resaltado.set(-1);
    }
  }

  /** Un clic fuera del campo cierra el panel. */
  @HostListener('document:click', ['$event'])
  cerrarSiEsAfuera(evento: MouseEvent) {
    if (!this.elementRef.nativeElement.contains(evento.target)) {
      this.abierto.set(false);
    }
  }

  /** Lista completa filtrada por el texto, sin distinguir mayúsculas. */
  private filtrar(): Droga[] {
    const texto = this.busqueda().trim().toLowerCase();
    return texto
      ? this.drogas().filter((droga) => droga.nombre.toLowerCase().includes(texto))
      : this.drogas();
  }
}

import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { AdminHeaderComponent } from '../../components/admin-header/admin-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { VeterinarioService } from '../../service/veterinario.service';
import { VeterinarioCamposComponent } from './components/veterinario-campos/veterinario-campos.component';

/**
 * Página de alta y edición de veterinarios del portal del administrador.
 * Usa formularios reactivos con validación y conserva la contraseña
 * actual si el campo se deja vacío en la edición.
 */
@Component({
  selector: 'app-admin-veterinarios-formulario',
  imports: [
    AdminHeaderComponent,
    FooterPortalComponent,
    ReactiveFormsModule,
    RouterLink,
    VeterinarioCamposComponent,
  ],
  templateUrl: './admin-veterinarios-formulario.component.html',
  styleUrl: './admin-veterinarios-formulario.component.scss',
})
export class AdminVeterinariosFormularioComponent {
  private veterinarioService = inject(VeterinarioService);

  private router = inject(Router);

  private activatedRoute = inject(ActivatedRoute);

  /** Nombre del administrador (sin sesión, igual que el portal veterinario). */
  nombreAdministrador: string = 'Dirección Vetopia';

  /** true cuando la ruta trae un id (edición). */
  isEdit = false;

  /** Identificador del veterinario en edición; 0 al crear. */
  id: number = 0;

  /** Mensaje del backend (por ejemplo, cédula o correo repetidos). */
  mensajeError = '';

  /** true cuando el id de la URL no corresponde a un veterinario existente. */
  errorCarga = false;

  veterinarioForm = new FormGroup({
    id: new FormControl(0),
    cedula: new FormControl('', [Validators.required]),
    nombre: new FormControl('', [Validators.required]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    especialidad: new FormControl(''),
    contrasena: new FormControl(''),
    estado: new FormControl('Activo'),
  });

  ngOnInit() {
    this.id = Number(this.activatedRoute.snapshot.params['id']) || 0;
    if (!this.id) {
      return;
    }
    this.isEdit = true;
    this.veterinarioService.obtenerPorId(this.id).subscribe({
      next: (veterinario) =>
        this.veterinarioForm.patchValue({
          id: veterinario.id,
          cedula: veterinario.cedula,
          nombre: veterinario.nombre,
          correo: veterinario.correo,
          especialidad: veterinario.especialidad ?? '',
          contrasena: '',
          estado: veterinario.estado,
        }),
      error: (error) => {
        this.errorCarga = true;
        this.mensajeError =
          error?.error?.mensaje ?? 'No encontramos el veterinario solicitado.';
      },
    });
  }

  guardar() {
    const formValue = this.veterinarioForm.value;
    this.mensajeError = '';
    // Se navega dentro del next: al volver al listado, el guardado ya está
    // confirmado y el veterinario aparece de una vez.
    this.veterinarioService
      .guardarValidada({
        id: this.isEdit ? this.id : 0,
        cedula: (formValue.cedula ?? '').trim(),
        nombre: (formValue.nombre ?? '').trim(),
        correo: (formValue.correo ?? '').trim(),
        especialidad: (formValue.especialidad ?? '').trim(),
        contrasena: formValue.contrasena ?? '',
        estado: formValue.estado ?? 'Activo',
      })
      .subscribe({
        next: () => this.router.navigate(['/admin/veterinarios']),
        error: (error) => {
          this.mensajeError =
            error?.error?.mensaje ??
            'No se pudo guardar el veterinario. Verifica que la cédula y el correo no estén registrados.';
        },
      });
  }
}

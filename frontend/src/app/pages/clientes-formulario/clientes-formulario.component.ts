import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { PortalHeaderComponent } from '../../components/portal-header/portal-header.component';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { ClienteCamposComponent } from './components/cliente-campos/cliente-campos.component';

@Component({
  selector: 'app-clientes-formulario',
  imports: [FooterPortalComponent, PortalHeaderComponent, ClienteCamposComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './clientes-formulario.component.html',
  styleUrl: './clientes-formulario.component.scss',
})
export class ClientesFormularioComponent {
  private duenoService = inject(DuenoService);

  private router = inject(Router);

  private activatedRoute = inject(ActivatedRoute);

  isEdit = false;

  id: number = 0;

  mensajeError = '';

  duenoForm = new FormGroup({
    id: new FormControl(0),
    cedula: new FormControl('', [Validators.required]),
    nombre: new FormControl('', [Validators.required]),
    correo: new FormControl('', [Validators.required, Validators.email]),
    contrasena: new FormControl(''),
    celular: new FormControl(''),
    estado: new FormControl('Activo'),
  });

  ngOnInit() {
    this.id = Number(this.activatedRoute.snapshot.params['id']) || 0;
    if (!this.id) {
      return;
    }
    this.isEdit = true;
    this.duenoService.obtenerPorId(this.id).subscribe((dueno) =>
      this.duenoForm.patchValue({
        id: dueno.id,
        cedula: dueno.cedula,
        nombre: dueno.nombre,
        correo: dueno.correo,
        contrasena: '',
        celular: dueno.celular ?? '',
        estado: dueno.estado,
      }),
    );
  }

  guardar() {
    const formValue = this.duenoForm.value;
    // Se navega dentro del next: al volver al listado, el guardado ya está
    // confirmado y el cliente aparece de una vez.
    this.duenoService
      .guardarValidada({
        id: this.isEdit ? this.id : 0,
        cedula: (formValue.cedula ?? '').trim(),
        nombre: (formValue.nombre ?? '').trim(),
        correo: (formValue.correo ?? '').trim(),
        contrasena: formValue.contrasena ?? '',
        celular: (formValue.celular ?? '').trim(),
        estado: formValue.estado ?? 'Activo',
      })
      .subscribe(() => this.router.navigate(['/clientes']));
  }
}

import { Component, inject } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-mascotas-formulario',
  imports: [FooterPortalComponent, ReactiveFormsModule, RouterLink],
  templateUrl: './mascotas-formulario.component.html',
  styleUrl: './mascotas-formulario.component.scss',
})
export class MascotasFormularioComponent {
  private mascotaService = inject(MascotaService);

  private duenoService = inject(DuenoService);

  private router = inject(Router);

  private activatedRoute = inject(ActivatedRoute);

  duenos: Dueno[] = [];

  isEdit = false;

  id: number = 0;

  mascotaForm = new FormGroup({
    id: new FormControl(0),
    nombre: new FormControl('', [Validators.required]),
    especie: new FormControl('', [Validators.required]),
    raza: new FormControl(''),
    sexo: new FormControl(''),
    edad: new FormControl<number | null>(null),
    pesoKg: new FormControl<number | null>(null),
    color: new FormControl(''),
    imagen: new FormControl(''),
    dueno: new FormControl('', [Validators.required]),
    estado: new FormControl('Activo'),
  });

  ngOnInit() {
    this.duenos = this.duenoService.listarDuenos();
    this.id = Number(this.activatedRoute.snapshot.params['id']) || 0;
    if (!this.id) {
      return;
    }
    this.isEdit = true;
    const mascota = this.mascotaService.obtenerMascotaPorId(this.id);
    if (!mascota) {
      return;
    }
    this.mascotaForm.patchValue({
      id: mascota.id,
      nombre: mascota.nombre,
      especie: mascota.especie,
      raza: mascota.raza ?? '',
      sexo: mascota.sexo ?? '',
      edad: mascota.edad ?? null,
      pesoKg: mascota.pesoKg ?? null,
      color: mascota.color ?? '',
      imagen: mascota.imagen ?? '',
      dueno: `${mascota.dueno.id}`,
      estado: mascota.estado,
    });
  }

  guardar() {
    const formValue = this.mascotaForm.value;
    const dueno = this.duenos.find((dueno) => dueno.id === Number(formValue.dueno));
    if (!dueno) {
      return;
    }
    this.mascotaService.guardarValidada({
      id: this.isEdit ? this.id : 0,
      nombre: formValue.nombre ?? '',
      especie: formValue.especie ?? '',
      raza: formValue.raza ?? '',
      sexo: formValue.sexo ?? '',
      edad: formValue.edad ?? undefined,
      pesoKg: formValue.pesoKg ?? undefined,
      color: formValue.color ?? '',
      imagen: formValue.imagen ?? '',
      dueno,
      estado: formValue.estado ?? 'Activo',
      tratamientos: this.mascotaService.obtenerMascotaPorId(this.id)?.tratamientos ?? [],
    });
    this.router.navigate(['/mascotas']);
  }
}

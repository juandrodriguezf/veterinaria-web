import { Component, inject } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { ClienteHeaderComponent } from '../../components/cliente-header/cliente-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Dueno } from '../../models/dueno.model';
import { Mascota } from '../../models/mascota.model';
import { DuenoService } from '../../service/dueno.service';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-cliente-mascotas',
  imports: [FormsModule, RouterLink, FooterPortalComponent, ClienteHeaderComponent],
  templateUrl: './cliente-mascotas.component.html',
  styleUrl: './cliente-mascotas.component.scss',
})
export class ClienteMascotasComponent {
  private duenoService = inject(DuenoService);

  private mascotaService = inject(MascotaService);

  private activatedRoute = inject(ActivatedRoute);

  idUsuario = 0;

  dueno?: Dueno;

  mascotas: Mascota[] = [];

  nombre = '';

  ngOnInit() {
    // El id del cliente viaja en la ruta; el login lo puso ahí al autenticar.
    this.idUsuario = Number(this.activatedRoute.snapshot.params['idUsuario']);
    this.duenoService.obtenerPorId(this.idUsuario).subscribe((data) => (this.dueno = data));
    this.cargar();
  }

  /** Carga las mascotas del cliente (todas o filtradas por nombre). */
  cargar() {
    this.mascotaService
      .listarPorDueno(this.idUsuario, this.nombre)
      .subscribe((data) => (this.mascotas = data));
  }

  buscar() {
    this.cargar();
  }

  limpiar() {
    this.nombre = '';
    this.cargar();
  }

  numeroPaciente(mascota: Mascota) {
    return String(mascota.id).padStart(4, '0');
  }

  descripcionEdad(mascota: Mascota) {
    if (mascota.edad === undefined) {
      return 'No registrada';
    }
    if (mascota.edad < 1) {
      const meses = Math.max(1, Math.round(mascota.edad * 12));
      return meses + (meses === 1 ? ' mes' : ' meses');
    }
    const anios = Math.floor(mascota.edad);
    return anios + (anios === 1 ? ' año' : ' años');
  }
}

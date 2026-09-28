import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { Mascota } from '../../models/mascota.model';
import { MascotaService } from '../../service/mascota.service';

@Component({
  selector: 'app-mascotas-ficha',
  imports: [FooterPortalComponent, RouterLink],
  templateUrl: './mascotas-ficha.component.html',
  styleUrl: './mascotas-ficha.component.scss',
})
export class MascotasFichaComponent {
  private mascotaService = inject(MascotaService);

  private activatedRoute = inject(ActivatedRoute);

  nombreVeterinario: string = 'Carlos Gutiérrez';

  mascota?: Mascota;

  mensajeError: string = '';

  ngOnInit() {
    const id = Number(this.activatedRoute.snapshot.params['id']);
    this.mascota = this.mascotaService.obtenerMascotaPorId(id);
    if (!this.mascota) {
      this.mensajeError = this.mensajeNoEncontrada(id);
    }
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

  descripcionPeso(mascota: Mascota) {
    return mascota.pesoKg === undefined ? 'No registrado' : mascota.pesoKg.toFixed(1) + ' kg';
  }

  descripcionSexo(mascota: Mascota) {
    return mascota.sexo ? mascota.sexo : 'No registrado';
  }

  alternarEstado() {
    if (this.mascota) {
      this.mascotaService.alternarEstado(this.mascota.id);
    }
  }

  private mensajeNoEncontrada(id: number) {
    if (!Number.isInteger(id)) {
      return 'El identificador suministrado no es válido.';
    }
    if (id <= 0) {
      return 'El identificador "' + id + '" no es válido.';
    }
    return 'No encontramos ninguna mascota registrada con el identificador "' + id + '".';
  }
}

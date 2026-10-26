import { Component, inject, input } from '@angular/core';
import { RouterLink } from '@angular/router';
import { SesionService } from '../../service/sesion.service';

/**
 * Encabezado del portal del cliente. Estaba copiado en las dos pantallas
 * del cliente (sus mascotas y el detalle); aquí queda en un solo
 * componente que recibe el dueño y la sección activa.
 */
@Component({
  selector: 'app-cliente-header',
  imports: [RouterLink],
  templateUrl: './cliente-header.component.html',
  styleUrl: './cliente-header.component.scss',
})
export class ClienteHeaderComponent {
  private sesionService = inject(SesionService);

  /** Identificador del cliente (para volver a sus mascotas). */
  idUsuario = input<number>();

  /** Nombre del cliente que se muestra junto al logo. */
  nombreCliente = input<string>();

  /** Sección activa del portal del cliente. */
  seccion = input<'mascotas'>();

  /** Elimina la sesión antes de que el enlace Cerrar Sesión navegue al login. */
  cerrarSesion() {
    this.sesionService.cerrar();
  }

  /** Clases del enlace "Mascotas" según sea o no la sección activa. */
  claseMascotas() {
    return this.seccion() === 'mascotas'
      ? 'font-bold text-portal-primary-fixed border-b-2 border-portal-primary-fixed pb-1'
      : 'text-gray-300 hover:text-portal-primary-fixed transition-colors';
  }
}

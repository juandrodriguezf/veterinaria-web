import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';

/**
 * Página de error para direcciones desconocidas. El mensaje puede llegar
 * por query param (?mensaje=...) cuando otra pantalla redirige con el texto
 * del backend; si no viene, se usa el aviso genérico de ruta no encontrada.
 */
@Component({
  selector: 'app-error',
  imports: [RouterLink, FooterPortalComponent],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss',
})
export class ErrorComponent {
  private route = inject(ActivatedRoute);

  /** Mensaje amable que se muestra bajo el título. */
  mensaje: string =
    this.route.snapshot.queryParamMap.get('mensaje') || 'La página que buscas no existe o fue movida.';
}

import { Component, inject } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';

@Component({
  selector: 'app-error',
  imports: [RouterLink, FooterPortalComponent],
  templateUrl: './error.component.html',
  styleUrl: './error.component.scss',
})
export class ErrorComponent {
  private route = inject(ActivatedRoute);

  /**
   * Mensaje amable a mostrar. Llega por query param (?mensaje=...) cuando el
   * backend tradujo una excepción; si no viene, se usa el texto por defecto
   * (mismo que la página de error de Thymeleaf).
   */
  mensaje: string =
    this.route.snapshot.queryParamMap.get('mensaje') || 'Nada coincide con tu búsqueda.';
}

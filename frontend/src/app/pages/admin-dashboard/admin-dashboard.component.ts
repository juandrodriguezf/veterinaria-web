import { Component, inject } from '@angular/core';
import { AdminHeaderComponent } from '../../components/admin-header/admin-header.component';
import { FooterPortalComponent } from '../../components/footer-portal/footer-portal.component';
import { ResumenDashboard } from '../../models/dashboard.model';
import { DashboardService } from '../../service/dashboard.service';
import { KpiCardComponent } from './components/kpi-card/kpi-card.component';
import { KpiMedicamentosComponent } from './components/kpi-medicamentos/kpi-medicamentos.component';
import { TopMedicamentosComponent } from './components/top-medicamentos/top-medicamentos.component';

/**
 * Página del dashboard del administrador. Consume el resumen de KPIs del
 * backend y lo reparte en tarjetas y dos tablas; el formateo de números y
 * moneda se hace aquí para que las tarjetas solo pinten.
 */
@Component({
  selector: 'app-admin-dashboard',
  imports: [
    AdminHeaderComponent,
    FooterPortalComponent,
    KpiCardComponent,
    KpiMedicamentosComponent,
    TopMedicamentosComponent,
  ],
  templateUrl: './admin-dashboard.component.html',
  styleUrl: './admin-dashboard.component.scss',
})
export class AdminDashboardComponent {
  private dashboardService = inject(DashboardService);

  /** Nombre del administrador (sin sesión, igual que el portal veterinario). */
  nombreAdministrador: string = 'Dirección Vetopia';

  /** Resumen de KPIs; queda indefinido mientras carga. */
  resumen?: ResumenDashboard;

  /** Mensaje de error si la petición falla. */
  mensajeError = '';

  ngOnInit() {
    this.cargar();
  }

  /** Pide el resumen al backend; se reutiliza en el botón "Actualizar". */
  cargar() {
    this.mensajeError = '';
    this.dashboardService.obtenerResumen().subscribe({
      next: (resumen) => (this.resumen = resumen),
      error: () => {
        this.mensajeError = 'No se pudieron cargar los indicadores. Intenta de nuevo.';
      },
    });
  }

  /** Formatea un conteo con separadores de miles (es-CO). */
  formatearNumero(valor: number | undefined): string {
    return new Intl.NumberFormat('es-CO').format(valor ?? 0);
  }

  /** Formatea un valor monetario en pesos colombianos, sin decimales. */
  formatearMoneda(valor: number | undefined): string {
    return new Intl.NumberFormat('es-CO', {
      style: 'currency',
      currency: 'COP',
      maximumFractionDigits: 0,
    }).format(valor ?? 0);
  }
}

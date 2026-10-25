import { Component, input } from '@angular/core';

/**
 * Tarjeta de un indicador (KPI) del dashboard del administrador. Recibe
 * el título, el valor ya formateado, el ícono de Material Symbols y un
 * detalle opcional; no conoce el servicio ni hace peticiones.
 */
@Component({
  selector: 'app-kpi-card',
  templateUrl: './kpi-card.component.html',
  styleUrl: './kpi-card.component.scss',
})
export class KpiCardComponent {
  /** Nombre del indicador (por ejemplo "Ventas totales"). */
  titulo = input<string>('');

  /** Valor ya formateado para mostrar (número o moneda). */
  valor = input<string>('');

  /** Nombre del ícono de Material Symbols. */
  icono = input<string>('insights');

  /** Texto corto de apoyo debajo del valor (opcional). */
  detalle = input<string>('');
}

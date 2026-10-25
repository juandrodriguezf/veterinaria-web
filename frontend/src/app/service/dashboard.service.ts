import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResumenDashboard } from '../models/dashboard.model';

/**
 * SERVICIO - Dashboard
 * Capa de servicio del frontend: hace la petición HTTP a la API REST de
 * Spring Boot (patrón de la clase de REST). El backend calcula los KPIs y
 * devuelve un DTO plano; el componente solo se suscribe y los pinta.
 */
@Injectable({
  providedIn: 'root',
})
export class DashboardService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/administrador';

  /** Resumen de indicadores del negocio (GET /administrador/dashboard). */
  obtenerResumen(): Observable<ResumenDashboard> {
    return this.http.get<ResumenDashboard>(`${this.baseUrl}/dashboard`);
  }
}

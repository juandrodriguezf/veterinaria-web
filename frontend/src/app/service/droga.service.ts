import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Droga } from '../models/droga.model';

/**
 * SERVICIO - Droga
 * Capa de servicio del frontend: hace las peticiones HTTP a la API REST
 * de Spring Boot. El listado alimenta el selector de medicamentos del
 * formulario de asignación de tratamientos.
 */
@Injectable({
  providedIn: 'root',
})
export class DrogaService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/droga';

  /** Lista el inventario de medicamentos. */
  listarDrogas(): Observable<Droga[]> {
    return this.http.get<Droga[]>(`${this.baseUrl}/all`);
  }
}

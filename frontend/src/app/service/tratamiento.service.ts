import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Droga } from '../models/droga.model';
import { Tratamiento } from '../models/tratamiento.model';

/**
 * Cuerpo que el formulario envía al asignar un tratamiento: el backend
 * resuelve la mascota, la droga y el veterinario responsable a partir de
 * los id, así que solo viajan las referencias mínimas.
 */
export interface NuevoTratamiento {
  fecha: string;
  mascota: { id: number };
  droga: { id: number };
}

/**
 * SERVICIO - Tratamiento
 * Capa de servicio del frontend: hace las peticiones HTTP a la API REST de
 * Spring Boot (patrón de la clase de REST). El historial de una mascota se
 * pide con una consulta aparte (la colección de tratamientos de la mascota
 * va con @JsonIgnore).
 */
@Injectable({
  providedIn: 'root',
})
export class TratamientoService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/tratamiento';

  /** Historial clínico de una mascota (GET /byMascota/{id}). */
  listarTratamientosPorMascota(mascotaId: number): Observable<Tratamiento[]> {
    return this.http.get<Tratamiento[]>(`${this.baseUrl}/byMascota/${mascotaId}`);
  }

  /**
   * Asigna un tratamiento: el backend valida mascota activa y stock,
   * descuenta una unidad del inventario y devuelve la Droga actualizada.
   */
  asignar(tratamiento: NuevoTratamiento): Observable<Droga> {
    return this.http.post<Droga>(`${this.baseUrl}/add`, tratamiento);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Mascota } from '../models/mascota.model';

/**
 * SERVICIO - Mascota
 * Capa de servicio del frontend: hace las peticiones HTTP a la API REST de
 * Spring Boot (patrón de la clase de REST). Ya no siembra datos quemados ni
 * mantiene las dos caras de la relación en memoria: la persistencia y las
 * reglas viven en el backend.
 *
 * Regla del video: las consultas devuelven el Observable para que el
 * componente reaccione a la respuesta; los comandos que no actualizan la
 * vista (crear, actualizar, eliminar) hacen el subscribe aquí mismo.
 */
@Injectable({
  providedIn: 'root',
})
export class MascotaService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/mascota';

  /** Lista todas las mascotas. */
  listarMascotas(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/all`);
  }

  /** Obtiene una mascota por su identificador. */
  obtenerMascotaPorId(id: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.baseUrl}/find/${id}`);
  }

  /** Busca mascotas por nombre (coincidencia exacta, sin distinguir mayúsculas). */
  buscarPorNombre(nombre: string): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/search/${encodeURIComponent(nombre)}`);
  }

  /** Mascotas de un dueño (consulta aparte: la colección va con @JsonIgnore). */
  listarPorDueno(duenoId: number): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/byDueno/${duenoId}`);
  }

  /** Solo las mascotas activas (candidatas a recibir tratamiento). */
  listarMascotasActivas(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/activas`);
  }

  /**
   * Crea (sin id) o actualiza (con id) una mascota. Es un comando (el
   * componente no reacciona a la respuesta), así que el subscribe va aquí.
   */
  guardarValidada(mascota: Mascota): void {
    if (mascota.id) {
      this.http.put<void>(`${this.baseUrl}/update`, mascota).subscribe();
      return;
    }
    // En la creación se omite el id: el backend trata un id nulo como
    // registro nuevo (con id 0 lo tomaría como edición/merge).
    this.http.post<void>(`${this.baseUrl}/add`, { ...mascota, id: null }).subscribe();
  }

  /** Alterna el estado (Activo <-> Inactivo): el componente reacciona al nuevo estado. */
  alternarEstado(id: number): Observable<string> {
    return this.http.put(`${this.baseUrl}/toggle/${id}`, null, { responseType: 'text' });
  }

  /** Elimina definitivamente una mascota (comando: subscribe aquí). */
  eliminar(id: number): void {
    this.http.delete<void>(`${this.baseUrl}/delete/${id}`).subscribe();
  }
}

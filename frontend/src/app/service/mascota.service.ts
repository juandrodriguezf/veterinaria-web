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
 * Todos los métodos devuelven el Observable: el servicio solo arma la
 * petición y es el componente el que se suscribe, de modo que la vista
 * reaccione siempre a la respuesta del backend.
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
  listarPorDueno(duenoId: number, nombre?: string): Observable<Mascota[]> {
    const url = `${this.baseUrl}/byDueno/${duenoId}`;
    // Un solo endpoint sirve el listado del portal del cliente y su
    // búsqueda: el nombre viaja como parámetro opcional.
    return nombre && nombre.trim()
      ? this.http.get<Mascota[]>(url, { params: { nombre } })
      : this.http.get<Mascota[]>(url);
  }

  /** Mascota propia de un dueño (el backend valida la pertenencia). */
  obtenerPropia(id: number, duenoId: number): Observable<Mascota> {
    return this.http.get<Mascota>(`${this.baseUrl}/propia/${id}/${duenoId}`);
  }

  /** Solo las mascotas activas (candidatas a recibir tratamiento). */
  listarMascotasActivas(): Observable<Mascota[]> {
    return this.http.get<Mascota[]>(`${this.baseUrl}/activas`);
  }

  /**
   * Crea (sin id) o actualiza (con id) una mascota. Devuelve el observable
   * para que el componente navegue al listado solo cuando el backend
   * confirme el guardado.
   */
  guardarValidada(mascota: Mascota): Observable<void> {
    if (mascota.id) {
      return this.http.put<void>(`${this.baseUrl}/update`, mascota);
    }
    // En la creación se omite el id: el backend trata un id nulo como
    // registro nuevo (con id 0 lo tomaría como edición/merge).
    return this.http.post<void>(`${this.baseUrl}/add`, { ...mascota, id: null });
  }

  /** Alterna el estado (Activo <-> Inactivo): el componente reacciona al nuevo estado. */
  alternarEstado(id: number): Observable<string> {
    return this.http.put(`${this.baseUrl}/toggle/${id}`, null, { responseType: 'text' });
  }

  /** Elimina definitivamente una mascota. Devuelve el observable. */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }
}

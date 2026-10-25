import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Veterinario } from '../models/veterinario.model';

/**
 * SERVICIO - Veterinario
 * Capa de servicio del frontend: hace las peticiones HTTP a la API REST
 * de Spring Boot (patrón de la clase de REST). Alimenta el CRUD del
 * portal del administrador y el nombre del responsable en el portal del
 * veterinario.
 *
 * Todos los métodos devuelven el Observable: el servicio solo arma la
 * petición y es el componente el que se suscribe, de modo que la vista
 * reaccione siempre a la respuesta del backend.
 */
@Injectable({
  providedIn: 'root',
})
export class VeterinarioService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/veterinario';

  /** Lista todos los veterinarios. */
  listarVeterinarios(): Observable<Veterinario[]> {
    return this.http.get<Veterinario[]>(`${this.baseUrl}/all`);
  }

  /** Obtiene un veterinario por su identificador. */
  obtenerPorId(id: number): Observable<Veterinario> {
    return this.http.get<Veterinario>(`${this.baseUrl}/find/${id}`);
  }

  /** Busca veterinarios por cédula o nombre (coincidencia parcial). */
  buscar(termino: string): Observable<Veterinario[]> {
    return this.http.get<Veterinario[]>(`${this.baseUrl}/search/${encodeURIComponent(termino)}`);
  }

  /**
   * Crea o actualiza un veterinario: sin id va a /add (POST) y con id a
   * /update (PUT). Devuelve el observable para que el componente navegue
   * al listado solo cuando el backend confirme el guardado.
   */
  guardarValidada(veterinario: Veterinario): Observable<void> {
    if (veterinario.id) {
      return this.http.put<void>(`${this.baseUrl}/update`, veterinario);
    }
    // En la creación se omite el id: el backend trata un id nulo como
    // registro nuevo (con id 0 lo tomaría como edición).
    return this.http.post<void>(`${this.baseUrl}/add`, { ...veterinario, id: null });
  }

  /** Alterna el estado (Activo <-> Inactivo): el componente reacciona al nuevo estado. */
  alternarEstado(id: number): Observable<string> {
    return this.http.put(`${this.baseUrl}/toggle/${id}`, null, { responseType: 'text' });
  }

  /** Elimina el veterinario y desvincula sus tratamientos. Devuelve el observable. */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }
}

import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Dueno } from '../models/dueno.model';

/**
 * SERVICIO - Dueno (cliente)
 * Capa de servicio del frontend: su única tarea es hacer las peticiones
 * HTTP a la API REST de Spring Boot (patrón de la clase de REST). Ya no
 * siembra datos quemados: los clientes viven en la base de datos del
 * backend.
 *
 * Todos los métodos devuelven el Observable: el servicio solo arma la
 * petición y es el componente el que se suscribe, de modo que la vista
 * reaccione siempre a la respuesta del backend.
 */
@Injectable({
  providedIn: 'root',
})
export class DuenoService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/dueno';

  /** Lista todos los clientes. */
  listarDuenos(): Observable<Dueno[]> {
    return this.http.get<Dueno[]>(`${this.baseUrl}/all`);
  }

  /** Obtiene un cliente por su identificador. */
  obtenerPorId(id: number): Observable<Dueno> {
    return this.http.get<Dueno>(`${this.baseUrl}/find/${id}`);
  }

  /** Busca clientes por nombre (coincidencia exacta, sin distinguir mayúsculas). */
  buscarPorNombre(nombre: string): Observable<Dueno[]> {
    return this.http.get<Dueno[]>(`${this.baseUrl}/search/${encodeURIComponent(nombre)}`);
  }

  /**
   * Crea o actualiza un cliente: sin id va a /add (POST) y con id a
   * /update (PUT). Devuelve el observable para que el componente navegue
   * al listado solo cuando el backend confirme el guardado.
   */
  guardarValidada(dueno: Dueno): Observable<void> {
    if (dueno.id) {
      return this.http.put<void>(`${this.baseUrl}/update`, dueno);
    }
    // En la creación se omite el id: el backend trata un id nulo como
    // registro nuevo (con id 0 lo tomaría como edición).
    return this.http.post<void>(`${this.baseUrl}/add`, { ...dueno, id: null });
  }

  /** Alterna el estado (Activo <-> Inactivo): el componente reacciona al nuevo estado. */
  alternarEstado(id: number): Observable<string> {
    return this.http.put(`${this.baseUrl}/toggle/${id}`, null, { responseType: 'text' });
  }

  /** Elimina el cliente y, en cascada, sus mascotas. Devuelve el observable. */
  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/delete/${id}`);
  }
}

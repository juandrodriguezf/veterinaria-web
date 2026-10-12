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
 * Regla del video: las consultas devuelven el Observable para que el
 * componente reaccione a la respuesta; los comandos que no actualizan la
 * vista (crear, actualizar, eliminar) hacen el subscribe aquí mismo.
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
   * /update (PUT). Es un comando (el componente no reacciona a la
   * respuesta), así que el subscribe va aquí.
   */
  guardarValidada(dueno: Dueno): void {
    if (dueno.id) {
      this.http.put<void>(`${this.baseUrl}/update`, dueno).subscribe();
      return;
    }
    // En la creación se omite el id: el backend trata un id nulo como
    // registro nuevo (con id 0 lo tomaría como edición).
    this.http.post<void>(`${this.baseUrl}/add`, { ...dueno, id: null }).subscribe();
  }

  /** Alterna el estado (Activo <-> Inactivo): el componente reacciona al nuevo estado. */
  alternarEstado(id: number): Observable<string> {
    return this.http.put(`${this.baseUrl}/toggle/${id}`, null, { responseType: 'text' });
  }

  /** Elimina el cliente y, en cascada, sus mascotas (comando: subscribe aquí). */
  eliminar(id: number): void {
    this.http.delete<void>(`${this.baseUrl}/delete/${id}`).subscribe();
  }
}

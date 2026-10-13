import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Veterinario } from '../models/veterinario.model';

/**
 * SERVICIO - Veterinario
 * Capa de servicio del frontend: hace las peticiones HTTP a la API REST
 * de Spring Boot. El listado permite mostrar el veterinario responsable
 * del portal y del formulario de asignación de tratamientos.
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
}

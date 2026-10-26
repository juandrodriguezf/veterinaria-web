import { Injectable, inject } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ResultadoLogin } from '../models/resultado-login.model';

/**
 * SERVICIO - Login
 * Capa de servicio del frontend: hace la petición HTTP a la API REST de
 * Spring Boot (patrón de la clase de REST). El backend identifica el rol
 * (veterinario, administrador o cliente) y devuelve el id y el nombre del
 * usuario; con eso el componente guarda la sesión y navega al portal.
 */
@Injectable({
  providedIn: 'root',
})
export class LoginService {
  private http = inject(HttpClient);

  private readonly baseUrl = 'http://localhost:8080/login';

  /** Autentica por correo y contraseña; devuelve el rol, el id y el nombre. */
  autenticar(correo: string, contrasena: string): Observable<ResultadoLogin> {
    return this.http.post<ResultadoLogin>(this.baseUrl, { correo, contrasena });
  }
}

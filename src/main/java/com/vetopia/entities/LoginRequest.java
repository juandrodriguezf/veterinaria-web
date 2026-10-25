package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase de apoyo del proceso de login: cuerpo JSON que el frontend
 * Angular envía al endpoint POST /login con las credenciales del
 * usuario (correo o cédula, y contraseña).
 *
 * Lombok genera getters/setters (@Data) y los constructores vacío y con
 * todos los atributos, igual que en las demás clases del proyecto.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    /** Correo o cédula con el que el usuario inicia sesión. */
    private String correo;

    /** Contraseña del usuario. */
    private String contrasena;
}

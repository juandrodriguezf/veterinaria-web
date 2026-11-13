package com.vetopia.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - LoginRequest
 * Cuerpo JSON del login: el identificador acepta correo o cédula, y la
 * contraseña viaja en claro solo hasta el backend, donde se compara con
 * el hash BCrypt de la cuenta.
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

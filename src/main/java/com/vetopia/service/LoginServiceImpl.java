package com.vetopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.ResultadoLogin;
import com.vetopia.entities.Veterinario;

/**
 * CAPA SERVICIO - Implementación de LoginService
 * Busca las credenciales en cada tabla (delegando en los services de cada
 * entidad) hasta encontrar un match. El identificador puede ser el correo
 * o la cédula, igual que ofrece el formulario. El orden Veterinario ->
 * Administrador -> Dueño define qué rol se asigna cuando el mismo
 * identificador existiera en más de una tabla.
 */
@Service
public class LoginServiceImpl implements LoginService {

    /** Rol del portal del veterinario. */
    private static final String ROL_VETERINARIO = "VETERINARIO";

    /** Rol del portal del administrador. */
    private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    /** Rol del portal del cliente. */
    private static final String ROL_CLIENTE = "CLIENTE";

    @Autowired
    private VeterinarioService veterinarioService;

    @Autowired
    private AdministradorService administradorService;

    @Autowired
    private DuenoService duenoService;

    @Override
    public ResultadoLogin autenticar(String identificador, String contrasena) {
        // Credenciales incompletas: no tiene sentido consultar las tablas
        // y el mensaje indica qué le falta al usuario.
        if (identificador == null || identificador.isBlank()
                || contrasena == null || contrasena.isBlank()) {
            throw new IllegalArgumentException(
                    "Ingresa tu cédula o correo y tu contraseña para continuar.");
        }

        // Se consulta cada repositorio en orden hasta encontrar la coincidencia;
        // el primero que responde define el rol y el panel de destino. Cada
        // service acepta correo o cédula en el identificador.
        Veterinario veterinario = veterinarioService.obtenerPorCorreoYContrasena(identificador, contrasena);
        if (veterinario != null) {
            return new ResultadoLogin(ROL_VETERINARIO, veterinario.getId());
        }

        Administrador administrador = administradorService.obtenerPorCorreoYContrasena(identificador, contrasena);
        if (administrador != null) {
            return new ResultadoLogin(ROL_ADMINISTRADOR, administrador.getId());
        }

        Dueno dueno = duenoService.obtenerPorCorreoYContrasena(identificador, contrasena);
        if (dueno != null) {
            return new ResultadoLogin(ROL_CLIENTE, dueno.getId());
        }

        // Ninguna tabla reconoció la combinación: la excepción lleva el
        // mensaje que el formulario de login debe mostrar.
        throw new IllegalArgumentException(
                "Cédula, correo o contraseña incorrectos. Verifica tus credenciales.");
    }
}

package com.vetopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;

/**
 * CAPA SERVICIO - Implementación de LoginService
 * Busca las credenciales en cada tabla (delegando en los services de cada
 * entidad) hasta encontrar un match. El orden Veterinario -> Administrador
 * -> Dueño define qué rol se asigna cuando el mismo correo existiera en
 * más de una tabla (incoherencia que la fake DB evita).
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
    public ResultadoLogin autenticar(String correo, String contrasena) {
        if (correo == null || correo.isBlank() || contrasena == null || contrasena.isBlank()) {
            return null;
        }

        Veterinario veterinario = veterinarioService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (veterinario != null) {
            return new ResultadoLogin(ROL_VETERINARIO, veterinario.getId());
        }

        Administrador administrador = administradorService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (administrador != null) {
            return new ResultadoLogin(ROL_ADMINISTRADOR, administrador.getId());
        }

        Dueno dueno = duenoService.obtenerPorCorreoYContrasena(correo, contrasena);
        if (dueno != null) {
            return new ResultadoLogin(ROL_CLIENTE, dueno.getId());
        }

        return null;
    }
}

package com.vetopia.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.vetopia.dto.UsuarioSesion;
import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.VeterinarioRepository;

/**
 * CAPA SERVICIO - Implementación de PerfilUsuarioService
 * Los identificadores (correo y cédula) son únicos entre las tres tablas,
 * así que la primera coincidencia define el perfil y su rol.
 */
@Service
public class PerfilUsuarioServiceImpl implements PerfilUsuarioService {

    /** Rol del cliente (dueño de mascotas). */
    private static final String ROL_CLIENTE = "CLIENTE";

    /** Rol del veterinario. */
    private static final String ROL_VETERINARIO = "VETERINARIO";

    /** Rol del administrador. */
    private static final String ROL_ADMINISTRADOR = "ADMINISTRADOR";

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public UsuarioSesion obtenerPorUsername(String username) {
        Dueno dueno = duenoRepository.findByCorreoIgnoreCase(username);
        if (dueno != null) {
            return new UsuarioSesion(ROL_CLIENTE, dueno.getId(), dueno.getNombre());
        }
        Veterinario veterinario = veterinarioRepository.findByCorreoIgnoreCase(username);
        if (veterinario != null) {
            return new UsuarioSesion(ROL_VETERINARIO, veterinario.getId(), veterinario.getNombre());
        }
        Administrador administrador = administradorRepository.findByCorreoIgnoreCase(username);
        if (administrador != null) {
            return new UsuarioSesion(ROL_ADMINISTRADOR, administrador.getId(), administrador.getNombre());
        }
        throw new UsernameNotFoundException(
                "No encontramos ningún perfil asociado a la cuenta \"" + username + "\".");
    }

    @Override
    public String resolverUsername(String identificador) {
        String valor = identificador == null ? "" : identificador.trim();
        // Un correo se usa tal cual: la autenticación compara sin distinguir
        // mayúsculas. Una cédula se traduce al correo de su perfil.
        if (valor.contains("@")) {
            return valor;
        }
        Dueno dueno = duenoRepository.findByCedula(valor);
        if (dueno != null) {
            return dueno.getCorreo();
        }
        Veterinario veterinario = veterinarioRepository.findByCedula(valor);
        if (veterinario != null) {
            return veterinario.getCorreo();
        }
        Administrador administrador = administradorRepository.findByCedula(valor);
        if (administrador != null) {
            return administrador.getCorreo();
        }
        throw new BadCredentialsException(
                "Cédula, correo o contraseña incorrectos. Verifica tus credenciales.");
    }
}

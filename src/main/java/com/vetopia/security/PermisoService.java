package com.vetopia.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.vetopia.dto.UsuarioSesion;
import com.vetopia.entities.Mascota;
import com.vetopia.service.MascotaService;
import com.vetopia.service.PerfilUsuarioService;

/**
 * SERVICIO - PermisoService
 * Apoya las expresiones @PreAuthorize con la identidad firmada del token:
 * resuelve el perfil autenticado y comprueba la propiedad de los recursos
 * de un cliente. Así un id manipulado en la URL no da acceso a datos ajenos.
 */
@Component("permisoService")
public class PermisoService {

    private final PerfilUsuarioService perfilUsuarioService;

    private final MascotaService mascotaService;

    public PermisoService(PerfilUsuarioService perfilUsuarioService,
            MascotaService mascotaService) {
        this.perfilUsuarioService = perfilUsuarioService;
        this.mascotaService = mascotaService;
    }

    /** Perfil autenticado (rol, id y nombre) según el usuario del token. */
    public UsuarioSesion perfilAutenticado() {
        Authentication autenticacion = SecurityContextHolder.getContext().getAuthentication();
        return perfilUsuarioService.obtenerPorUsername(autenticacion.getName());
    }

    /** Identificador del perfil autenticado en su tabla de negocio. */
    public Integer perfilAutenticadoId() {
        return perfilAutenticado().getId();
    }

    /** Rol del usuario autenticado ("ADMINISTRADOR", "VETERINARIO" o "CLIENTE"). */
    public String rolAutenticado() {
        return perfilAutenticado().getRol();
    }

    /** Indica si el id de la ruta corresponde al perfil autenticado. */
    public boolean esPerfilAutenticado(Integer id) {
        return id != null && id.equals(perfilAutenticadoId());
    }

    /** Indica si el veterinario autenticado es el dueño del recurso. */
    public boolean esVeterinarioAutenticado(Integer veterinarioId) {
        return veterinarioId != null
                && "VETERINARIO".equals(rolAutenticado())
                && veterinarioId.equals(perfilAutenticadoId());
    }

    /**
     * Indica si la mascota pertenece al cliente autenticado. Si la mascota
     * no existe se responde false para no revelar recursos inexistentes.
     */
    public boolean esMascotaDelCliente(Integer mascotaId) {
        if (mascotaId == null || !"CLIENTE".equals(rolAutenticado())) {
            return false;
        }
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(mascotaId);
            return mascota.getDueno() != null
                    && mascota.getDueno().getId().equals(perfilAutenticadoId());
        } catch (RuntimeException excepcion) {
            return false;
        }
    }
}

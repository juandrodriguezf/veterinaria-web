package com.vetopia.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Role;
import com.vetopia.entities.UserEntity;
import com.vetopia.repository.RoleRepository;

/**
 * CAPA SERVICIO - Implementación de CuentaUsuarioService
 * Crea la cuenta unificada de cada perfil, cifra la contraseña con BCrypt y
 * reutiliza los roles existentes para no duplicarlos. La cuenta se guarda
 * en cascada cuando se persiste el perfil que la referencia.
 */
@Service
public class CuentaUsuarioServiceImpl implements CuentaUsuarioService {

    /** Repositorio de roles (find-or-create por nombre). */
    @Autowired
    private RoleRepository roleRepository;

    /** Codificador BCrypt compartido con la configuración de seguridad. */
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    public UserEntity construir(String username, String contrasena, String rol) {
        List<Role> roles = new ArrayList<>();
        roles.add(obtenerRol(rol));
        return UserEntity.builder()
                .username(normalizar(username))
                .password(passwordEncoder.encode(contrasena))
                .activo(true)
                .roles(roles)
                .build();
    }

    @Override
    public void sincronizar(UserEntity cuenta, String username, String contrasena, String rol) {
        cuenta.setUsername(normalizar(username));
        // Contraseña vacía significa "conservar la actual": el formulario de
        // edición no devuelve la clave y no debe invalidarla.
        if (contrasena != null && !contrasena.isBlank()) {
            cuenta.setPassword(passwordEncoder.encode(contrasena));
        }
        List<Role> roles = new ArrayList<>();
        roles.add(obtenerRol(rol));
        cuenta.setRoles(roles);
    }

    @Override
    public void cambiarEstado(UserEntity cuenta, boolean activo) {
        if (cuenta != null) {
            cuenta.setActivo(activo);
        }
    }

    /**
     * Devuelve el rol por nombre y lo crea si aún no existe. Los roles son
     * un catálogo pequeño y fijo, así que la creación es idempotente.
     */
    private Role obtenerRol(String nombre) {
        return roleRepository.findByNombre(nombre).orElseGet(
                () -> roleRepository.save(Role.builder().nombre(nombre).build()));
    }

    /** Elimina espacios accidentales del nombre de usuario. */
    private String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}

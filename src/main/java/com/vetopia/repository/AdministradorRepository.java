package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Administrador;

/**
 * REPOSITORIO - Administrador
 * Operaciones estándar heredadas de JpaRepository más consultas derivadas
 * por nombre de propiedad.
 */
@Repository
public interface AdministradorRepository extends JpaRepository<Administrador, Integer> {

    /** Administrador cuyas credenciales coinciden, o null si no existe. */
    Administrador findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}

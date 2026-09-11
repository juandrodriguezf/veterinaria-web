package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Veterinario;

/**
 * REPOSITORIO - Veterinario
 * Operaciones estándar heredadas de JpaRepository más consultas derivadas
 * por nombre de propiedad (los veterinarios entran sin filtro de estado:
 * se controla el acceso por la capa de servicio).
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {

    /** Veterinario cuyas credenciales coinciden, o null si no existe. */
    Veterinario findByCorreoIgnoreCaseAndContrasena(String correo, String contrasena);
}

package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Role;

/**
 * REPOSITORIO - Role
 * Operaciones estándar de JpaRepository más la búsqueda por nombre, que
 * permite reutilizar los roles existentes y no duplicarlos al crear cuentas.
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    /** Busca un rol por su nombre (por ejemplo ROLE_VETERINARIO). */
    Optional<Role> findByNombre(String nombre);
}

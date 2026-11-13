package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.UserEntity;

/**
 * REPOSITORIO - UserEntity
 * Operaciones estándar de JpaRepository más las consultas por nombre de
 * usuario que usarán la autenticación y la verificación de cuentas.
 */
@Repository
public interface UserRepository extends JpaRepository<UserEntity, Integer> {

    /** Busca la cuenta por su nombre de usuario (correo). */
    Optional<UserEntity> findByUsername(String username);

    /**
     * Busca la cuenta por su nombre de usuario sin distinguir
     * mayúsculas/minúsculas (el correo se captura con cualquier capitalización).
     */
    Optional<UserEntity> findByUsernameIgnoreCase(String username);
}

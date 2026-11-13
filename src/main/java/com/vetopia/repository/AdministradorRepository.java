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

    /** Administrador por correo: soporta la validación de unicidad global. */
    Administrador findByCorreoIgnoreCase(String correo);

    /** Administrador por cédula: soporta la validación de unicidad global. */
    Administrador findByCedula(String cedula);
}

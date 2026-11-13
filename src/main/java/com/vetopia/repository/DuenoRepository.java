package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Dueno;

/**
 * REPOSITORIO - Dueno
 * Operaciones estándar heredadas de JpaRepository (Spring Data genera su
 * implementación) más consultas derivadas por nombre de propiedad.
 */
@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Integer> {

    /**
     * Cliente por correo: soporta la validación de unicidad global
     * entre clientes, veterinarios y administradores.
     */
    Dueno findByCorreoIgnoreCase(String correo);

    /**
     * Cliente por cédula: soporta la validación de unicidad global
     * entre clientes, veterinarios y administradores.
     */
    Dueno findByCedula(String cedula);

    /**
     * Clientes cuyo nombre contiene el término buscado, sin distinguir
     * mayúsculas/minúsculas (AC21: buscar un cliente por su nombre). Usa
     * la misma coincidencia parcial que MascotaRepository y la búsqueda
     * de veterinarios, para no exigir el nombre completo.
     */
    List<Dueno> findByNombreContainingIgnoreCase(String nombre);
}

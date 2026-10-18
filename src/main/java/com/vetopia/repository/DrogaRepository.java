package com.vetopia.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Droga;

/**
 * REPOSITORIO - Droga
 * Operaciones estándar heredadas de JpaRepository (findAll, findById,
 * save...): el inventario solo necesita listado, búsqueda por id,
 * actualización de unidades y la búsqueda por nombre que usa la carga
 * desde Excel para actualizar en vez de duplicar.
 */
@Repository
public interface DrogaRepository extends JpaRepository<Droga, Integer> {

    /**
     * Busca una droga por su nombre (columna única del inventario). La
     * carga desde Excel la usa para hacer upsert: si el nombre ya existe
     * se actualizan sus valores, si no se registra una droga nueva.
     */
    Optional<Droga> findByNombre(String nombre);
}

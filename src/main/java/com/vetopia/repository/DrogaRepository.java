package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Droga;

/**
 * REPOSITORIO - Droga
 * Operaciones estándar heredadas de JpaRepository (findAll, findById,
 * save...): el inventario solo necesita listado, búsqueda por id y
 * actualización de unidades.
 */
@Repository
public interface DrogaRepository extends JpaRepository<Droga, Integer> {
}

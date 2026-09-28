package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Tratamiento;

/**
 * REPOSITORIO - Tratamiento
 * Operaciones estándar heredadas de JpaRepository (findAll, findById,
 * save...) más la consulta derivada por mascota, que soporta la
 * desvinculación del historial clínico desde el service.
 */
@Repository
public interface TratamientoRepository extends JpaRepository<Tratamiento, Integer> {

    /**
     * Tratamientos aplicados a una mascota: soporta que el service los
     * desvincule (FK a nulo) antes de eliminar la mascota, conservando
     * el historial clínico.
     */
    List<Tratamiento> findByMascotaId(Integer mascotaId);
}

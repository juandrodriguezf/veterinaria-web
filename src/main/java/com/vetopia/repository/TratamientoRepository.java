package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Tratamiento;

/**
 * Único punto de acceso a los datos de tratamientos. Define las operaciones
 * de persistencia que la capa Service puede utilizar sin exponer detalles
 * de la fuente de datos (por ahora HashMap en memoria).
 */
public interface TratamientoRepository {

    /** Devuelve todos los tratamientos registrados. */
    Collection<Tratamiento> searchAll();

    /**
     * Busca un tratamiento por su identificador dentro del HashMap.
     */
    Tratamiento searchById(Integer id);

    /**
     * Guarda (persiste) un tratamiento en el HashMap asignándole
     * automáticamente el siguiente id disponible.
     */
    void save(Tratamiento tratamiento);
}

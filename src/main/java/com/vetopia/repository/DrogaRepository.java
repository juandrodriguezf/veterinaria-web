package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Droga;

/**
 * Único punto de acceso a los datos de drogas/medicamentos. Define las
 * operaciones de persistencia que la capa Service puede utilizar sin
 * exponer detalles de la fuente de datos (por ahora HashMap en memoria).
 */
public interface DrogaRepository {

    /** Devuelve todas las drogas registradas. */
    Collection<Droga> searchAll();

    /**
     * Busca una droga por su identificador dentro del HashMap.
     */
    Droga searchById(Integer id);

    /**
     * Guarda (persiste) una droga en el HashMap asignándole
     * automáticamente el siguiente id disponible.
     */
    void save(Droga droga);
}

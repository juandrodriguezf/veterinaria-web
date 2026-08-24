package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Mascota;

/**
 * Único punto de acceso a los datos de mascotas. Define las operaciones
 * de persistencia que la capa Service puede utilizar sin exponer detalles
 * de la fuente de datos (por ahora HashMap en memoria).
 */
public interface MascotaRepository {

    /**
     * Devuelve todas las mascotas registradas en la "tabla" mascotas
     * almacenada en un HashMap.
     */
    Collection<Mascota> searchAll();

    /**
     * Busca una mascota por su identificador dentro del HashMap.
     */
    Mascota searchById(Integer id);
}

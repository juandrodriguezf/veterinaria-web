package com.vetopia.repository;

import java.util.Collection;

import com.vetopia.entities.Mascota;

/**
 * CAPA REPOSITORIO / DAO - Contrato de MascotaRepository
 * Único punto de acceso a los datos de mascotas. Define las operaciones
 * de persistencia que la capa Service puede utilizar sin exponer detalles
 * de la fuente de datos (por ahora HashMap en memoria).
 */
public interface MascotaRepository {

    /**
     * Devuelve todas las mascotas registradas en la "tabla" mascotas
     * almacenada en un HashMap.
     *
     * @return colección con todos los registros; vacía si no hay datos,
     *         nunca {@code null}.
     */
    Collection<Mascota> searchAll();

    /**
     * Busca una mascota por su identificador dentro del HashMap.
     *
     * @param id identificador único de la mascota.
     * @return la mascota encontrada o {@code null} si no existe.
     */
    Mascota searchById(Integer id);
}

package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Mascota;

/**
 * CAPA SERVICIO - Contrato de MascotaService
 * Define la lógica de negocio relacionada con mascotas. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface MascotaService {

    /**
     * Lista las mascotas ordenadas alfabéticamente por nombre
     * (regla de negocio para el listado del portal del cliente).
     *
     * @return lista de mascotas ordenada; vacía si no hay registros.
     */
    List<Mascota> listarMascotas();

    /**
     * Obtiene una mascota específica validando previamente el
     * identificador recibido.
     *
     * @param id identificador de la mascota a consultar.
     * @return la mascota correspondiente o {@code null} si no existe.
     * @throws IllegalArgumentException cuando el identificador es nulo
     *                                  o no es un entero positivo.
     */
    Mascota obtenerMascotaPorId(Integer id);
}

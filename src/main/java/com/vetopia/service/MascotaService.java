package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Mascota;

/**
 * SERVICIO - MascotaService
 * Define la lógica de negocio relacionada con mascotas. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface MascotaService {

    /**
     * Lista las mascotas ordenadas alfabéticamente por nombre
     * (regla de negocio para el listado del portal del cliente).
     */
    List<Mascota> listarMascotas();

    /**
     * Obtiene una mascota específica validando previamente el
     * identificador recibido.
     */
    Mascota obtenerMascotaPorId(Integer id);
}

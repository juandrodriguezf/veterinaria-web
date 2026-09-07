package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;

/**
 * SERVICIO - TratamientoService
 * Define la lógica de negocio relacionada con tratamientos. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface TratamientoService {

    /** Lista todos los tratamientos registrados. */
    List<Tratamiento> listarTratamientos();

    /** Obtiene un tratamiento validando previamente el identificador. */
    Tratamiento obtenerTratamientoPorId(Integer id);

    /** Guarda (registra) un tratamiento nuevo. El id lo asigna el repositorio. */
    void guardar(Tratamiento tratamiento);

    /**
     * Retira los tratamientos aplicados a una mascota. La usa el borrado
     * en cascada para que la mascota no deje referencias huérfanas.
     */
    void eliminarPorMascota(Integer mascotaId);

    /**
     * Registra la asignación del tratamiento aplicando las reglas de
     * negocio: exige mascota y medicamento, descuenta una unidad del
     * inventario de la droga y devuelve la droga actualizada para la
     * vista de confirmación. Lanza IllegalArgumentException si faltan
     * datos y IllegalStateException si el medicamento no existe o se
     * quedó sin unidades.
     */
    Droga asignar(Tratamiento tratamiento);
}

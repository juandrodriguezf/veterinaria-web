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

    /**
     * Guarda (registra) una nueva mascota. El identificador lo asigna
     * la capa repository automáticamente.
     */
    void guardar(Mascota mascota);

    /**
     * Guarda la mascota aplicando las reglas de negocio: asigna el estado
     * inicial "Activo" cuando es un registro nuevo y exige un dueño
     * asignado para no dejar registros huérfanos. Lanza
     * IllegalArgumentException si no llegan datos y
     * IllegalStateException si la mascota queda sin dueño.
     */
    void guardarValidada(Mascota mascota);

    /**
     * Obtiene la mascota con el id indicado y valida que pertenezca al
     * dueño dado (aislamiento de datos del portal del cliente). Lanza
     * IllegalArgumentException si el id es inválido o la mascota no
     * existe, y IllegalStateException si la mascota es de otro dueño.
     */
    Mascota obtenerPropia(Integer id, Integer duenoId);

    /**
     * Alterna el estado de la mascota (Activo <-> Inactivo, el borrado
     * lógico del diagrama de clases) y devuelve el nuevo estado; null si
     * la mascota no existe.
     */
    String alternarEstado(Integer id);

    /**
     * Cambia el estado (Activo/Inactivo) de una mascota por su id.
     * Usado por el veterinario para dar de alta o de baja una mascota.
     */
    void cambiarEstado(Integer id, String estado);

    /** Elimina definitivamente una mascota por su id (borrado físico). */
    void eliminar(Integer id);

    /**
     * Lista solo las mascotas que pertenecen al dueño indicado
     * (relación Dueno 1 -- 0..* Mascota del diagrama de clases).
     */
    List<Mascota> listarMascotasPorDueno(Integer duenoId);
}

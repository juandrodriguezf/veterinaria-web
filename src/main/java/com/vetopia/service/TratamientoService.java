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

    /**
     * Lista los tratamientos de una mascota (su historial clínico). La
     * colección de la entidad viaja con @JsonIgnore en la API REST, así
     * que la ficha clínica pide el historial con esta consulta aparte.
     */
    List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId);

    /**
     * Lista los tratamientos aplicados por un veterinario. Alimenta la
     * pestaña "Tratadas" del portal, donde cada veterinario consulta las
     * mascotas que ha atendido anteriormente.
     */
    List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId);

    /** Obtiene un tratamiento validando previamente el identificador. */
    Tratamiento obtenerTratamientoPorId(Integer id);

    /** Guarda (registra) un tratamiento nuevo. El id lo asigna el repositorio. */
    void guardar(Tratamiento tratamiento);

    /**
     * Desvincula de la mascota los tratamientos que recibio, dejando la
     * referencia en nulo en vez de borrarlos: el historial clinico se
     * conserva cuando la mascota (o su dueno) se elimina.
     */
    void desvincularDeMascota(Integer mascotaId);

    /**
     * Desvincula del veterinario los tratamientos que aplico, dejando la
     * referencia en nulo en vez de borrarlos: el historial clinico se
     * conserva cuando el administrador elimina al veterinario.
     */
    void desvincularDeVeterinario(Integer veterinarioId);

    /**
     * Registra la asignación del tratamiento aplicando las reglas de
     * negocio: exige mascota, veterinario y medicamento, valida que la
     * mascota y el veterinario estén activos, descuenta una unidad del
     * inventario de la droga y devuelve la droga actualizada para la vista
     * de confirmación. Lanza IllegalArgumentException si faltan datos,
     * RecursoNoEncontradoException si alguna referencia no existe e
     * IllegalStateException si la mascota o el veterinario están inactivos
     * o si el medicamento se quedó sin unidades.
     */
    Droga asignar(Tratamiento tratamiento);
}

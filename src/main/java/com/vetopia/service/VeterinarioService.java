package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Veterinario;

/**
 * SERVICIO - VeterinarioService
 * Define la lógica de negocio relacionada con veterinarios. Es el único
 * punto al que los controladores acceden para obtener datos; a su vez,
 * delega el acceso a datos en la capa repository.
 */
public interface VeterinarioService {

    /** Lista todos los veterinarios registrados. */
    List<Veterinario> listarVeterinarios();

    /**
     * Busca veterinarios por cédula o por nombre (coincidencia parcial,
     * sin distinguir mayúsculas/minúsculas). Si el término es nulo o
     * vacío devuelve el listado completo, de modo que el mismo endpoint
     * sirve para la barra de búsqueda del portal del administrador.
     */
    List<Veterinario> buscar(String termino);

    /** Obtiene un veterinario validando previamente el identificador. */
    Veterinario obtenerVeterinarioPorId(Integer id);

    /**
     * Guarda un veterinario nuevo o actualiza uno existente. Valida que
     * la cédula y el correo no se repitan; en edición, una contraseña en
     * blanco conserva la actual y el estado vacío se completa con
     * "Activo" al crear.
     */
    void guardar(Veterinario veterinario);

    /**
     * Busca un veterinario por correo o cédula y contraseña (para login).
     * Solo devuelve el registro si su estado es "Activo": un veterinario
     * desactivado por el administrador no puede ingresar al portal.
     */
    Veterinario obtenerPorCorreoYContrasena(String identificador, String contrasena);

    /**
     * Alterna el estado del veterinario (Activo <-> Inactivo) y devuelve
     * el nuevo estado: es el borrado lógico laboral del portal del
     * administrador.
     */
    String alternarEstado(Integer id);

    /** Cambia el estado (Activo/Inactivo) de un veterinario por su id. */
    void cambiarEstado(Integer id, String estado);

    /**
     * Elimina definitivamente un veterinario. Antes de borrarlo desvincula
     * sus tratamientos (FK a nulo) para conservar el historial clínico.
     */
    void eliminar(Integer id);
}

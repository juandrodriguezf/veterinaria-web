package com.vetopia.service;

import java.util.List;

import com.vetopia.entities.Dueno;

/**
 * SERVICIO - DuenoService
 * Define la lógica de negocio relacionada con dueños. Es el único punto
 * al que los controladores acceden para obtener datos; a su vez, delega
 * el acceso a datos en la capa repository.
 */
public interface DuenoService {

    /** Lista todos los dueños registrados. */
    List<Dueno> listarDuenos();

    /** Obtiene un dueño validando previamente el identificador. */
    Dueno obtenerDuenoPorId(Integer id);

    /** Guarda (registra) un dueño nuevo. El id lo asigna el repositorio. */
    void guardar(Dueno dueno);

    /** Busca un dueño por correo y contraseña (para login). */
    Dueno obtenerPorCorreoYContrasena(String correo, String contrasena);

    /**
     * Obtiene el dueño con el id indicado y valida que esté "Activo"
     * (misma política del login: un cliente desactivado no ingresa al
     * portal). Lanza IllegalArgumentException si el id es inválido y
     * IllegalStateException si el dueño no existe o está inactivo.
     */
    Dueno obtenerActivo(Integer id);

    /**
     * Alterna el estado del dueño (Activo <-> Inactivo, el borrado
     * lógico del diagrama de clases) y devuelve el nuevo estado; null si
     * el dueño no existe.
     */
    String alternarEstado(Integer id);

    /**
     * Elimina el dueño y, en cascada, sus mascotas (borrado físico),
     * para no dejar registros huérfanos en el portal del cliente.
     */
    void eliminarEnCascada(Integer id);

    /** Cambia el estado (Activo/Inactivo) de un dueño por su id. */
    void cambiarEstado(Integer id, String estado);

    /** Elimina definitivamente un dueño por su id (borrado físico). */
    void eliminar(Integer id);
}

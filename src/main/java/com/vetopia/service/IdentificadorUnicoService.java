package com.vetopia.service;

/**
 * SERVICIO - IdentificadorUnicoService
 * Centraliza la regla de negocio que impide repetir la cédula o el correo
 * de una cuenta entre clientes, veterinarios y administradores. El login
 * usa esos datos como identificador, así que deben ser únicos en todo el
 * sistema y no solo dentro de cada tabla.
 */
public interface IdentificadorUnicoService {

    /**
     * Valida que la cédula y el correo no pertenezcan a otra cuenta al
     * crear o editar un cliente. El id actual se ignora para permitir
     * guardar una edición sin cambiar sus propios identificadores.
     */
    void validarCliente(String cedula, String correo, Integer idActual);

    /** Aplica la misma regla global al crear o editar un veterinario. */
    void validarVeterinario(String cedula, String correo, Integer idActual);

    /** Aplica la misma regla global al crear o editar un administrador. */
    void validarAdministrador(String cedula, String correo, Integer idActual);
}

package com.vetopia.errors;

/**
 * Excepción de negocio para indicar que el recurso solicitado (mascota,
 * cliente, tratamiento, droga, etc.) no existe en la base de datos.
 *
 * Es una RuntimeException: no obliga a declararla en los métodos y llega
 * al RestExceptionHandler, que la traduce en una respuesta JSON de error
 * con el mensaje que la capa de servicio preparó.
 */
public class RecursoNoEncontradoException extends RuntimeException {

    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}

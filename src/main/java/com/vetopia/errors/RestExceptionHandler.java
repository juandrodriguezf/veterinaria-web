package com.vetopia.errors;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

/**
 * MANEJADOR GLOBAL DE ERRORES - API REST
 * Atrapa las excepciones que escapan de los controllers REST y las traduce
 * en una respuesta JSON con el mensaje de la capa de servicio, igual que el
 * GlobalExceptionHandler de Thymeleaf, pero en formato API en vez de una
 * vista. Es el equivalente REST del patrón "página de error amable": el
 * frontend recibe un código HTTP coherente (400, 404, 409, 500) y un cuerpo
 * {@code {"mensaje": ...}} que puede mostrar en su banner.
 *
 * @RestControllerAdvice aplica a todos los controllers de la aplicación, de
 * modo que ninguna capa necesita conocer esta traducción. Se anota con
 * @CrossOrigin para que la respuesta de error también viaje con las cabeceras
 * que el frontend Angular (http://localhost:4200) necesita.
 *
 * El controller de login conserva su propio try/catch (traduce el
 * IllegalArgumentException a 401); este manejador cubre el resto de casos.
 */
@RestControllerAdvice
@CrossOrigin(origins = "http://localhost:4200")
public class RestExceptionHandler {

    /** Logger para registrar los errores de la API. */
    private static final Logger log = LoggerFactory.getLogger(RestExceptionHandler.class);

    /**
     * Recurso inexistente (id válido pero sin registro en la base): debe
     * verse como un 404 limpio con el mensaje del service, no como un 500.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, String>> manejarNoEncontrado(RecursoNoEncontradoException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", excepcion.getMessage()));
    }

    /**
     * Ruta sin controller ni recurso estático: responde 404 en vez de dejar
     * que el 500 genérico se coma una URL desconocida.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<Map<String, String>> manejarRutaNoEncontrada(NoResourceFoundException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of("mensaje", "La ruta solicitada no existe."));
    }

    /**
     * Petición mal formada (faltan datos, identificador inválido, etc.):
     * responde 400 con el mensaje que preparó el service.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, String>> manejarSolicitudInvalida(IllegalArgumentException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", excepcion.getMessage()));
    }

    /**
     * Valor de la ruta que no se puede convertir al tipo esperado (por
     * ejemplo {@code /mascota/find/abc}, donde el id es numérico). Spring
     * falla antes de llegar al service, así que se traduce aquí a un 400
     * en vez del 500 genérico.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> manejarTipoInvalido(MethodArgumentTypeMismatchException excepcion) {
        log.warn(excepcion.getMessage());
        String mensaje = "El valor \"" + excepcion.getValue() + "\" no es válido para \"" + excepcion.getName() + "\".";
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", mensaje));
    }

    /**
     * Cuerpo de la petición ilegible (JSON mal formado o con tipos
     * incorrectos): también es un 400, no un 500.
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> manejarCuerpoIlegible(HttpMessageNotReadableException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of("mensaje", "El cuerpo de la petición no es válido."));
    }

    /**
     * Conflicto con el estado actual (mascota inactiva, droga sin
     * unidades, cliente inactivo, etc.): responde 409 con el mensaje.
     */
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<Map<String, String>> manejarConflicto(IllegalStateException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", excepcion.getMessage()));
    }

    /**
     * Violación de una restricción de la base (por ejemplo, una cédula
     * repetida que llegue a la vez por dos peticiones): 409 con un aviso
     * entendible en vez de un 500 críptico.
     */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String, String>> manejarDatosDuplicados(DataIntegrityViolationException excepcion) {
        log.warn(excepcion.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of("mensaje", "No se pudo completar la operación porque los datos entran en conflicto con un registro existente."));
    }

    /**
     * Red de seguridad para cualquier otro error no contemplado: en vez del
     * 500 genérico, devuelve el mismo formato con un mensaje amable.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> manejarGenerico(Exception excepcion) {
        log.error("Error no contemplado", excepcion);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("mensaje", "Ocurrió un error inesperado en la aplicación. Intenta de nuevo."));
    }
}

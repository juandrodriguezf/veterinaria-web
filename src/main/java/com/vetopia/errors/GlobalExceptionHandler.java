package com.vetopia.errors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * MANEJADOR GLOBAL DE ERRORES
 * Atrapa las excepciones que escapan de los controllers y las traduce
 * en una página de error amable (error.html), igual que el patrón del
 * ejemplo: la vista recibe el mensaje con el modelo.
 *
 * @ControllerAdvice aplica a todos los controllers de la aplicación,
 * de modo que ninguna capa necesita conocer esta traducción.
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    /** Logger ayuda a identificar errores de manera sencilla. */
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Atiende la excepción de recurso inexistente (id válido pero sin
     * registro en la base): debe verse como una página limpia, no como
     * un error 500.
     */
    @ExceptionHandler(RecursoNoEncontradoException.class)
    public String manejarNoEncontrado(RecursoNoEncontradoException excepcion, Model model) {
        log.warn(excepcion.getMessage());
        model.addAttribute("mensaje", excepcion.getMessage());
        return "error";
    }

    /**
     * Red de seguridad para cualquier otro error no contemplado: en vez
     * del whitelabel, se muestra la misma página con un mensaje genérico.
     */
    @ExceptionHandler(Exception.class)
    public String manejarGenerico(Exception excepcion, Model model) {
        log.error("Error no contemplado", excepcion);
        model.addAttribute("mensaje",
                "Ocurrió un error inesperado en la aplicación. Intenta de nuevo.");
        return "error";
    }
}

package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vetopia.entities.Mascota;
import com.vetopia.service.MascotaService;

/**
 * CAPA CONTROLLER - Portal del Cliente
 * Gestiona las peticiones del usuario con @Controller y @RequestMapping,
 * delega la lógica de negocio en el Service y retorna los nombres de las
 * vistas que Thymeleaf renderiza. No contiene lógica de negocio ni acceso
 * a datos.
 *
 * Flujo estricto de comunicación: Controller -> Service -> Repository.
 */
@Controller
@RequestMapping("/mascotas")
public class MascotaController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    private final MascotaService mascotaService;

    /**
     * Constructor con inyección por constructor: Spring entrega
     * automáticamente el bean @Service que implementa MascotaService.
     * El controlador nunca accede al repositorio directamente.
     *
     * @param mascotaService servicio de mascotas inyectado por Spring.
     */
    @Autowired
    public MascotaController(MascotaService mascotaService) {
        this.mascotaService = mascotaService;
    }

    /**
     * Atiende GET /mascotas: vista principal del cliente con el listado
     * de mascotas ordenadas alfabéticamente por nombre.
     *
     * URL para visualizar: http://localhost:8080/mascotas
     * Vista renderizada:   src/main/resources/templates/principal-cliente.html
     *
     * @param model modelo enviado a la vista Thymeleaf.
     * @return nombre de la vista {@code principal-cliente}.
     */
    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        return "principal-cliente";
    }

    /**
     * Atiende GET /mascotas/detalle?id=N: vista de detalle de una mascota.
     * El id llega como parámetro de consulta, igual que en la versión
     * JavaScript original.
     *
     * URL para visualizar: http://localhost:8080/mascotas/detalle?id=2
     * (sin id, con id inexistente o inválido se muestra el panel
     * "Mascota no encontrada").
     * Vista renderizada:   src/main/resources/templates/detalle-mascota.html
     *
     * @param id identificador de la mascota a consultar.
     * @param model modelo enviado a la vista Thymeleaf; contiene la mascota
     *              o el mensaje de error para el panel "no encontrada".
     * @return nombre de la vista {@code detalle-mascota}.
     */
    @GetMapping("/detalle")
    public String verDetalle(@RequestParam(name = "id", required = false) Integer id, Model model) {
        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            if (mascota == null) {
                model.addAttribute("mensajeError",
                        "No encontramos ninguna mascota registrada con el identificador \""
                                + (id == null ? "" : id) + "\".");
            } else {
                model.addAttribute("mascota", mascota);
            }
        } catch (IllegalArgumentException excepcion) {
            // Id nulo o fuera de rango: se muestra el panel informativo.
            model.addAttribute("mensajeError", excepcion.getMessage());
        }
        return "detalle-mascota";
    }

    /**
     * Maneja ids no numéricos (por ejemplo ?id=abc): Spring no puede
     * convertirlos a Integer y lanzaría un 400; en su lugar se renderiza
     * el panel "Mascota no encontrada" con un mensaje amigable.
     *
     * @param modelo modelo enviado a la vista Thymeleaf.
     * @return nombre de la vista {@code detalle-mascota}.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "detalle-mascota";
    }
}

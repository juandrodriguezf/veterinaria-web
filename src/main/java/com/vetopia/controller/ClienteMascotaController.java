package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.service.DuenoService;
import com.vetopia.service.MascotaService;

/**
 * CONTROLLER (Portal del Cliente)
 * Responsabilidad única: gestiona las peticiones del portal del cliente.
 * Todas las rutas viven bajo el contexto /cliente/mascotas. El listado
 * usa la plantilla principal (src/main/resources/templates/principal-cliente.html)
 * y el detalle usa src/main/resources/templates/cliente/detalle-mascota.html.
 *
 * El controller delega la lógica de negocio en el Service (MascotaService)
 * y solo retorna los nombres de las vistas que Thymeleaf renderiza.
 * Flujo estricto: Controller -> Service -> Repository.
 *
 * La identidad del cliente llega como parámetro de consulta idUsuario
 * (el login redirige a /cliente/mascotas?idUsuario=N).
 * 
 */
@Controller
@RequestMapping("/cliente/mascotas")
public class ClienteMascotaController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de dueños (para resolver el cliente logueado). */
    @Autowired
    private DuenoService duenoService;

    /**
     * Atiende GET /cliente/mascotas?idUsuario=N: listado de las mascotas
     * del cliente identificado (relación Dueno 1 -- 0..* Mascota). Si no
     * llega idUsuario se regresa al login.
     *
     * URL para visualizar: http://localhost:8080/cliente/mascotas?idUsuario=1
     * Vista: src/main/resources/templates/principal-cliente.html
     */
    @GetMapping
    public String listarMascotas(@RequestParam(name = "idUsuario", required = false) Integer idUsuario,
                                 Model model) {
        if (idUsuario == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.obtenerDuenoPorId(idUsuario);
        if (dueno == null) {
            return "redirect:/login";
        }
        model.addAttribute("dueno", dueno);
        model.addAttribute("idUsuario", idUsuario);
        model.addAttribute("mascotas", mascotaService.listarMascotasPorDueno(idUsuario));
        return "principal-cliente";
    }

    /**
     * Atiende GET /cliente/mascotas/detalle?id=N&idUsuario=M: vista de
     * detalle de una mascota propia. Valida que la mascota pertenezca al
     * cliente identificado (aislamiento de datos entre clientes).
     *
     * URL para visualizar: http://localhost:8080/cliente/mascotas/detalle?id=2&idUsuario=1
     * Vista: src/main/resources/templates/cliente/detalle-mascota.html
     */
    @GetMapping("/detalle")
    public String verDetalle(@RequestParam(name = "id", required = false) Integer id,
                             @RequestParam(name = "idUsuario", required = false) Integer idUsuario,
                             Model model) {
        if (idUsuario == null) {
            return "redirect:/login";
        }
        Dueno dueno = duenoService.obtenerDuenoPorId(idUsuario);
        if (dueno == null) {
            return "redirect:/login";
        }
        model.addAttribute("dueno", dueno);
        model.addAttribute("idUsuario", idUsuario);

        try {
            Mascota mascota = mascotaService.obtenerMascotaPorId(id);
            if (mascota == null) {
                model.addAttribute("mensajeError",
                        "No encontramos ninguna mascota registrada con el identificador \""
                                + (id == null ? "" : id) + "\".");
            } else if (!idUsuario.equals(mascota.getDuenoId())) {
                model.addAttribute("mensajeError", "Esta mascota no está registrada a tu nombre.");
            } else {
                model.addAttribute("mascota", mascota);
            }
        } catch (IllegalArgumentException excepcion) {
            // Id nulo o fuera de rango: se muestra el panel informativo.
            model.addAttribute("mensajeError", excepcion.getMessage());
        }
        return "cliente/detalle-mascota";
    }

    /**
     * Maneja ids no numéricos (id=abc) en el portal del cliente: Spring
     * no puede convertirlos a Integer y lanzaría un 400; en su lugar se
     * renderiza el panel "Mascota no encontrada" con un mensaje amigable.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "cliente/detalle-mascota";
    }
}

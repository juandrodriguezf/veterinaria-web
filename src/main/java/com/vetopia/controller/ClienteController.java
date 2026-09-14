package com.vetopia.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vetopia.entities.Dueno;
import com.vetopia.service.DuenoService;
import com.vetopia.service.MascotaService;

/**
 * CONTROLLER (Portal del cliente)
 * Gestiona las rutas del portal del cliente. Un controlador por lógica
 * de portal: las rutas del veterinario viven en VeterinarioController
 * y el flujo público (login) en HomeController, de modo que cada
 * controlador atiende un solo dominio de la aplicación.
 *
 * El controller es delgado: delega toda la lógica de negocio en los
 * Services y solo resuelve la navegación (vistas y redirects). Las
 * reglas (identidad del cliente, estado Activo, propiedad de la
 * mascota) viven en la capa Service, que las comunica lanzando
 * IllegalArgumentException (dato inválido) o IllegalStateException
 * (regla de negocio incumplida); el controller las atrapa y trabaja
 * con ellas: muestra el panel informativo o redirige al login.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 *
 * Rutas del portal del cliente:
 * - GET /cliente/mascotas              -> listado (requiere idUsuario)
 * - GET /cliente/mascotas/detalle?id=N -> detalle de una mascota propia
 */
@Controller
public class ClienteController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de dueños (identidad del cliente). */
    @Autowired
    private DuenoService duenoService;

    /**
     * Atiende GET /cliente/mascotas?idUsuario=N: listado de las mascotas
     * del cliente identificado (relación Dueno 1 -- 0..* Mascota). El
     * service valida la identidad (obtenerActivo lanza si el id falta,
     * es inválido o el cliente está inactivo); el controller atrapa la
     * excepción y regresa al login.
     *
     * URL para visualizar: http://localhost:8080/cliente/mascotas?idUsuario=1
     * Vista: src/main/resources/templates/principal-cliente.html
     */
    @GetMapping("/cliente/mascotas")
    public String listarMascotasCliente(@RequestParam(name = "idUsuario", required = false) Integer idUsuario,
                                        Model model) {
        return mostrarMascotasCliente(idUsuario, null, model);
    }

    /**
     * Atiende GET /cliente/mascotas?idUsuario=N&nombre=X: búsqueda de las
     * mascotas del cliente por nombre (AC21). Misma ruta del listado con
     * el parámetro nombre, patrón del material del curso (un handler por
     * caso, igual que buscarEstudiantesPorNombre con params = "nombre").
     *
     * URL para visualizar: http://localhost:8080/cliente/mascotas?idUsuario=1&nombre=Max
     * Vista: src/main/resources/templates/principal-cliente.html
     */
    @GetMapping(value = "/cliente/mascotas", params = "nombre")
    public String buscarMascotasCliente(@RequestParam(name = "idUsuario", required = false) Integer idUsuario,
                                        @RequestParam("nombre") String nombre,
                                        Model model) {
        return mostrarMascotasCliente(idUsuario, nombre, model);
    }

    /**
     * Resuelve el listado del portal del cliente para los dos handlers
     * anteriores: valida la identidad del cliente y deja en el modelo las
     * mascotas (todas o filtradas por nombre, según llegue el término).
     * Sin identidad válida no hay nada que mostrar y se regresa al login.
     */
    private String mostrarMascotasCliente(Integer idUsuario, String nombre, Model model) {
        try {
            Dueno dueno = duenoService.obtenerActivo(idUsuario);
            model.addAttribute("dueno", dueno);
            model.addAttribute("idUsuario", idUsuario);
            model.addAttribute("mascotas", mascotaService.buscarPorDuenoYNombre(idUsuario, nombre));
            model.addAttribute("nombre", nombre);
            return "principal-cliente";
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // Sin idUsuario, con id inválido o con un cliente inactivo no
            // hay nada que mostrar: la salida segura es volver al login.
            return "redirect:/login";
        }
    }

    /**
     * Atiende GET /cliente/mascotas/detalle?id=N&idUsuario=M: vista de
     * detalle de una mascota propia. La identidad del cliente se valida
     * en el service (igual que el listado) y la mascota se pide con
     * obtenerPropia, que lanza si el id es inválido, no existe o es de
     * otro dueño; esos casos muestran el panel informativo de la vista.
     *
     * URL para visualizar: http://localhost:8080/cliente/mascotas/detalle?id=2&idUsuario=1
     * Vista: src/main/resources/templates/cliente/detalle-mascota.html
     */
    @GetMapping("/cliente/mascotas/detalle")
    public String verDetalle(@RequestParam(name = "id", required = false) Integer id,
                             @RequestParam(name = "idUsuario", required = false) Integer idUsuario,
                             Model model) {
        Dueno dueno;
        try {
            dueno = duenoService.obtenerActivo(idUsuario);
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // La identidad llegó ausente o inválida, o el cliente está
            // inactivo: no se consulta nada y se devuelve al login.
            return "redirect:/login";
        }
        model.addAttribute("dueno", dueno);
        model.addAttribute("idUsuario", idUsuario);
        try {
            model.addAttribute("mascota", mascotaService.obtenerPropia(id, idUsuario));
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // Id inválido, mascota inexistente o de otro dueño: el service
            // ya produjo el mensaje, aquí solo se muestra en el panel.
            model.addAttribute("mensajeError", excepcion.getMessage());
        }
        return "cliente/detalle-mascota";
    }

    /**
     * Maneja ids no numéricos (id=abc) en las rutas del portal del
     * cliente: Spring no puede convertirlos a Integer y lanzaría un
     * 400; en su lugar se renderiza el panel informativo del detalle.
     * Al vivir el handler en un controlador de un solo portal, la vista
     * es fija y no hace falta inspeccionar el método que originó la
     * excepción (HandlerMethod), como se hacía en el controller unificado.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "cliente/detalle-mascota";
    }
}

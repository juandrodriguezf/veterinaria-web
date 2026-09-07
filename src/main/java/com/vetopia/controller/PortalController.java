package com.vetopia.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.service.DrogaService;
import com.vetopia.service.DuenoService;
import com.vetopia.service.MascotaService;
import com.vetopia.service.TratamientoService;

/**
 * CONTROLLER (Portal único)
 * Gestiona las rutas de los dos portales de la aplicación (cliente y
 * veterinario) en una sola clase, según la recomendación de reducir el
 * número de controladores. El archivo se organiza en dos secciones
 * claramente delimitadas para conservar la legibilidad.
 *
 * El controller es delgado: delega toda la lógica de negocio en los
 * Services y solo resuelve la navegación (vistas y redirects). Las
 * reglas (propiedad de la mascota, estado Activo, dueño obligatorio,
 * stock de drogas, cascada de borrado) viven en la capa Service, que
 * las comunica lanzando IllegalArgumentException (dato inválido) o
 * IllegalStateException (regla de negocio incumplida); el controller
 * las atrapa y trabaja con ellas: muestra el panel informativo, regresa
 * al formulario o redirige al login según el caso.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 *
 * Rutas del portal del cliente:
 * - GET  /cliente/mascotas                 -> listado (requiere idUsuario)
 * - GET  /cliente/mascotas/detalle?id=N    -> detalle de una mascota propia
 *
 * Rutas del portal del veterinario:
 * - GET  /veterinario/mascotas                      -> listar mascotas
 * - GET  /veterinario/mascotas/registrar-mascota    -> formulario crear
 * - POST /veterinario/mascotas/guardar-mascota      -> crea o actualiza
 * - GET  /veterinario/mascotas/editar-mascota?id=N  -> form precargado
 * - GET  /veterinario/mascotas/desactivar-mascota   -> alterna estado
 * - GET  /veterinario/mascotas/clientes             -> listar dueños
 * - GET  /veterinario/mascotas/clientes/crear       -> form crear dueño
 * - GET  /veterinario/mascotas/clientes/editar?id=N -> form precargado
 * - POST /veterinario/mascotas/clientes/guardar     -> crea o actualiza
 * - GET  /veterinario/mascotas/clientes/desactivar  -> alterna estado
 * - GET  /veterinario/mascotas/clientes/eliminar?id -> borra en cascada
 * - GET  /veterinario/mascotas/registrar-cliente    -> flujo original
 * - POST /veterinario/mascotas/registrar-cliente    -> flujo original
 * - GET  /veterinario/mascotas/asignar-tratamiento  -> form tratamiento
 * - POST /veterinario/mascotas/asignar-tratamiento  -> asigna y descuenta
 * - GET  /veterinario/mascotas/ficha?id=N           -> ficha clínica
 * - POST /veterinario/mascotas/ficha/estado         -> toggle de la ficha
 */
@Controller
public class PortalController {

    /** Logger ayuda a identificar errores de manera sencilla. */
    private static final Logger log = LoggerFactory.getLogger(PortalController.class);

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de dueños (identidad del cliente y CRUD de clientes). */
    @Autowired
    private DuenoService duenoService;

    /** Servicio de tratamientos (asignación de tratamientos). */
    @Autowired
    private TratamientoService tratamientoService;

    /** Servicio de drogas (medicamentos del inventario). */
    @Autowired
    private DrogaService drogaService;

    // =====================================================================
    // PORTAL DEL CLIENTE (plantillas en templates/ y templates/cliente/)
    // =====================================================================

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
        try {
            Dueno dueno = duenoService.obtenerActivo(idUsuario);
            model.addAttribute("dueno", dueno);
            model.addAttribute("idUsuario", idUsuario);
            model.addAttribute("mascotas", mascotaService.listarMascotasPorDueno(idUsuario));
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

    // =====================================================================
    // PORTAL DEL VETERINARIO (plantillas en templates/veterinario/)
    // =====================================================================

    /**
     * Atiende GET /veterinario/mascotas: listado de mascotas a cargo
     * del veterinario.
     *
     * URL: http://localhost:8080/veterinario/mascotas
     * Vista: src/main/resources/templates/veterinario/mascotas-cargo.html
     */
    @GetMapping("/veterinario/mascotas")
    public String listarMascotasVeterinario(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        return "veterinario/mascotas-cargo";
    }

    /**
     * Atiende GET /veterinario/mascotas/registrar-mascota: formulario de
     * creación de mascota. Se envía una Mascota vacía (id null) y el
     * listado de dueños para el select; el mismo formulario se reutiliza
     * para editar.
     *
     * URL: http://localhost:8080/veterinario/mascotas/registrar-mascota
     * Vista: src/main/resources/templates/veterinario/mascota-form.html
     */
    @GetMapping("/veterinario/mascotas/registrar-mascota")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("duenos", duenoService.listarDuenos());
        return "veterinario/mascota-form";
    }

    /**
     * Atiende POST /veterinario/mascotas/guardar-mascota: crea o actualiza
     * la mascota capturada con @ModelAttribute (los name del formulario
     * coinciden con los atributos, th:field los genera). Las reglas
     * (estado inicial "Activo" y dueño obligatorio) las aplica el service
     * en guardarValidada; si lanza, el controller regresa al formulario.
     */
    @PostMapping("/veterinario/mascotas/guardar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        try {
            mascotaService.guardarValidada(mascota);
            log.info(mascota.getId() + " - " + mascota.getNombre() + " (duenoId=" + mascota.getDueno().getId() + ")");
            return "redirect:/veterinario/mascotas";
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // El service rechazó el guardado (mascota sin dueño o sin
            // datos): se regresa al mismo formulario para corregir.
            log.warn(excepcion.getMessage());
            return mascota.getId() != null
                    ? "redirect:/veterinario/mascotas/editar-mascota?id=" + mascota.getId()
                    : "redirect:/veterinario/mascotas/registrar-mascota";
        }
    }

    /**
     * Atiende GET /veterinario/mascotas/editar-mascota?id=N: carga la
     * mascota por id y muestra el formulario con los datos precargados.
     * Si el service lanza por un id inválido, el panel informativo de la
     * ficha lo comunica.
     *
     * URL: http://localhost:8080/veterinario/mascotas/editar-mascota?id=1
     */
    @GetMapping("/veterinario/mascotas/editar-mascota")
    public String editarMascota(@RequestParam("id") Integer id, Model model) {
        try {
            model.addAttribute("mascota", mascotaService.obtenerMascotaPorId(id));
        } catch (IllegalArgumentException excepcion) {
            // Id inválido (ausente, cero o negativo): la ficha muestra
            // su panel informativo con la causa exacta.
            model.addAttribute("mensajeError", excepcion.getMessage());
            return "veterinario/ficha-clinica";
        }
        model.addAttribute("duenos", duenoService.listarDuenos());
        return "veterinario/mascota-form";
    }

    /**
     * Atiende GET /veterinario/mascotas/desactivar-mascota?id=N: alterna
     * el estado de la mascota (Activo <-> Inactivo) y redirige al listado.
     * Es el "borrado lógico" definido en el diagrama de clases
     * (Mascota.activar()/desactivar()); la regla vive en el service.
     *
     * URL: http://localhost:8080/veterinario/mascotas/desactivar-mascota?id=1
     */
    @GetMapping("/veterinario/mascotas/desactivar-mascota")
    public String desactivarMascota(@RequestParam("id") Integer id) {
        try {
            log.info("Estado de la mascota " + id + " actualizado a: " + mascotaService.alternarEstado(id));
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // El service lanza si el id es inválido: se registra la causa
            // y se vuelve al listado sin alterar ningún registro.
            log.warn(excepcion.getMessage());
        }
        return "redirect:/veterinario/mascotas";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes: listado de dueños
     * registrados con sus acciones de editar/desactivar.
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes
     * Vista: src/main/resources/templates/veterinario/clientes.html
     */
    @GetMapping("/veterinario/mascotas/clientes")
    public String listarClientes(Model model) {
        model.addAttribute("duenos", duenoService.listarDuenos());
        return "veterinario/clientes";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/crear: formulario de
     * creación de cliente con un Dueno vacío (id null).
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/crear
     * Vista: src/main/resources/templates/veterinario/cliente-form.html
     */
    @GetMapping("/veterinario/mascotas/clientes/crear")
    public String crearCliente(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "veterinario/cliente-form";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/editar?id=N: carga el
     * dueño por id y muestra el mismo formulario precargado; un id
     * inválido regresa al listado de clientes.
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/editar?id=1
     */
    @GetMapping("/veterinario/mascotas/clientes/editar")
    public String editarCliente(@RequestParam("id") Integer id, Model model) {
        try {
            model.addAttribute("dueno", duenoService.obtenerDuenoPorId(id));
        } catch (IllegalArgumentException excepcion) {
            // Id inválido: no hay formulario que precargar y se regresa
            // al listado de clientes.
            log.warn(excepcion.getMessage());
            return "redirect:/veterinario/mascotas/clientes";
        }
        return "veterinario/cliente-form";
    }

    /**
     * Atiende POST /veterinario/mascotas/clientes/guardar: crea o actualiza
     * el dueño capturado con @ModelAttribute (incluye la contraseña para
     * que el cliente pueda iniciar sesión).
     */
    @PostMapping("/veterinario/mascotas/clientes/guardar")
    public String guardarCliente(@ModelAttribute Dueno dueno) {
        log.info(dueno.getId() + " - " + dueno.getNombre());
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/clientes";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/desactivar?id=N: alterna
     * el estado del dueño (Activo <-> Inactivo) y redirige al listado.
     * Es el "borrado lógico" definido en el diagrama de clases
     * (Dueno.activar()/desactivar()); la regla vive en el service.
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/desactivar?id=1
     */
    @GetMapping("/veterinario/mascotas/clientes/desactivar")
    public String desactivarCliente(@RequestParam("id") Integer id) {
        try {
            log.info("Estado del cliente " + id + " actualizado a: " + duenoService.alternarEstado(id));
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // El service lanza si el id es inválido: se registra la causa
            // y se vuelve al listado sin alterar ningún registro.
            log.warn(excepcion.getMessage());
        }
        return "redirect:/veterinario/mascotas/clientes";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/eliminar?id=N: borra
     * definitivamente al dueño (borrado físico, a diferencia del borrado
     * lógico de desactivar). La cascada (mascotas primero, dueño al
     * final) la ejecuta el service en eliminarEnCascada.
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/eliminar?id=3
     */
    @GetMapping("/veterinario/mascotas/clientes/eliminar")
    public String eliminarCliente(@RequestParam("id") Integer id) {
        try {
            duenoService.eliminarEnCascada(id);
            log.info("Dueño eliminado en cascada: id=" + id);
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // El service lanza si el id falta; la cascada no se ejecuta
            // parcialmente y simplemente se vuelve al listado.
            log.warn(excepcion.getMessage());
        }
        return "redirect:/veterinario/mascotas/clientes";
    }

    /**
     * Atiende GET /veterinario/mascotas/registrar-cliente: formulario del
     * flujo original para registrar un nuevo cliente.
     */
    @GetMapping("/veterinario/mascotas/registrar-cliente")
    public String registrarCliente(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    /**
     * Atiende POST /veterinario/mascotas/registrar-cliente: guarda el
     * cliente del flujo original y continúa al registro de su mascota.
     */
    @PostMapping("/veterinario/mascotas/registrar-cliente")
    public String guardarClienteFlujoOriginal(@ModelAttribute Dueno dueno) {
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/registrar-mascota";
    }

    /**
     * Atiende GET /veterinario/mascotas/asignar-tratamiento: formulario
     * del veterinario para asignar un tratamiento/medicamento a una mascota.
     */
    @GetMapping("/veterinario/mascotas/asignar-tratamiento")
    public String asignarTratamiento(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        return "veterinario/asignar-tratamiento";
    }

    /**
     * Atiende POST /veterinario/mascotas/asignar-tratamiento: delega en
     * el service la asignación (validaciones, registro y descuento de
     * inventario) y llena la vista de confirmación con lo que devuelve;
     * si el service lanza, regresa al formulario mostrando el mensaje.
     */
    @PostMapping("/veterinario/mascotas/asignar-tratamiento")
    public String aplicarTratamiento(@ModelAttribute Tratamiento tratamiento, Model model) {
        try {
            Droga droga = tratamientoService.asignar(tratamiento);
            model.addAttribute("unidadesDescontadas", 1);
            model.addAttribute("medicamentoNombre", droga.getNombre());
            model.addAttribute("stockRestante", droga.getUnidadesDisponibles());
            model.addAttribute("mascotaId", tratamiento.getMascota().getId());
        } catch (IllegalArgumentException | IllegalStateException excepcion) {
            // El service valida mascota, medicamento y stock: si algo
            // falla, el mensaje regresa al formulario para que el
            // veterinario lo vea y corrija.
            model.addAttribute("mensajeError", excepcion.getMessage());
            return "veterinario/asignar-tratamiento";
        }
        return "veterinario/tratamiento-confirmacion";
    }

    /**
     * Atiende GET /veterinario/mascotas/ficha?id=N: ficha clínica de una
     * mascota para el portal del veterinario. El service lanza si el id
     * es inválido; si no existe la mascota, el panel informativo se
     * muestra con el mensaje por defecto de la vista.
     */
    @GetMapping("/veterinario/mascotas/ficha")
    public String verFichaClinica(@RequestParam(name = "id", required = false) Integer id, Model model) {
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
            // Id nulo o fuera de rango: el service aporta el mensaje y
            // la ficha lo presenta en su panel informativo.
            model.addAttribute("mensajeError", excepcion.getMessage());
        }
        return "veterinario/ficha-clinica";
    }

    /**
     * Atiende POST /veterinario/mascotas/ficha/estado: cambia el estado
     * (Activo/Inactivo) de una mascota desde el toggle de la ficha.
     */
    @PostMapping("/veterinario/mascotas/ficha/estado")
    public String cambiarEstado(@RequestParam("id") Integer id, @RequestParam("estado") String estado) {
        mascotaService.cambiarEstado(id, estado);
        return "redirect:/veterinario/mascotas/ficha?id=" + id;
    }

    /**
     * Maneja ids no numéricos (id=abc) en cualquier ruta del portal:
     * Spring no puede convertirlos a Integer y lanzaría un 400; en su
     * lugar se renderiza el panel informativo. La vista se elige según
     * el método que originó la excepción (HandlerMethod): ficha clínica
     * para las rutas del veterinario y detalle para las del cliente.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(HandlerMethod origen, Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        // El método que originó la excepción identifica el portal: cada
        // uno tiene su propia plantilla con panel informativo.
        String metodo = origen.getMethod().getName();
        boolean delVeterinario = metodo.equals("verFichaClinica") || metodo.equals("editarMascota")
                || metodo.equals("editarCliente") || metodo.equals("desactivarMascota")
                || metodo.equals("desactivarCliente") || metodo.equals("eliminarCliente");
        return delVeterinario ? "veterinario/ficha-clinica" : "cliente/detalle-mascota";
    }
}

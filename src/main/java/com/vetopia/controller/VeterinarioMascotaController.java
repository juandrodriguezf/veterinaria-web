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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
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
 * CONTROLLER (Portal del Veterinario)
 * Responsabilidad única: gestiona las peticiones del portal del veterinario.
 * Todas las rutas viven bajo el contexto /veterinario/mascotas y las vistas
 * renderizadas se ubican en src/main/resources/templates/veterinario/.
 *
 * El controller delega la lógica de negocio en los Services y solo retorna
 * los nombres de las vistas que Thymeleaf renderiza.
 * Flujo estricto: Controller -> Service -> Repository.
 *
 * CRUD completo :
 * - GET /                      -> listar mascotas
 * - GET /registrar-mascota     -> formulario crear (reutilizado para editar)
 * - POST /guardar-mascota      -> crea o actualiza (@ModelAttribute)
 * - GET /editar-mascota?id=N   -> carga la mascota y muestra el mismo form
 * - GET /desactivar-mascota?id -> alterna Activo/Inactivo y redirige
 * - GET /clientes              -> listar dueños
 * - GET /clientes/crear        -> formulario crear dueño
 * - GET /clientes/editar?id=N  -> carga el dueño y muestra el mismo form
 * - POST /clientes/guardar     -> crea o actualiza dueño
 * - GET /clientes/desactivar?id-> alterna Activo/Inactivo del dueño
 */
@Controller
@RequestMapping("/veterinario/mascotas")
public class VeterinarioMascotaController {

    /** Logger ayuda a identificar errores de manera sencilla . */
    private static final Logger log = LoggerFactory.getLogger(VeterinarioMascotaController.class);

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de dueños (CRUD de clientes). */
    @Autowired
    private DuenoService duenoService;

    /** Servicio de tratamientos (asignación de tratamientos). */
    @Autowired
    private TratamientoService tratamientoService;

    /** Servicio de drogas (medicamentos del inventario). */
    @Autowired
    private DrogaService drogaService;

    

    /**
     * Atiende GET /veterinario/mascotas: listado de mascotas a cargo
     * del veterinario.
     *
     * URL: http://localhost:8080/veterinario/mascotas
     * Vista: src/main/resources/templates/veterinario/mascotas-cargo.html
     */
    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        return "veterinario/mascotas-cargo";
    }

    /**
     * Atiende GET /veterinario/mascotas/registrar-mascota: formulario de
     * creación de mascota. Se envía una Mascota vacía (id null) y el
     * listado de dueños para el select; el mismo formulario se reutiliza
     * para editar .
     *
     * URL: http://localhost:8080/veterinario/mascotas/registrar-mascota
     * Vista: src/main/resources/templates/veterinario/mascota-form.html
     */
    @GetMapping("/registrar-mascota")
    public String mostrarFormularioCrear(Model model) {
        model.addAttribute("mascota", new Mascota());
        model.addAttribute("duenos", duenoService.listarDuenos());
        return "veterinario/mascota-form";
    }

    /**
     * Atiende POST /veterinario/mascotas/guardar-mascota: crea o actualiza
     * la mascota. Spring captura los datos con @ModelAttribute (los name
     * del formulario coinciden con los atributos, th:field los genera).
     * Si el id llega null es creación (estado inicial "Activo"); si llega
     * con valor, el repositorio actualiza el registro existente.
     */
    @PostMapping("/guardar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        if (mascota.getId() == null) {
            mascota.setEstado("Activo");
        }
        log.info(mascota.getId() + " - " + mascota.getNombre() + " (duenoId=" + mascota.getDuenoId() + ")");
        mascotaService.guardar(mascota);
        return "redirect:/veterinario/mascotas";
    }

    /**
     * Atiende GET /veterinario/mascotas/editar-mascota?id=N: carga la
     * mascota por id y muestra el mismo formulario de creación con los
     * datos precargados .
     *
     * URL: http://localhost:8080/veterinario/mascotas/editar-mascota?id=1
     */
    @GetMapping("/editar-mascota")
    public String editarMascota(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("mascota", mascotaService.obtenerMascotaPorId(id));
        model.addAttribute("duenos", duenoService.listarDuenos());
        return "veterinario/mascota-form";
    }

    /**
     * Atiende GET /veterinario/mascotas/desactivar-mascota?id=N: alterna el
     * estado de la mascota (Activo <-> Inactivo) y redirige al listado.
     * Es el "borrado lógico" definido en el diagrama de clases
     * (Mascota.activar()/desactivar()).
     *
     * URL: http://localhost:8080/veterinario/mascotas/desactivar-mascota?id=1
     */
    @GetMapping("/desactivar-mascota")
    public String desactivarMascota(@RequestParam("id") Integer id) {
        Mascota mascota = mascotaService.obtenerMascotaPorId(id);
        if (mascota != null) {
            String nuevoEstado = "Inactivo".equals(mascota.getEstado()) ? "Activo" : "Inactivo";
            mascotaService.cambiarEstado(id, nuevoEstado);
            log.info(mascota.getNombre() + " -> " + nuevoEstado);
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
    @GetMapping("/clientes")
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
    @GetMapping("/clientes/crear")
    public String crearCliente(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "veterinario/cliente-form";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/editar?id=N: carga el
     * dueño por id y muestra el mismo formulario de creación precargado.
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/editar?id=1
     */
    @GetMapping("/clientes/editar")
    public String editarCliente(@RequestParam("id") Integer id, Model model) {
        model.addAttribute("dueno", duenoService.obtenerDuenoPorId(id));
        return "veterinario/cliente-form";
    }

    /**
     * Atiende POST /veterinario/mascotas/clientes/guardar: crea o actualiza
     * el dueño capturado con @ModelAttribute (incluye la contraseña para
     * que el cliente pueda iniciar sesión).
     */
    @PostMapping("/clientes/guardar")
    public String guardarCliente(@ModelAttribute Dueno dueno) {
        log.info(dueno.getId() + " - " + dueno.getNombre());
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/clientes";
    }

    /**
     * Atiende GET /veterinario/mascotas/clientes/desactivar?id=N: alterna el
     * estado del dueño (Activo <-> Inactivo) y redirige al listado. Es el
     * "borrado lógico" definido en el diagrama de clases
     * (Dueno.activar()/desactivar()).
     *
     * URL: http://localhost:8080/veterinario/mascotas/clientes/desactivar?id=1
     */
    @GetMapping("/clientes/desactivar")
    public String desactivarCliente(@RequestParam("id") Integer id) {
        Dueno dueno = duenoService.obtenerDuenoPorId(id);
        if (dueno != null) {
            String nuevoEstado = "Inactivo".equals(dueno.getEstado()) ? "Activo" : "Inactivo";
            duenoService.cambiarEstado(id, nuevoEstado);
            log.info(dueno.getNombre() + " -> " + nuevoEstado);
        }
        return "redirect:/veterinario/mascotas/clientes";
    }

    

    /**
     * Atiende GET /veterinario/mascotas/registrar-cliente: formulario del
     * flujo original para registrar un nuevo cliente.
     */
    @GetMapping("/registrar-cliente")
    public String registrarCliente(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    /**
     * Atiende POST /veterinario/mascotas/registrar-cliente: guarda el
     * cliente del flujo original y continúa al registro de su mascota.
     */
    @PostMapping("/registrar-cliente")
    public String guardarClienteFlujoOriginal(@ModelAttribute Dueno dueno) {
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/registrar-mascota";
    }

 

    /**
     * Atiende GET /veterinario/mascotas/asignar-tratamiento: formulario
     * del veterinario para asignar un tratamiento/medicamento a una mascota.
     */
    @GetMapping("/asignar-tratamiento")
    public String asignarTratamiento(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        return "veterinario/asignar-tratamiento";
    }

    /**
     * Atiende POST /veterinario/mascotas/asignar-tratamiento: guarda el
     * tratamiento, descuenta una unidad de la droga del inventario y
     * redirige a la pantalla de confirmación.
     */
    @PostMapping("/asignar-tratamiento")
    public String aplicarTratamiento(@ModelAttribute Tratamiento tratamiento, Model model) {
        tratamiento.setVeterinarioId(1);
        tratamientoService.guardar(tratamiento);

        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDrogaId());
        int unidadesDescontadas = 1;
        int stockRestante = (droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles())
                - unidadesDescontadas;

        model.addAttribute("unidadesDescontadas", unidadesDescontadas);
        model.addAttribute("medicamentoNombre", droga.getNombre());
        model.addAttribute("stockRestante", stockRestante);
        model.addAttribute("mascotaId", tratamiento.getMascotaId());
        return "veterinario/tratamiento-confirmacion";
    }



    /**
     * Atiende GET /veterinario/mascotas/ficha?id=N: ficha clínica de una
     * mascota para el portal del veterinario.
     */
    @GetMapping("/ficha")
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
            // Id nulo o fuera de rango: se muestra el panel informativo.
            model.addAttribute("mensajeError", excepcion.getMessage());
        }
        return "veterinario/ficha-clinica";
    }

    /**
     * Atiende POST /veterinario/mascotas/ficha/estado: cambia el estado
     * (Activo/Inactivo) de una mascota desde el toggle de la ficha.
     */
    @PostMapping("/ficha/estado")
    public String cambiarEstado(@RequestParam("id") Integer id, @RequestParam("estado") String estado) {
        mascotaService.cambiarEstado(id, estado);
        return "redirect:/veterinario/mascotas/ficha?id=" + id;
    }

    /**
     * Maneja ids no numéricos (id=abc) en las rutas del veterinario:
     * Spring no puede convertirlos a Integer y lanzaría un 400; en su
     * lugar se renderiza la ficha con el panel informativo.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "veterinario/ficha-clinica";
    }
}

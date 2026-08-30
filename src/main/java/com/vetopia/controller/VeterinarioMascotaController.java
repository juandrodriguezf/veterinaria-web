package com.vetopia.controller;

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
 * El controller delega la lógica de negocio en el Service (MascotaService)
 * y solo retorna los nombres de las vistas que Thymeleaf renderiza.
 * Flujo estricto: Controller -> Service -> Repository.
 */
@Controller
@RequestMapping("/veterinario/mascotas")
public class VeterinarioMascotaController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Servicio de dueños (registro de nuevos clientes). */
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
     * URL para visualizar: http://localhost:8080/veterinario/mascotas
     * Vista: src/main/resources/templates/veterinario/mascotas-cargo.html
     */
    @GetMapping
    public String listarMascotas(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        return "veterinario/mascotas-cargo";
    }

    /**
     * Atiende GET /veterinario/mascotas/registrar-cliente: formulario
     * del veterinario para registrar un nuevo cliente y su mascota.
     *
     * URL: http://localhost:8080/veterinario/mascotas/registrar-cliente
     * Vista: src/main/resources/templates/veterinario/registrar-cliente.html
     */
    @GetMapping("/registrar-cliente")
    public String registrarCliente(Model model) {
        model.addAttribute("dueno", new Dueno());
        return "veterinario/registrar-cliente";
    }

    /**
     * Atiende POST /veterinario/mascotas/registrar-cliente: procesa el
     * formulario de registro de un nuevo cliente. Spring captura los datos
     * del cuerpo de la petición con @ModelAttribute y los mapea a un objeto
     * Dueno (los name del formulario coinciden con sus atributos).
     *
     * @ModelAttribute es legal porque la URL es la misma que la del GET
     * (muestra el form) pero con método HTTP distinto (procesa los datos).
     */
    @PostMapping("/registrar-cliente")
    public String guardarCliente(@ModelAttribute Dueno dueno) {
        duenoService.guardar(dueno);
        return "redirect:/veterinario/mascotas/registrar-mascota";
    }

    /**
     * Atiende GET /veterinario/mascotas/registrar-mascota: formulario
     * del veterinario para registrar una mascota de un cliente recién
     * registrado.
     *
     * URL: http://localhost:8080/veterinario/mascotas/registrar-mascota
     * Vista: src/main/resources/templates/veterinario/registrar-mascotas.html
     */
    @GetMapping("/registrar-mascota")
    public String registrarMascota(Model model) {
        model.addAttribute("mascota", new Mascota());
        return "veterinario/registrar-mascotas";
    }

    /**
     * Atiende POST /veterinario/mascotas/registrar-mascota: procesa el
     * formulario de registro de una nueva mascota. Spring captura los
     * datos con @ModelAttribute y los mapea a un objeto Mascota.
     *
     * Como el formulario no muestra el dueño ni el estado, se asignan
     * por defecto: el último dueño registrado y el estado "Activo".
     */
    @PostMapping("/registrar-mascota")
    public String guardarMascota(@ModelAttribute Mascota mascota) {
        mascota.setEstado("Activo");
        mascotaService.guardar(mascota);
        return "redirect:/veterinario/mascotas";
    }

    /**
     * Atiende GET /veterinario/mascotas/asignar-tratamiento: formulario
     * del veterinario para asignar un tratamiento/medicamento a una mascota.
     *
     * URL: http://localhost:8080/veterinario/mascotas/asignar-tratamiento
     * Vista: src/main/resources/templates/veterinario/asignar-tratamiento.html
     */
    @GetMapping("/asignar-tratamiento")
    public String asignarTratamiento(Model model) {
        model.addAttribute("mascotas", mascotaService.listarMascotas());
        model.addAttribute("drogas", drogaService.listarDrogas());
        model.addAttribute("tratamiento", new Tratamiento());
        return "veterinario/asignar-tratamiento";
    }

    /**
     * Atiende POST /veterinario/mascotas/asignar-tratamiento: procesa el
     * formulario de asignación de un tratamiento. Spring captura con
     * @ModelAttribute (mascotaId, fecha, drogaId) y el veterinario se
     * asigna por defecto. Además descuenta una unidad de la droga del
     * inventario y redirige a la pantalla de confirmación con los datos
     * de la aplicación (medicamento, unidades descontadas y stock).
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
     * mascota para el portal del veterinario (historial médico y propietario).
     *
     * URL para visualizar: http://localhost:8080/veterinario/mascotas/ficha?id=2
     * Vista: src/main/resources/templates/veterinario/ficha-clinica.html
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
     * (Activo/Inactivo) de una mascota. Es el endpoint del toggle
     * "Dar de alta / Reingresar" de la ficha clínica: el formulario envía
     * el id y el nuevo estado, y tras actualizarlo se vuelve a la ficha.
     */
    @PostMapping("/ficha/estado")
    public String cambiarEstado(@RequestParam("id") Integer id, @RequestParam("estado") String estado) {
        mascotaService.cambiarEstado(id, estado);
        return "redirect:/veterinario/mascotas/ficha?id=" + id;
    }

    /**
     * Maneja ids no numéricos (id=abc) en las rutas del veterinario:
     * Spring no puede convertirlos a Integer y lanzaria un 400; en su
     * lugar se renderiza la ficha con el panel informativo.
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public String idNoNumerico(Model modelo) {
        modelo.addAttribute("mensajeError", "El identificador suministrado no es válido.");
        return "veterinario/ficha-clinica";
    }
}

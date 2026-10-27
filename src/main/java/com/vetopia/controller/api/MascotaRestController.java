package com.vetopia.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.entities.Mascota;
import com.vetopia.service.MascotaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Mascota
 * Expone el CRUD de mascotas como API JSON para que el frontend Angular lo
 * consuma con HttpClient. Sigue el patrón de la clase de REST:
 * {@code @RestController} (devuelve datos, no vistas), {@code @CrossOrigin}
 * para permitir las peticiones desde http://localhost:4200 y las
 * anotaciones @GetMapping/@PostMapping/@PutMapping/@DeleteMapping con
 * @PathVariable y @RequestBody.
 *
 * El controller es delgado: delega toda la lógica en MascotaService y solo
 * traduce las peticiones HTTP. Las entidades se exponen directamente y las
 * colecciones @OneToMany van con @JsonIgnore para cortar la recursión
 * infinita de Jackson.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/mascota")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Mascota", description = "CRUD de mascotas del portal del veterinario.")
public class MascotaRestController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    @Operation(summary = "Lista todas las mascotas.")
    @GetMapping("/all")
    public List<Mascota> listarMascotas() {
        return mascotaService.listarMascotas();
    }

    @Operation(summary = "Obtiene una mascota por su identificador.")
    @GetMapping("/find/{id}")
    public Mascota obtenerMascota(@PathVariable Integer id) {
        return mascotaService.obtenerMascotaPorId(id);
    }

    @Operation(summary = "Busca mascotas por nombre (coincidencia parcial, sin distinguir mayúsculas).")
    @GetMapping("/search/{nombre}")
    public List<Mascota> buscarPorNombre(@PathVariable String nombre) {
        return mascotaService.buscarPorNombre(nombre);
    }

    @Operation(summary = "Lista las mascotas de un dueño existente (opcionalmente filtradas por nombre).")
    @GetMapping("/byDueno/{duenoId}")
    public List<Mascota> listarPorDueno(@PathVariable Integer duenoId,
                                        @RequestParam(name = "nombre", required = false) String nombre) {
        // Un solo endpoint para el listado del portal del cliente y su
        // búsqueda: buscarPorDuenoYNombre devuelve todo el listado del
        // dueño cuando el término llega vacío.
        return mascotaService.buscarPorDuenoYNombre(duenoId, nombre);
    }

    @Operation(summary = "Obtiene una mascota validando que pertenezca al dueño indicado.")
    @GetMapping("/propia/{id}/{duenoId}")
    public Mascota obtenerPropia(@PathVariable Integer id, @PathVariable Integer duenoId) {
        return mascotaService.obtenerPropia(id, duenoId);
    }

    @Operation(summary = "Lista solo las mascotas activas (candidatas a tratamiento).")
    @GetMapping("/activas")
    public List<Mascota> listarMascotasActivas() {
        return mascotaService.listarMascotasActivas();
    }

    @Operation(summary = "Crea o actualiza una mascota (dueño obligatorio, estado inicial Activo).")
    @PostMapping("/add")
    public void agregarMascota(@RequestBody Mascota mascota) {
        mascotaService.guardarValidada(mascota);
    }

    @Operation(summary = "Actualiza una mascota existente.")
    @PutMapping("/update")
    public void actualizarMascota(@RequestBody Mascota mascota) {
        mascotaService.guardarValidada(mascota);
    }

    @Operation(summary = "Alterna el estado de la mascota (Activo <-> Inactivo) y lo devuelve.")
    @PutMapping("/toggle/{id}")
    public String alternarEstado(@PathVariable Integer id) {
        return mascotaService.alternarEstado(id);
    }

    @Operation(summary = "Elimina definitivamente una mascota (desvincula su historial).")
    @DeleteMapping("/delete/{id}")
    public void eliminarMascota(@PathVariable Integer id) {
        mascotaService.eliminar(id);
    }
}

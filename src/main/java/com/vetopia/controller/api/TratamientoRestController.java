package com.vetopia.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.service.TratamientoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Tratamiento
 * Expone el registro y la consulta de tratamientos como API JSON. La
 * asignación (POST /add) delega en TratamientoService.asignar, que valida
 * mascota activa y stock y descuenta una unidad del inventario; devuelve la
 * Droga actualizada para la vista de confirmación (igual que la clase de
 * REST, que devuelve la entidad y no un String de vista).
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/tratamiento")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Tratamiento", description = "Asignación y consulta de tratamientos.")
public class TratamientoRestController {

    /** Servicio de tratamientos (flujo obligatorio: Controller -> Service). */
    @Autowired
    private TratamientoService tratamientoService;

    @Operation(summary = "Lista todos los tratamientos.")
    @GetMapping("/all")
    public List<Tratamiento> listarTratamientos() {
        return tratamientoService.listarTratamientos();
    }

    @Operation(summary = "Obtiene un tratamiento por su identificador.")
    @GetMapping("/find/{id}")
    public Tratamiento obtenerTratamiento(@PathVariable Integer id) {
        return tratamientoService.obtenerTratamientoPorId(id);
    }

    @Operation(summary = "Lista el historial de tratamientos de una mascota.")
    @GetMapping("/byMascota/{mascotaId}")
    public List<Tratamiento> listarPorMascota(@PathVariable Integer mascotaId) {
        return tratamientoService.listarTratamientosPorMascota(mascotaId);
    }

    @Operation(summary = "Asigna un tratamiento (valida mascota activa y stock; descuenta inventario).")
    @PostMapping("/add")
    public Droga asignarTratamiento(@RequestBody Tratamiento tratamiento) {
        return tratamientoService.asignar(tratamiento);
    }
}

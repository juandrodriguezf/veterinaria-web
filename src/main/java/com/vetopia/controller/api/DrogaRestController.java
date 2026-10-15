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
import com.vetopia.service.DrogaService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Droga
 * Expone el inventario de medicamentos como API JSON: listado, consulta por
 * id y registro. El listado alimenta el selector del formulario de
 * asignación de tratamientos en el frontend.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/droga")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Droga", description = "Inventario de medicamentos.")
public class DrogaRestController {

    /** Servicio de drogas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private DrogaService drogaService;

    @Operation(summary = "Lista todas las drogas del inventario.")
    @GetMapping("/all")
    public List<Droga> listarDrogas() {
        return drogaService.listarDrogas();
    }

    @Operation(summary = "Obtiene una droga por su identificador.")
    @GetMapping("/find/{id}")
    public Droga obtenerDroga(@PathVariable Integer id) {
        return drogaService.obtenerDrogaPorId(id);
    }

    @Operation(summary = "Registra una droga nueva en el inventario.")
    @PostMapping("/add")
    public void agregarDroga(@RequestBody Droga droga) {
        drogaService.guardar(droga);
    }
}

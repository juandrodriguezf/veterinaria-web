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
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.entities.Dueno;
import com.vetopia.service.DuenoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Dueno (cliente)
 * Expone el CRUD de dueños como API JSON. Sigue el patrón de la clase de
 * REST (@RestController, @CrossOrigin, verbos HTTP). El borrado es en
 * cascada (mascotas primero) tal como lo resuelve DuenoService.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/dueno")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Dueno", description = "CRUD de clientes (dueños) del portal del veterinario.")
public class DuenoRestController {

    /** Servicio de dueños (flujo obligatorio: Controller -> Service). */
    @Autowired
    private DuenoService duenoService;

    @Operation(summary = "Lista todos los clientes.")
    @GetMapping("/all")
    public List<Dueno> listarDuenos() {
        return duenoService.listarDuenos();
    }

    @Operation(summary = "Obtiene un cliente por su identificador.")
    @GetMapping("/find/{id}")
    public Dueno obtenerDueno(@PathVariable Integer id) {
        return duenoService.obtenerDuenoPorId(id);
    }

    @Operation(summary = "Obtiene un cliente activo para su portal (rechaza clientes inactivos).")
    @GetMapping("/activo/{id}")
    public Dueno obtenerDuenoActivo(@PathVariable Integer id) {
        return duenoService.obtenerActivo(id);
    }

    @Operation(summary = "Busca clientes por nombre (coincidencia parcial, sin distinguir mayúsculas).")
    @GetMapping("/search/{nombre}")
    public List<Dueno> buscarPorNombre(@PathVariable String nombre) {
        return duenoService.buscarPorNombre(nombre);
    }

    @Operation(summary = "Crea un cliente (valida cédula y correo únicos en todo el sistema).")
    @PostMapping("/add")
    public void agregarDueno(@RequestBody Dueno dueno) {
        duenoService.guardar(dueno);
    }

    @Operation(summary = "Actualiza un cliente existente (valida cédula y correo únicos en todo el sistema).")
    @PutMapping("/update")
    public void actualizarDueno(@RequestBody Dueno dueno) {
        duenoService.guardar(dueno);
    }

    @Operation(summary = "Alterna el estado del cliente (Activo <-> Inactivo) y lo devuelve.")
    @PutMapping("/toggle/{id}")
    public String alternarEstado(@PathVariable Integer id) {
        return duenoService.alternarEstado(id);
    }

    @Operation(summary = "Elimina un cliente y, en cascada, sus mascotas.")
    @DeleteMapping("/delete/{id}")
    public void eliminarDueno(@PathVariable Integer id) {
        duenoService.eliminarEnCascada(id);
    }
}

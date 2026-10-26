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

import com.vetopia.dto.ResumenDashboard;
import com.vetopia.entities.Administrador;
import com.vetopia.service.AdministradorService;
import com.vetopia.service.DashboardService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Administrador
 * Expone el listado y registro de administradores como API JSON.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/administrador")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Administrador", description = "Administradores del hospital.")
public class AdministradorRestController {

    /** Servicio de administradores (flujo obligatorio: Controller -> Service). */
    @Autowired
    private AdministradorService administradorService;

    /** Servicio del dashboard (calcula los KPIs del negocio). */
    @Autowired
    private DashboardService dashboardService;

    @Operation(summary = "Calcula los indicadores del dashboard del administrador.")
    @GetMapping("/dashboard")
    public ResumenDashboard obtenerDashboard() {
        return dashboardService.generarResumen();
    }

    @Operation(summary = "Lista todos los administradores.")
    @GetMapping("/all")
    public List<Administrador> listarAdministradores() {
        return administradorService.listarAdministradores();
    }

    @Operation(summary = "Obtiene un administrador por su identificador.")
    @GetMapping("/find/{id}")
    public Administrador obtenerAdministrador(@PathVariable Integer id) {
        return administradorService.obtenerAdministradorPorId(id);
    }

    @Operation(summary = "Registra un administrador nuevo (valida cédula y correo únicos en todo el sistema).")
    @PostMapping("/add")
    public void agregarAdministrador(@RequestBody Administrador administrador) {
        administradorService.guardar(administrador);
    }
}

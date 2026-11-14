package com.vetopia.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.VeterinarioDto;
import com.vetopia.dto.VeterinarioRequest;

import com.vetopia.mapper.VeterinarioMapper;
import com.vetopia.service.VeterinarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Veterinario
 * Expone el CRUD de veterinarios como API JSON. El listado permite al
 * frontend mostrar el nombre del responsable del portal y alimenta el
 * CRUD del portal del administrador; el borrado desvincula los
 * tratamientos del veterinario para conservar el historial clínico.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/veterinario")
@Tag(name = "Veterinario", description = "Veterinarios del hospital y su gestión desde el portal del administrador.")
public class VeterinarioRestController {

    /** Servicio de veterinarios (flujo obligatorio: Controller -> Service). */
    @Autowired
    private VeterinarioService veterinarioService;

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Lista todos los veterinarios.")
    @GetMapping("/all")
    public List<VeterinarioDto> listarVeterinarios() {
        return VeterinarioMapper.INSTANCE.toDto(veterinarioService.listarVeterinarios());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Obtiene un veterinario por su identificador.")
    @GetMapping("/find/{id}")
    public VeterinarioDto obtenerVeterinario(@PathVariable Integer id) {
        return VeterinarioMapper.INSTANCE.toDto(
                veterinarioService.obtenerVeterinarioPorId(id));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Busca veterinarios por cédula o nombre (coincidencia parcial).")
    @GetMapping("/search/{termino}")
    public List<VeterinarioDto> buscar(@PathVariable String termino) {
        return VeterinarioMapper.INSTANCE.toDto(veterinarioService.buscar(termino));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Registra un veterinario nuevo (valida cédula y correo únicos en todo el sistema).")
    @PostMapping("/add")
    public void agregarVeterinario(@RequestBody VeterinarioRequest solicitud) {
        veterinarioService.guardar(solicitud);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Actualiza un veterinario existente (valida cédula y correo únicos en todo el sistema).")
    @PutMapping("/update")
    public void actualizarVeterinario(@RequestBody VeterinarioRequest solicitud) {
        veterinarioService.guardar(solicitud);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Alterna el estado del veterinario (Activo <-> Inactivo) y lo devuelve.")
    @PutMapping("/toggle/{id}")
    public String alternarEstado(@PathVariable Integer id) {
        return veterinarioService.alternarEstado(id);
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Elimina un veterinario y desvincula sus tratamientos.")
    @DeleteMapping("/delete/{id}")
    public void eliminarVeterinario(@PathVariable Integer id) {
        veterinarioService.eliminar(id);
    }
}

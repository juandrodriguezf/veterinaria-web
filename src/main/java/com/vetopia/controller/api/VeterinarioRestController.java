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

import com.vetopia.dto.VeterinarioDTO;
import com.vetopia.entities.Veterinario;
import com.vetopia.mapper.DtoMapper;
import com.vetopia.service.VeterinarioService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

/**
 * CONTROLLER REST - Veterinario
 * Expone el listado y registro de veterinarios como API JSON. El listado
 * permite al frontend mostrar el nombre del responsable del portal.
 *
 * Flujo estricto: Controller -> Service -> Repository.
 */
@RestController
@RequestMapping("/veterinario")
@CrossOrigin(origins = "http://localhost:4200")
@Tag(name = "Veterinario", description = "Veterinarios del hospital.")
public class VeterinarioRestController {

    /** Servicio de veterinarios (flujo obligatorio: Controller -> Service). */
    @Autowired
    private VeterinarioService veterinarioService;

    /** Traductor de entidades a DTO de respuesta. */
    @Autowired
    private DtoMapper dtoMapper;

    @Operation(summary = "Lista todos los veterinarios.")
    @GetMapping("/all")
    public List<VeterinarioDTO> listarVeterinarios() {
        return dtoMapper.toVeterinarioDTOs(veterinarioService.listarVeterinarios());
    }

    @Operation(summary = "Obtiene un veterinario por su identificador.")
    @GetMapping("/find/{id}")
    public VeterinarioDTO obtenerVeterinario(@PathVariable Integer id) {
        return dtoMapper.toVeterinarioDTO(veterinarioService.obtenerVeterinarioPorId(id));
    }

    @Operation(summary = "Registra un veterinario nuevo.")
    @PostMapping("/add")
    public void agregarVeterinario(@RequestBody Veterinario veterinario) {
        veterinarioService.guardar(veterinario);
    }
}

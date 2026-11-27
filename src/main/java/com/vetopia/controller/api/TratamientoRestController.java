package com.vetopia.controller.api;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.DrogaDto;
import com.vetopia.dto.TratamientoDto;
import com.vetopia.dto.TratamientoRequest;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.mapper.DrogaMapper;
import com.vetopia.mapper.TratamientoMapper;
import com.vetopia.security.PermisoService;
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
@Tag(name = "Tratamiento", description = "Asignación y consulta de tratamientos.")
public class TratamientoRestController {

    /** Servicio de tratamientos (flujo obligatorio: Controller -> Service). */
    @Autowired
    private TratamientoService tratamientoService;

    /** Resuelve la identidad del token para el veterinario autenticado. */
    @Autowired
    private PermisoService permisoService;

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Lista todos los tratamientos.")
    @GetMapping("/all")
    public List<TratamientoDto> listarTratamientos() {
        return TratamientoMapper.INSTANCE.toDto(tratamientoService.listarTratamientos());
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR')")
    @Operation(summary = "Obtiene un tratamiento por su identificador.")
    @GetMapping("/find/{id}")
    public TratamientoDto obtenerTratamiento(@PathVariable Integer id) {
        return TratamientoMapper.INSTANCE.toDto(
                tratamientoService.obtenerTratamientoPorId(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO') or (hasAuthority('ROLE_CLIENTE') and @permisoService.esMascotaDelCliente(#mascotaId))")
    @Operation(summary = "Lista el historial de tratamientos de una mascota.")
    @GetMapping("/byMascota/{mascotaId}")
    public List<TratamientoDto> listarPorMascota(@PathVariable Integer mascotaId) {
        return TratamientoMapper.INSTANCE.toDto(
                tratamientoService.listarTratamientosPorMascota(mascotaId));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMINISTRADOR') or @permisoService.esVeterinarioAutenticado(#veterinarioId)")
    @Operation(summary = "Lista los tratamientos aplicados por un veterinario.")
    @GetMapping("/byVeterinario/{veterinarioId}")
    public List<TratamientoDto> listarPorVeterinario(@PathVariable Integer veterinarioId) {
        return TratamientoMapper.INSTANCE.toDto(
                tratamientoService.listarTratamientosPorVeterinario(veterinarioId));
    }

    @PreAuthorize("hasAuthority('ROLE_VETERINARIO')")
    @Operation(summary = "Lista los tratamientos del veterinario autenticado (el id sale del token).")
    @GetMapping("/mios")
    public List<TratamientoDto> listarMios() {
        return TratamientoMapper.INSTANCE.toDto(
                tratamientoService.listarTratamientosPorVeterinario(
                        permisoService.perfilAutenticadoId()));
    }

    @PreAuthorize("hasAuthority('ROLE_VETERINARIO')")
    @Operation(summary = "Asigna un tratamiento (valida mascota activa y stock; descuenta inventario).")
    @PostMapping("/add")
    public DrogaDto asignarTratamiento(@RequestBody TratamientoRequest solicitud) {
        Tratamiento tratamiento = TratamientoMapper.INSTANCE.toEntity(solicitud);
        tratamiento.setVeterinario(Veterinario.builder().id(permisoService.perfilAutenticadoId()).build());
        Droga droga = tratamientoService.asignar(tratamiento);
        return DrogaMapper.INSTANCE.toDto(droga);
    }
}

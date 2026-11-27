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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RestController;

import com.vetopia.dto.MascotaDto;
import com.vetopia.dto.MascotaRequest;

import com.vetopia.mapper.MascotaMapper;
import com.vetopia.security.PermisoService;
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
@Tag(name = "Mascota", description = "CRUD de mascotas del portal del veterinario.")
public class MascotaRestController {

    /** Servicio de mascotas (flujo obligatorio: Controller -> Service). */
    @Autowired
    private MascotaService mascotaService;

    /** Resuelve la identidad del token para los endpoints del cliente. */
    @Autowired
    private PermisoService permisoService;

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Lista todas las mascotas.")
    @GetMapping("/all")
    public List<MascotaDto> listarMascotas() {
        return MascotaMapper.INSTANCE.toDto(mascotaService.listarMascotas());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Obtiene una mascota por su identificador.")
    @GetMapping("/find/{id}")
    public MascotaDto obtenerMascota(@PathVariable Integer id) {
        return MascotaMapper.INSTANCE.toDto(mascotaService.obtenerMascotaPorId(id));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Busca mascotas por nombre (coincidencia parcial, sin distinguir mayúsculas).")
    @GetMapping("/search/{nombre}")
    public List<MascotaDto> buscarPorNombre(@PathVariable String nombre) {
        return MascotaMapper.INSTANCE.toDto(mascotaService.buscarPorNombre(nombre));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Lista las mascotas de un dueño existente (opcionalmente filtradas por nombre).")
    @GetMapping("/byDueno/{duenoId}")
    public List<MascotaDto> listarPorDueno(@PathVariable Integer duenoId,
                                        @RequestParam(name = "nombre", required = false) String nombre) {
        // Un solo endpoint para el listado del portal del cliente y su
        // búsqueda: buscarPorDuenoYNombre devuelve todo el listado del
        // dueño cuando el término llega vacío.
        return MascotaMapper.INSTANCE.toDto(
                mascotaService.buscarPorDuenoYNombre(duenoId, nombre));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Obtiene una mascota validando que pertenezca al dueño indicado.")
    @GetMapping("/propia/{id}/{duenoId}")
    public MascotaDto obtenerPropia(@PathVariable Integer id, @PathVariable Integer duenoId) {
        return MascotaMapper.INSTANCE.toDto(mascotaService.obtenerPropia(id, duenoId));
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE')")
    @Operation(summary = "Lista las mascotas del cliente autenticado (el dueño sale del token).")
    @GetMapping("/mias")
    public List<MascotaDto> listarMias(
            @RequestParam(name = "nombre", required = false) String nombre) {
        return MascotaMapper.INSTANCE.toDto(mascotaService.buscarPorDuenoYNombre(
                permisoService.perfilAutenticadoId(), nombre));
    }

    @PreAuthorize("hasAuthority('ROLE_CLIENTE') and @permisoService.esMascotaDelCliente(#id)")
    @Operation(summary = "Obtiene una mascota propia validando la propiedad con el token.")
    @GetMapping("/mias/{id}")
    public MascotaDto obtenerMia(@PathVariable Integer id) {
        return MascotaMapper.INSTANCE.toDto(mascotaService.obtenerPropia(
                id, permisoService.perfilAutenticadoId()));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Lista solo las mascotas activas (candidatas a tratamiento).")
    @GetMapping("/activas")
    public List<MascotaDto> listarMascotasActivas() {
        return MascotaMapper.INSTANCE.toDto(mascotaService.listarMascotasActivas());
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Crea o actualiza una mascota (dueño obligatorio, estado inicial Activo).")
    @PostMapping("/add")
    public void agregarMascota(@RequestBody MascotaRequest solicitud) {
        mascotaService.guardarValidada(MascotaMapper.INSTANCE.toEntity(solicitud));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Actualiza una mascota existente.")
    @PutMapping("/update")
    public void actualizarMascota(@RequestBody MascotaRequest solicitud) {
        mascotaService.guardarValidada(MascotaMapper.INSTANCE.toEntity(solicitud));
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Alterna el estado de la mascota (Activo <-> Inactivo) y lo devuelve.")
    @PutMapping("/toggle/{id}")
    public String alternarEstado(@PathVariable Integer id) {
        return mascotaService.alternarEstado(id);
    }

    @PreAuthorize("hasAnyAuthority('ROLE_ADMINISTRADOR','ROLE_VETERINARIO')")
    @Operation(summary = "Elimina definitivamente una mascota (desvincula su historial).")
    @DeleteMapping("/delete/{id}")
    public void eliminarMascota(@PathVariable Integer id) {
        mascotaService.eliminar(id);
    }
}

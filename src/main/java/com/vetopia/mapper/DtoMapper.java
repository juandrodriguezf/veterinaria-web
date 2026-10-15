package com.vetopia.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vetopia.dto.AdministradorDTO;
import com.vetopia.dto.DrogaDTO;
import com.vetopia.dto.DrogaResumenDTO;
import com.vetopia.dto.DuenoDTO;
import com.vetopia.dto.TratamientoDTO;
import com.vetopia.dto.VeterinarioDTO;
import com.vetopia.dto.VeterinarioResumenDTO;
import com.vetopia.entities.Administrador;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.MascotaRepository;

/**
 * MAPPER - Entidad a DTO
 * Traduce las entidades JPA a los DTO de respuesta que expone la API. Es
 * una preocupación de presentación, así que vive fuera de las capas de
 * negocio: los controllers siguen delegando en el service (que devuelve
 * entidades) y aquí se recorta el JSON a lo que cada pantalla necesita.
 *
 * El conteo de mascotas del dueño se resuelve con una consulta derivada
 * del MascotaRepository (countByDuenoId), para no arrastrar la colección
 * completa de mascotas en cada cliente.
 */
@Component
public class DtoMapper {

    /** Repositorio de mascotas: se usa para contar las de cada dueño. */
    @Autowired
    private MascotaRepository mascotaRepository;

    /** Dueño -> DTO (sin contraseña, con el conteo de mascotas). */
    public DuenoDTO toDuenoDTO(Dueno dueno) {
        if (dueno == null) {
            return null;
        }
        return new DuenoDTO(
                dueno.getId(),
                dueno.getCedula(),
                dueno.getNombre(),
                dueno.getCorreo(),
                dueno.getCelular(),
                dueno.getEstado(),
                (int) mascotaRepository.countByDuenoId(dueno.getId()));
    }

    /** Lista de dueños -> lista de DTO. */
    public List<DuenoDTO> toDuenoDTOs(List<Dueno> duenos) {
        return duenos.stream().map(this::toDuenoDTO).toList();
    }

    /** Droga -> DTO completo (inventario, sin colecciones anidadas). */
    public DrogaDTO toDrogaDTO(Droga droga) {
        if (droga == null) {
            return null;
        }
        return new DrogaDTO(
                droga.getId(),
                droga.getNombre(),
                droga.getPrecioCompra(),
                droga.getPrecioVenta(),
                droga.getUnidadesDisponibles(),
                droga.getUnidadesVendidas());
    }

    /** Droga -> resumen (para anidar en un tratamiento). */
    public DrogaResumenDTO toDrogaResumenDTO(Droga droga) {
        if (droga == null) {
            return null;
        }
        return new DrogaResumenDTO(droga.getId(), droga.getNombre());
    }

    /** Lista de drogas -> lista de DTO. */
    public List<DrogaDTO> toDrogaDTOs(List<Droga> drogas) {
        return drogas.stream().map(this::toDrogaDTO).toList();
    }

    /** Veterinario -> DTO (sin contraseña ni colecciones anidadas). */
    public VeterinarioDTO toVeterinarioDTO(Veterinario veterinario) {
        if (veterinario == null) {
            return null;
        }
        return new VeterinarioDTO(
                veterinario.getId(),
                veterinario.getCedula(),
                veterinario.getCorreo(),
                veterinario.getEspecialidad(),
                veterinario.getNumeroAtenciones(),
                veterinario.getNombre(),
                veterinario.getEstado(),
                veterinario.getUrlFoto());
    }

    /** Veterinario -> resumen (para anidar en un tratamiento). */
    public VeterinarioResumenDTO toVeterinarioResumenDTO(Veterinario veterinario) {
        if (veterinario == null) {
            return null;
        }
        return new VeterinarioResumenDTO(
                veterinario.getId(),
                veterinario.getNombre(),
                veterinario.getEspecialidad());
    }

    /** Lista de veterinarios -> lista de DTO. */
    public List<VeterinarioDTO> toVeterinarioDTOs(List<Veterinario> veterinarios) {
        return veterinarios.stream().map(this::toVeterinarioDTO).toList();
    }

    /** Administrador -> DTO (sin contraseña ni colecciones anidadas). */
    public AdministradorDTO toAdministradorDTO(Administrador administrador) {
        if (administrador == null) {
            return null;
        }
        return new AdministradorDTO(
                administrador.getId(),
                administrador.getCedula(),
                administrador.getCorreo(),
                administrador.getNombre());
    }

    /** Lista de administradores -> lista de DTO. */
    public List<AdministradorDTO> toAdministradorDTOs(List<Administrador> administradores) {
        return administradores.stream().map(this::toAdministradorDTO).toList();
    }

    /** Tratamiento -> DTO (droga y veterinario como resúmenes, mascota por id). */
    public TratamientoDTO toTratamientoDTO(Tratamiento tratamiento) {
        if (tratamiento == null) {
            return null;
        }
        return new TratamientoDTO(
                tratamiento.getId(),
                tratamiento.getFecha(),
                tratamiento.getMascota() != null ? tratamiento.getMascota().getId() : null,
                toDrogaResumenDTO(tratamiento.getDroga()),
                toVeterinarioResumenDTO(tratamiento.getVeterinario()));
    }

    /** Lista de tratamientos -> lista de DTO. */
    public List<TratamientoDTO> toTratamientoDTOs(List<Tratamiento> tratamientos) {
        return tratamientos.stream().map(this::toTratamientoDTO).toList();
    }
}

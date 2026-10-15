package com.vetopia.mapper;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.vetopia.dto.DrogaResumenDTO;
import com.vetopia.dto.DuenoDTO;
import com.vetopia.dto.DuenoResumenDTO;
import com.vetopia.dto.MascotaDetalleDTO;
import com.vetopia.dto.MascotaResumenDTO;
import com.vetopia.dto.TratamientoDTO;
import com.vetopia.dto.VeterinarioResumenDTO;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
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

    /** Dueño -> resumen (para anidar en la ficha de una mascota). */
    public DuenoResumenDTO toDuenoResumenDTO(Dueno dueno) {
        if (dueno == null) {
            return null;
        }
        return new DuenoResumenDTO(
                dueno.getId(),
                dueno.getNombre(),
                dueno.getCelular(),
                dueno.getCorreo());
    }

    /** Mascota -> resumen (para los listados, sin anidar el dueño). */
    public MascotaResumenDTO toMascotaResumenDTO(Mascota mascota) {
        if (mascota == null) {
            return null;
        }
        return new MascotaResumenDTO(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getEdad(),
                mascota.getSexo(),
                mascota.getImagen(),
                mascota.getPesoKg(),
                mascota.getColor(),
                mascota.getEstado());
    }

    /** Lista de mascotas -> lista de resúmenes. */
    public List<MascotaResumenDTO> toMascotaResumenDTOs(List<Mascota> mascotas) {
        return mascotas.stream().map(this::toMascotaResumenDTO).toList();
    }

    /** Mascota -> detalle (resumen + datos clínicos + dueño resumido). */
    public MascotaDetalleDTO toMascotaDetalleDTO(Mascota mascota) {
        if (mascota == null) {
            return null;
        }
        return new MascotaDetalleDTO(
                mascota.getId(),
                mascota.getNombre(),
                mascota.getEspecie(),
                mascota.getRaza(),
                mascota.getEdad(),
                mascota.getSexo(),
                mascota.getImagen(),
                mascota.getPesoKg(),
                mascota.getColor(),
                mascota.getFechaIngreso(),
                mascota.getEnfermedad(),
                mascota.getEstado(),
                toDuenoResumenDTO(mascota.getDueno()));
    }

    /** Droga -> resumen (para anidar en un tratamiento). */
    public DrogaResumenDTO toDrogaResumenDTO(Droga droga) {
        if (droga == null) {
            return null;
        }
        return new DrogaResumenDTO(droga.getId(), droga.getNombre());
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

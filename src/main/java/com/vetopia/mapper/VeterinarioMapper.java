package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.VeterinarioDto;
import com.vetopia.entities.Veterinario;

/**
 * MAPPER - Veterinario
 * Convierte entidades Veterinario en DTOs planos, sin cuenta de acceso ni
 * tratamientos.
 */
@Mapper
public interface VeterinarioMapper {

    VeterinarioMapper INSTANCE = Mappers.getMapper(VeterinarioMapper.class);

    VeterinarioDto toDto(Veterinario veterinario);

    List<VeterinarioDto> toDto(List<Veterinario> veterinarios);
}

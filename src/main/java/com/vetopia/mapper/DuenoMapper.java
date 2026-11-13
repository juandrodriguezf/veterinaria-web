package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.DuenoDto;
import com.vetopia.entities.Dueno;

/**
 * MAPPER - Dueno
 * Convierte entidades Dueno en DTOs planos, sin contraseña ni mascotas.
 */
@Mapper
public interface DuenoMapper {

    DuenoMapper INSTANCE = Mappers.getMapper(DuenoMapper.class);

    DuenoDto toDto(Dueno dueno);

    List<DuenoDto> toDto(List<Dueno> duenos);
}

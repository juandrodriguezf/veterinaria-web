package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.DuenoDto;
import com.vetopia.dto.DuenoRequest;
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

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "mascotas", ignore = true)
    @Mapping(target = "mascotaCount", ignore = true)
    Dueno toEntity(DuenoRequest solicitud);
}

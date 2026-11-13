package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.DrogaDto;
import com.vetopia.entities.Droga;

/**
 * MAPPER - Droga
 * Convierte entidades Droga en DTOs planos: solo id, nombre y stock.
 */
@Mapper
public interface DrogaMapper {

    DrogaMapper INSTANCE = Mappers.getMapper(DrogaMapper.class);

    DrogaDto toDto(Droga droga);

    List<DrogaDto> toDto(List<Droga> drogas);
}

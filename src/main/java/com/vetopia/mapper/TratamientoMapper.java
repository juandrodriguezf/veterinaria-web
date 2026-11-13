package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.TratamientoDto;
import com.vetopia.entities.Tratamiento;

/**
 * MAPPER - Tratamiento
 * Convierte entidades Tratamiento en DTOs planos. Los datos de droga,
 * veterinario y mascota se leen de los getters calculados de la entidad,
 * de modo que no viaje ninguna relación anidada.
 */
@Mapper
public interface TratamientoMapper {

    TratamientoMapper INSTANCE = Mappers.getMapper(TratamientoMapper.class);

    TratamientoDto toDto(Tratamiento tratamiento);

    List<TratamientoDto> toDto(List<Tratamiento> tratamientos);
}

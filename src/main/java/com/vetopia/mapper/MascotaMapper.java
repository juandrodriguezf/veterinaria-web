package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.MascotaDto;
import com.vetopia.entities.Mascota;

/**
 * MAPPER - Mascota
 * Convierte entidades Mascota en DTOs planos. El dueño se expone solo como
 * identificador, de modo que la API no devuelva objetos anidados.
 */
@Mapper
public interface MascotaMapper {

    MascotaMapper INSTANCE = Mappers.getMapper(MascotaMapper.class);

    @Mapping(source = "dueno.id", target = "duenoId")
    MascotaDto toDto(Mascota mascota);

    List<MascotaDto> toDto(List<Mascota> mascotas);
}

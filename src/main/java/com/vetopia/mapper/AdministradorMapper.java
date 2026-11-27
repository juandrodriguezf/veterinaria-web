package com.vetopia.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import com.vetopia.dto.AdministradorDto;
import com.vetopia.dto.AdministradorRequest;
import com.vetopia.entities.Administrador;

/**
 * MAPPER - Administrador
 * Convierte entidades Administrador en DTOs planos, sin cuenta de acceso ni
 * veterinarios gestionados.
 */
@Mapper
public interface AdministradorMapper {

    AdministradorMapper INSTANCE = Mappers.getMapper(AdministradorMapper.class);

    AdministradorDto toDto(Administrador administrador);

    List<AdministradorDto> toDto(List<Administrador> administradores);

    @Mapping(target = "user", ignore = true)
    @Mapping(target = "veterinarios", ignore = true)
    Administrador toEntity(AdministradorRequest solicitud);
}

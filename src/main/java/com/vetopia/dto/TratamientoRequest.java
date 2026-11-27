package com.vetopia.dto;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - TratamientoRequest
 * Cuerpo de entrada para asignar un tratamiento. Solo viajan la mascota y
 * la droga por id; el veterinario responsable se toma del token firmado.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TratamientoRequest {

    private LocalDate fecha;
    private Integer mascotaId;
    private Integer drogaId;
}

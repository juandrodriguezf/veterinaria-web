package com.vetopia.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO - ResumenDashboard
 * Objeto de transferencia (no es una entidad del diagrama de clases):
 * reúne en una sola respuesta los indicadores (KPIs) que pide el portal
 * del administrador, para que el frontend haga una única petición y no
 * reciba objetos anidados de las entidades.
 *
 * Lombok genera getters/setters (@Data) y los constructores vacío y
 * completo, igual que en ResultadoLogin.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumenDashboard {

    /** Cantidad total de tratamientos administrados en el último mes. */
    private Long tratamientosUltimoMes;

    /** Tratamientos del último mes agrupados por medicamento (tabla medicamento - cantidad). */
    private List<ConteoMedicamento> tratamientosPorMedicamento;

    /** Cantidad de veterinarios activos en la plataforma. */
    private Long veterinariosActivos;

    /** Cantidad de veterinarios inactivos en la plataforma. */
    private Long veterinariosInactivos;

    /** Cantidad de mascotas totales registradas en la veterinaria. */
    private Long mascotasTotales;

    /** Cantidad de mascotas activas (en tratamiento) en la veterinaria. */
    private Long mascotasActivas;

    /** Ventas totales de la veterinaria (precio de venta por unidad vendida). */
    private Double ventasTotales;

    /** Ganancias totales de la veterinaria (venta menos compra por unidad vendida). */
    private Double gananciasTotales;

    /** Top 3 de medicamentos con más unidades vendidas. */
    private List<ConteoMedicamento> topMedicamentos;
}

package com.vetopia.service;

import com.vetopia.dto.ResumenDashboard;

/**
 * SERVICIO - DashboardService
 * Define el cálculo de los indicadores (KPIs) que muestra el dashboard del
 * portal del administrador. Es el único punto al que el controlador accede;
 * a su vez, delega el acceso a datos en la capa repository.
 */
public interface DashboardService {

    /**
     * Calcula el resumen de indicadores del negocio: tratamientos del
     * último mes, tratamientos por medicamento, veterinarios activos e
     * inactivos, mascotas totales y activas, ventas, ganancias y el top 3
     * de medicamentos más vendidos.
     */
    ResumenDashboard generarResumen();
}

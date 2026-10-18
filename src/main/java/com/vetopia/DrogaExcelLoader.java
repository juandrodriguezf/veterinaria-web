package com.vetopia;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vetopia.service.DrogaService;

/**
 * CARGADOR DE DATOS - inventario de medicamentos desde Excel
 * Al arrancar carga el inventario de drogas desde el archivo fijo
 * resources/data/medicamentos.xlsx (upsert por nombre, resuelto en la capa
 * service). Corre antes que DataLoader (@Order(1)) porque los tratamientos
 * semilla referencian drogas de ese inventario.
 */
@Component
@Order(1)
public class DrogaExcelLoader implements CommandLineRunner {

    private static final Logger LOG = LoggerFactory.getLogger(DrogaExcelLoader.class);

    /** Servicio de drogas (flujo obligatorio: cargador -> Service). */
    private final DrogaService drogaService;

    public DrogaExcelLoader(DrogaService drogaService) {
        this.drogaService = drogaService;
    }

    @Override
    public void run(String... args) {
        int cargadas = drogaService.cargarDesdeExcel();
        LOG.info("Inventario de medicamentos cargado desde Excel: {} drogas.", cargadas);
    }
}

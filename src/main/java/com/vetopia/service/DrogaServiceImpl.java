package com.vetopia.service;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.DrogaRepository;

import jakarta.transaction.Transactional;

/**
 * CAPA SERVICIO - Implementación de DrogaService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class DrogaServiceImpl implements DrogaService {

    /** Ruta fija del inventario de medicamentos dentro de resources. */
    private static final String RUTA_EXCEL = "data/medicamentos.xlsx";

    /** Columnas que debe traer el Excel del inventario. */
    private static final String[] COLUMNAS = { "NOMBRE", "PRECIO VENTA", "PRECIO COMPRA",
            "UNIDADES DISPONIBLES", "UNIDADES VENDIDAS" };

    /** Repositorio de drogas (flujo obligatorio: Service -> Repository). */
    @Autowired
    private DrogaRepository drogaRepository;

    @Override
    public List<Droga> listarDrogas() {
        return List.copyOf(drogaRepository.findAll());
    }

    @Override
    public Droga obtenerDrogaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la droga.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return drogaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún medicamento registrado con el identificador \"" + id + "\"."));
    }

    @Override
    public void guardar(Droga droga) {
        drogaRepository.save(droga);
    }

    /**
     * {@inheritDoc}
     * Apache POI abre el archivo fijo del inventario, ubica la fila de
     * encabezado (la que contiene "NOMBRE") y recorre las filas de datos.
     * El nombre es la clave del inventario: si ya existe se actualizan
     * precios y unidades, y si no se registra una droga nueva. La operación
     * es transaccional: si el Excel viene incompleto no queda un inventario
     * a medias.
     */
    @Override
    @Transactional
    public int cargarDesdeExcel() {
        try (InputStream entrada = new ClassPathResource(RUTA_EXCEL).getInputStream();
                Workbook libro = WorkbookFactory.create(entrada)) {
            Sheet hoja = libro.getSheetAt(0);
            Row encabezado = hoja.getRow(buscarFilaEncabezado(hoja));
            Map<String, Integer> columnas = leerColumnas(encabezado);
            int cargadas = 0;
            for (int i = encabezado.getRowNum() + 1; i <= hoja.getLastRowNum(); i++) {
                Row fila = hoja.getRow(i);
                if (fila == null) {
                    continue;
                }
                String nombre = texto(fila.getCell(columnas.get("NOMBRE")));
                if (nombre == null || nombre.isBlank()) {
                    continue;
                }
                // Upsert: el nombre ya existe -> se actualiza; si no, se crea.
                Droga droga = drogaRepository.findByNombre(nombre)
                        .orElseGet(() -> Droga.builder().nombre(nombre).build());
                droga.setPrecioVenta(decimal(fila.getCell(columnas.get("PRECIO VENTA"))));
                droga.setPrecioCompra(decimal(fila.getCell(columnas.get("PRECIO COMPRA"))));
                droga.setUnidadesDisponibles(entero(fila.getCell(columnas.get("UNIDADES DISPONIBLES"))));
                droga.setUnidadesVendidas(entero(fila.getCell(columnas.get("UNIDADES VENDIDAS"))));
                drogaRepository.save(droga);
                cargadas++;
            }
            return cargadas;
        } catch (IOException e) {
            throw new IllegalStateException("No se pudo leer el inventario de medicamentos (" + RUTA_EXCEL + ").", e);
        }
    }

    /**
     * Devuelve el índice de la fila que contiene el encabezado del
     * inventario (la primera celda que dice "NOMBRE"), de modo que el
     * archivo pueda traer títulos o filas vacías antes de la tabla.
     */
    private int buscarFilaEncabezado(Sheet hoja) {
        for (int i = hoja.getFirstRowNum(); i <= hoja.getLastRowNum(); i++) {
            Row fila = hoja.getRow(i);
            if (fila == null) {
                continue;
            }
            for (Cell celda : fila) {
                if ("NOMBRE".equalsIgnoreCase(texto(celda))) {
                    return i;
                }
            }
        }
        throw new IllegalStateException("No se encontró la fila de encabezado en " + RUTA_EXCEL + ".");
    }

    /**
     * Mapea cada título del encabezado (normalizado a mayúsculas) con su
     * número de columna y valida que el Excel traiga todas las columnas del
     * inventario.
     */
    private Map<String, Integer> leerColumnas(Row encabezado) {
        Map<String, Integer> columnas = new HashMap<>();
        for (Cell celda : encabezado) {
            String titulo = texto(celda);
            if (titulo != null) {
                columnas.put(titulo.toUpperCase(), celda.getColumnIndex());
            }
        }
        for (String requerida : COLUMNAS) {
            if (!columnas.containsKey(requerida)) {
                throw new IllegalStateException("Al inventario (" + RUTA_EXCEL + ") le falta la columna \""
                        + requerida + "\".");
            }
        }
        return columnas;
    }

    /** Lee una celda como texto (las celdas numéricas se leen sin decimales). */
    private String texto(Cell celda) {
        if (celda == null) {
            return null;
        }
        if (celda.getCellType() == CellType.STRING) {
            return celda.getStringCellValue().trim();
        }
        if (celda.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) celda.getNumericCellValue());
        }
        return null;
    }

    /** Lee una celda como número; devuelve null si está vacía o no es numérica. */
    private Double numero(Cell celda) {
        if (celda == null) {
            return null;
        }
        if (celda.getCellType() == CellType.NUMERIC) {
            return celda.getNumericCellValue();
        }
        if (celda.getCellType() == CellType.STRING) {
            try {
                return Double.valueOf(celda.getStringCellValue().trim());
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /** Precio como decimal; una celda vacía se registra como cero. */
    private double decimal(Cell celda) {
        Double valor = numero(celda);
        return valor == null ? 0.0 : valor;
    }

    /** Unidades como entero redondeado; una celda vacía se registra como cero. */
    private int entero(Cell celda) {
        Double valor = numero(celda);
        return valor == null ? 0 : (int) Math.round(valor);
    }
}

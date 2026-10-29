package com.vetopia.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.dto.ConteoMedicamento;
import com.vetopia.dto.ResumenDashboard;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

/**
 * CAPA SERVICIO - Implementación de DashboardService
 * Calcula los KPIs del portal del administrador con las consultas de los
 * repositorios y agrupaciones en memoria. Las ventas y ganancias se
 * derivan del inventario (precio de venta/compra por unidad vendida), que
 * viaja en la entidad Droga aunque no se exponga en la API REST.
 *
 * El repositorio se recibe por inyección por campo (@Autowired), igual que
 * en los demás servicios del proyecto.
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    /** Repositorio de tratamientos (tratamientos del último mes). */
    @Autowired
    private TratamientoRepository tratamientoRepository;

    /** Repositorio de drogas (ventas, ganancias y top de medicamentos). */
    @Autowired
    private DrogaRepository drogaRepository;

    /** Repositorio de veterinarios (activos e inactivos). */
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    /** Repositorio de mascotas (totales y activas). */
    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public ResumenDashboard generarResumen() {
        // "Último mes" se mide desde hoy menos un mes, con la fecha incluida.
        LocalDate desde = LocalDate.now().minusMonths(1);
        List<Tratamiento> ultimoMes = tratamientoRepository.findByFechaGreaterThanEqual(desde);

        ResumenDashboard resumen = new ResumenDashboard();
        resumen.setTratamientosUltimoMes((long) ultimoMes.size());
        resumen.setTratamientosPorMedicamento(agruparPorMedicamento(ultimoMes));

        resumen.setVeterinariosActivos(veterinarioRepository.countByEstado("Activo"));
        resumen.setVeterinariosInactivos(veterinarioRepository.countByEstado("Inactivo"));
        resumen.setMascotasTotales(mascotaRepository.count());
        resumen.setMascotasActivas(mascotaRepository.countByEstado("Activo"));

        List<Droga> drogas = drogaRepository.findAll();
        resumen.setVentasTotales(totalVentas(drogas));
        resumen.setGananciasTotales(totalGanancias(drogas));
        resumen.setTopMedicamentos(topVendidos(drogas));
        return resumen;
    }

    /**
     * Agrupa los tratamientos por nombre de medicamento y devuelve la tabla
     * ordenada de mayor a menor cantidad. La agrupación se hace en esta capa
     * para no agregar consultas JPQL al repositorio.
     */
    private List<ConteoMedicamento> agruparPorMedicamento(List<Tratamiento> tratamientos) {
        Map<String, Long> conteo = tratamientos.parallelStream()
                .filter(tratamiento -> tratamiento.getDroga() != null)
                .collect(Collectors.groupingByConcurrent(tratamiento -> tratamiento.getDroga().getNombre(),
                        Collectors.counting()));
        return conteo.entrySet().stream()
                .map(entrada -> new ConteoMedicamento(entrada.getKey(), entrada.getValue()))
                .sorted(Comparator.comparing(ConteoMedicamento::getCantidad).reversed())
                .collect(Collectors.toList());
    }

    /** Suma el precio de venta de cada unidad vendida del inventario. */
    private double totalVentas(List<Droga> drogas) {
        return drogas.parallelStream()
                .mapToDouble(droga -> precioVenta(droga) * unidadesVendidas(droga))
                .sum();
    }

    /** Suma la diferencia entre venta y compra de cada unidad vendida. */
    private double totalGanancias(List<Droga> drogas) {
        return drogas.parallelStream()
                .mapToDouble(droga -> (precioVenta(droga) - precioCompra(droga)) * unidadesVendidas(droga))
                .sum();
    }

    /** Top 3 de medicamentos con más unidades vendidas (solo con ventas). */
    private List<ConteoMedicamento> topVendidos(List<Droga> drogas) {
        return drogas.parallelStream()
                .filter(droga -> unidadesVendidas(droga) > 0)
                .sorted(Comparator.comparingLong(this::unidadesVendidas).reversed())
                .limit(3)
                .map(droga -> new ConteoMedicamento(droga.getNombre(), unidadesVendidas(droga)))
                .collect(Collectors.toList());
    }

    /** Precio de compra a prueba de nulos (las columnas no admiten nulo). */
    private double precioCompra(Droga droga) {
        return droga.getPrecioCompra() == null ? 0 : droga.getPrecioCompra();
    }

    /** Precio de venta a prueba de nulos (las columnas no admiten nulo). */
    private double precioVenta(Droga droga) {
        return droga.getPrecioVenta() == null ? 0 : droga.getPrecioVenta();
    }

    /** Unidades vendidas a prueba de nulos. */
    private long unidadesVendidas(Droga droga) {
        return droga.getUnidadesVendidas() == null ? 0 : droga.getUnidadesVendidas();
    }
}

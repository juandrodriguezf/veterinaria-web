package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Droga;

/**
 * Simula la persistencia de drogas/medicamentos usando un HashMap
 * (llave = id, valor = Droga), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class DrogaRepositoryImpl implements DrogaRepository {

    /** "Tabla" drogas simulada en memoria. */
    private final Map<Integer, Droga> tablaDrogas = new HashMap<>();

    /**
     * Precarga registros de ejemplo de medicamentos.
     */
    public DrogaRepositoryImpl() {
        tablaDrogas.put(1, Droga.builder().id(1).nombre("Amoxicilina")
                .precioCompra(8000.0).precioVenta(15000.0).unidadesDisponibles(100).unidadesVendidas(0).build());
        tablaDrogas.put(2, Droga.builder().id(2).nombre("Ivermectina")
                .precioCompra(12000.0).precioVenta(22000.0).unidadesDisponibles(50).unidadesVendidas(0).build());
        tablaDrogas.put(3, Droga.builder().id(3).nombre("Metronidazol")
                .precioCompra(6000.0).precioVenta(11000.0).unidadesDisponibles(75).unidadesVendidas(0).build());
    }

    @Override
    public Collection<Droga> searchAll() {
        return new ArrayList<>(tablaDrogas.values());
    }

    @Override
    public Droga searchById(Integer id) {
        return tablaDrogas.get(id);
    }

    @Override
    public void save(Droga droga) {
        int lastId = tablaDrogas.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        droga.setId(lastId + 1);
        tablaDrogas.put(droga.getId(), droga);
    }
}

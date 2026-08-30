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
        tablaDrogas.put(1, new Droga(1, "Amoxicilina", 8000.0, 15000.0, 100, 0));
        tablaDrogas.put(2, new Droga(2, "Ivermectina", 12000.0, 22000.0, 50, 0));
        tablaDrogas.put(3, new Droga(3, "Metronidazol", 6000.0, 11000.0, 75, 0));
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

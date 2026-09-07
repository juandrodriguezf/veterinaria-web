package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;

/**
 * Simula la persistencia de tratamientos usando un HashMap
 * (llave = id, valor = Tratamiento), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class TratamientoRepositoryImpl implements TratamientoRepository {

    /** "Tabla" tratamientos simulada en memoria. */
    private final Map<Integer, Tratamiento> tablaTratamientos = new HashMap<>();

    /**
     * Precarga registros de ejemplo de tratamientos, conectados por
     * llave foránea a una mascota (mascotaId), a una droga (drogaId)
     * y al veterinario que lo realizó (veterinarioId).
     */
    public TratamientoRepositoryImpl() {
        tablaTratamientos.put(1, Tratamiento.builder()
                .id(1).fecha(LocalDate.parse("2026-08-10"))
                .mascota(Mascota.builder().id(2).build())
                .droga(Droga.builder().id(1).build())
                .veterinario(Veterinario.builder().id(1).build())
                .build());
        tablaTratamientos.put(2, Tratamiento.builder()
                .id(2).fecha(LocalDate.parse("2026-08-15"))
                .mascota(Mascota.builder().id(1).build())
                .droga(Droga.builder().id(2).build())
                .veterinario(Veterinario.builder().id(2).build())
                .build());
        tablaTratamientos.put(3, Tratamiento.builder()
                .id(3).fecha(LocalDate.parse("2026-08-20"))
                .mascota(Mascota.builder().id(3).build())
                .droga(Droga.builder().id(3).build())
                .veterinario(Veterinario.builder().id(3).build())
                .build());
    }

    @Override
    public Collection<Tratamiento> searchAll() {
        return new ArrayList<>(tablaTratamientos.values());
    }

    @Override
    public Tratamiento searchById(Integer id) {
        return tablaTratamientos.get(id);
    }

    /**
     * Guarda el tratamiento asignándole el siguiente id disponible.
     * Se calcula el id máximo existente + 1 para evitar colisiones
     * cuando los registros no están contiguos, y se asigna al objeto
     * antes de insertarlo en el mapa.
     */
    @Override
    public void save(Tratamiento tratamiento) {
        int lastId = tablaTratamientos.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        tratamiento.setId(lastId + 1);
        tablaTratamientos.put(tratamiento.getId(), tratamiento);
    }
}

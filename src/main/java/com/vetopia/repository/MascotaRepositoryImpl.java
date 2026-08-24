package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Mascota;

/**
 * CAPA REPOSITORIO / DAO - Implementación en memoria
 * Simula la persistencia de datos usando un HashMap (llave = id,
 * valor = Mascota), a modo de tabla de base de datos. La anotación
 * {@code @Repository} registra la clase como bean de acceso a datos,
 * habilita la traducción de excepciones de persistencia y permite que
 * Spring la inyecte automáticamente en el Service.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class MascotaRepositoryImpl implements MascotaRepository {

    /** "Tabla" mascotas simulada en memoria (llave = id, valor = Mascota). */
    private final Map<Integer, Mascota> tablaMascotas = new HashMap<>();

    /**
     * Precarga registros de ejemplo, equivalentes a los datos mock
     * del proyecto JavaScript original (js/data/mascotas.data.js).
     * El campo imagen permanece en null: las vistas muestran un
     * placeholder hasta que se asignen URLs manualmente.
     */
    public MascotaRepositoryImpl() {
        tablaMascotas.put(1, new Mascota(1, "Max", "Perro", "Labrador Retriever",
                5.0, "Macho", null, 32.0, "Dorado", LocalDate.parse("2026-01-12")));
        tablaMascotas.put(2, new Mascota(2, "Luna", "Gato", "Siamés",
                3.0, "Hembra", null, 4.2, "Blanco y café", LocalDate.parse("2026-02-03")));
        tablaMascotas.put(3, new Mascota(3, "Rocky", "Perro", "Bulldog Francés",
                2.0, "Macho", null, 11.5, "Gris atigrado", LocalDate.parse("2025-11-20")));
        tablaMascotas.put(4, new Mascota(4, "Mía", "Gato", "Persa",
                0.5, "Hembra", null, 2.1, "Blanco", LocalDate.parse("2026-06-18")));
        tablaMascotas.put(5, new Mascota(5, "Toby", "Perro", "Poodle",
                8.0, "Macho", null, 7.8, "Blanco", LocalDate.parse("2025-08-05")));
        tablaMascotas.put(6, new Mascota(6, "Coco", "Perro", "Schnauzer Miniatura",
                4.0, "Hembra", null, 6.4, "Sal y pimienta", LocalDate.parse("2026-03-27")));
    }

    /**
     * {@inheritDoc}
     * Se devuelve una copia de los valores del HashMap (Collection) para
     * que las capas superiores no manipulen directamente los datos
     * almacenados.
     */
    @Override
    public Collection<Mascota> searchAll() {
        return new ArrayList<>(tablaMascotas.values());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mascota searchById(Integer id) {
        return tablaMascotas.get(id);
    }
}

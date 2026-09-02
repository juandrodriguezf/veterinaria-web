package com.vetopia.repository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Mascota;

/**
 * Simula la persistencia de datos usando un HashMap (llave = id,
 * valor = Mascota), a modo de tabla de base de datos. La anotación
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
     *
     * Cada mascota queda conectada por llave foránea (duenoId) al dueño
     * correspondiente de DuenoRepositoryImpl.
     */
    public MascotaRepositoryImpl() {
        tablaMascotas.put(1, new Mascota(1, "Max", "Perro", "Doberman",
                5.0, "Macho", "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw", 32.0, "Negro", LocalDate.parse("2026-01-12"), 1, "Activo"));
        tablaMascotas.put(2, new Mascota(2, "Luna", "Gato", "Siamés",
                3.0, "Hembra", "https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0", 4.2, "Blanco y café", LocalDate.parse("2026-02-03"), 2, "Activo"));
        tablaMascotas.put(3, new Mascota(3, "Rocky", "Perro", "Bulldog Francés",
                0.6, "Macho", "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn", 7.5, "Gris atigrado", LocalDate.parse("2025-11-20"), 3, "Activo"));
    }

    /**
     * Se devuelve una copia de los valores del HashMap (Collection) para
     * que las capas superiores no manipulen directamente los datos
     * almacenados.
     */
    @Override
    public Collection<Mascota> searchAll() {
        return new ArrayList<>(tablaMascotas.values());
    }

    @Override
    public Mascota searchById(Integer id) {
        return tablaMascotas.get(id);
    }

    /**
     * Guarda la mascota en el HashMap. Si el objeto ya trae id (viene del
     * formulario de edición) se actualiza el registro existente; si no,
     * se le asigna el siguiente id disponible .
     */
    @Override
    public void save(Mascota mascota) {
        if (mascota.getId() != null) {
            tablaMascotas.put(mascota.getId(), mascota);
        } else {
            int lastId = tablaMascotas.keySet().stream()
                    .max(Integer::compareTo)
                    .orElse(0);
            mascota.setId(lastId + 1);
            tablaMascotas.put(mascota.getId(), mascota);
        }
    }

    /**
     * Actualiza el estado de la mascota identificada por id. Si el id
     * no existe, no se realiza ningún cambio.
     */
    @Override
    public void cambiarEstado(Integer id, String estado) {
        Mascota mascota = tablaMascotas.get(id);
        if (mascota != null) {
            mascota.setEstado(estado);
        }
    }

    /**
     * Elimina el registro de la mascota del HashMap (borrado físico, a
     * diferencia de cambiarEstado que realiza un borrado lógico). Se usa
     * al eliminar un dueño para no dejar mascotas huérfanas.
     */
    @Override
    public void eliminar(Integer id) {
        tablaMascotas.remove(id);
    }
}

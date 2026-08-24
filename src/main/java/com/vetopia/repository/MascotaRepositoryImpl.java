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
     */
    public MascotaRepositoryImpl() {
        tablaMascotas.put(1, new Mascota(1, "Max", "Perro", "Doberman",
                5.0, "Macho", "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw", 32.0, "Negro", LocalDate.parse("2026-01-12")));
        tablaMascotas.put(2, new Mascota(2, "Luna", "Gato", "Siamés",
                3.0, "Hembra", "https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0", 4.2, "Blanco y café", LocalDate.parse("2026-02-03")));
        tablaMascotas.put(3, new Mascota(3, "Rocky", "Perro", "Bulldog Francés",
                0.6, "Macho", "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn", 7.5, "Gris atigrado", LocalDate.parse("2025-11-20")));
        tablaMascotas.put(4, new Mascota(4, "Mía", "Gato", "Persa",
                2.0, "Hembra", "https://imgs.search.brave.com/fjekegE2bfE6YFBudfAXd0BnUy57te0W-hyUlPEJRLY/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvNTE4/ODAwMTMyL2VzL2Zv/dG8vYmxhbmNvLWdh/dG9zLXBlcnNhcy5q/cGc_cz02MTJ4NjEy/Jnc9MCZrPTIwJmM9/dkFXNkxkRzFXZ2Ja/NVZhUjJrRzZiUjhN/YlVRa2VkRW8zcEtr/UV9XY2ROZz0", 2.1, "Blanco", LocalDate.parse("2026-06-18")));
        tablaMascotas.put(5, new Mascota(5, "Toby", "Perro", "Husky Siberiano",
                8.0, "Macho", "https://imgs.search.brave.com/Jwd2wvc3HLvvtzO8kvSviHPTp25svAFg5zURXLjFDzA/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTM5/MTc3NDU4Ni9waG90/by9iZWF1dGlmdWwt/c2liZXJpYW4taHVz/a3ktZG9nLXdpdGgt/Ymx1ZS1hbmQtYnJv/d24tZXllcy1vbi10/aGUtYmFja2dyb3Vu/ZC1vZi1ibHVycmVk/LWJsdWUtc25vdy5q/cGc_cz02MTJ4NjEy/Jnc9MCZrPTIwJmM9/Y05tNXJ2Njk2UkpX/eWc4ejJxMzluVFdt/b3dFWU9lU1pDM0RS/NE51MGxKVT0", 14.8, "Blanco", LocalDate.parse("2025-08-05")));
        tablaMascotas.put(6, new Mascota(6, "Coco", "Perro", "Schnauzer Miniatura",
                4.0, "Hembra", "https://imgs.search.brave.com/5IMBmXO2HN5_wG-fpZ6TrchcmoxBbC_ZKd1ovH8XL7k/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly93d3cu/ZG9nZm9vZGFkdmlz/b3IuY29tL3dwLWNv/bnRlbnQvdXBsb2Fk/cy8yMDI1LzAyL01p/bmlhdHVyZS1TY2hu/YXV6ZXIuanBn", 6.4, "Sal y pimienta", LocalDate.parse("2026-03-27")));
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
}

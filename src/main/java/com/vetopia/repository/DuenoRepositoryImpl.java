package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Dueno;

/**
 * Simula la persistencia de dueños usando un HashMap
 * (llave = id, valor = Dueno), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class DuenoRepositoryImpl implements DuenoRepository {

    /** "Tabla" dueños simulada en memoria. */
    private final Map<Integer, Dueno> tablaDuenos = new HashMap<>();

    /**
     * Precarga registros de ejemplo de dueños.
     */
    public DuenoRepositoryImpl() {
        tablaDuenos.put(1, new Dueno(1, "1020304050", "Ana Rodríguez", "ana@correo.com", "dueño123", "3001234567", "Activo"));
        tablaDuenos.put(2, new Dueno(2, "1020304051", "Pedro Gómez", "pedro@correo.com", "dueño123", "3007654321", "Activo"));
        tablaDuenos.put(3, new Dueno(3, "1020304052", "María López", "maria@correo.com", "dueño123", "3010001111", "Inactivo"));
    }

    @Override
    public Collection<Dueno> searchAll() {
        return new ArrayList<>(tablaDuenos.values());
    }

    @Override
    public Dueno searchById(Integer id) {
        return tablaDuenos.get(id);
    }

    /**
     * Guarda el dueño asignándole el siguiente id disponible.
     * Se calcula el id máximo existente + 1 para evitar colisiones
     * cuando los registros no están contiguos, y se asigna al objeto
     * antes de insertarlo en el mapa.
     */
    @Override
    public void save(Dueno dueno) {
        int lastId = tablaDuenos.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        dueno.setId(lastId + 1);
        tablaDuenos.put(dueno.getId(), dueno);
    }

    @Override
    public Dueno searchByCorreoYContrasena(String correo, String contrasena) {
        return tablaDuenos.values().stream()
                .filter(d -> d.getCorreo() != null && d.getCorreo().equalsIgnoreCase(correo)
                        && d.getContrasena() != null && d.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}
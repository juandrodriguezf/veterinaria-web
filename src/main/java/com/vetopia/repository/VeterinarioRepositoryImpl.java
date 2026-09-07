package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Veterinario;

/**
 * Simula la persistencia de veterinarios usando un HashMap
 * (llave = id, valor = Veterinario), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class VeterinarioRepositoryImpl implements VeterinarioRepository {

    /** "Tabla" veterinarios simulada en memoria. */
    private final Map<Integer, Veterinario> tablaVeterinarios = new HashMap<>();

    /**
     * Precarga registros de ejemplo de veterinarios.
     */
    public VeterinarioRepositoryImpl() {
        tablaVeterinarios.put(1, Veterinario.builder().id(1).cedula("1002003001").contrasena("vet123")
                .correo("carlos.gutierrez@vetopia.com").especialidad("Medicina General")
                .numeroAtenciones(0).nombre("Carlos Gutiérrez").estado("Activo")
                .urlFoto("https://example.com/foto-carlos.jpg").build());
        tablaVeterinarios.put(2, Veterinario.builder().id(2).cedula("1002003002").contrasena("vet123")
                .correo("laura.mendez@vetopia.com").especialidad("Cirugía")
                .numeroAtenciones(0).nombre("Laura Méndez").estado("Activo")
                .build());
        tablaVeterinarios.put(3, Veterinario.builder().id(3).cedula("1002003003").contrasena("vet123")
                .correo("jorge.santana@vetopia.com").especialidad("Dermatología")
                .numeroAtenciones(0).nombre("Jorge Santana").estado("Inactivo")
                .build());
    }

    @Override
    public Collection<Veterinario> searchAll() {
        return new ArrayList<>(tablaVeterinarios.values());
    }

    @Override
    public Veterinario searchById(Integer id) {
        return tablaVeterinarios.get(id);
    }

    @Override
    public void save(Veterinario veterinario) {
        int lastId = tablaVeterinarios.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        veterinario.setId(lastId + 1);
        tablaVeterinarios.put(veterinario.getId(), veterinario);
    }

    @Override
    public Veterinario searchByCorreoYContrasena(String correo, String contrasena) {
        return tablaVeterinarios.values().stream()
                .filter(v -> v.getCorreo() != null && v.getCorreo().equalsIgnoreCase(correo)
                        && v.getContrasena() != null && v.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}

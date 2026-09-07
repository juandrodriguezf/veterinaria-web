package com.vetopia.repository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.vetopia.entities.Administrador;

/**
 * Simula la persistencia de administradores usando un HashMap
 * (llave = id, valor = Administrador), a modo de tabla de base de datos.
 * La anotación @Repository la registra en el contexto de Spring.
 *
 * Cuando se conecte una base de datos real, solo esta implementación
 * cambia; Service y Controller no requieren modificaciones.
 */
@Repository
public class AdministradorRepositoryImpl implements AdministradorRepository {

    /** "Tabla" administradores simulada en memoria. */
    private final Map<Integer, Administrador> tablaAdministradores = new HashMap<>();

    /**
     * Precarga registros de ejemplo de administradores.
     */
    public AdministradorRepositoryImpl() {
        tablaAdministradores.put(1, Administrador.builder().id(1).cedula("1000000001")
                .correo("admin@vetopia.com").contrasena("admin123").nombre("Dirección Vetopia").build());
        tablaAdministradores.put(2, Administrador.builder().id(2).cedula("1000000002")
                .correo("gerencia@vetopia.com").contrasena("gerencia123").nombre("Gerencia General").build());
        tablaAdministradores.put(3, Administrador.builder().id(3).cedula("1000000003")
                .correo("finanzas@vetopia.com").contrasena("finanzas123").nombre("Finanzas").build());
    }

    @Override
    public Collection<Administrador> searchAll() {
        return new ArrayList<>(tablaAdministradores.values());
    }

    @Override
    public Administrador searchById(Integer id) {
        return tablaAdministradores.get(id);
    }

    @Override
    public void save(Administrador administrador) {
        int lastId = tablaAdministradores.keySet().stream()
                .max(Integer::compareTo)
                .orElse(0);
        administrador.setId(lastId + 1);
        tablaAdministradores.put(administrador.getId(), administrador);
    }

    @Override
    public Administrador searchByCorreoYContrasena(String correo, String contrasena) {
        return tablaAdministradores.values().stream()
                .filter(a -> a.getCorreo() != null && a.getCorreo().equalsIgnoreCase(correo)
                        && a.getContrasena() != null && a.getContrasena().equals(contrasena))
                .findFirst()
                .orElse(null);
    }
}

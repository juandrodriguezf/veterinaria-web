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
        tablaDuenos.put(1, Dueno.builder().id(1).cedula("1020304050").nombre("Ana Rodríguez")
                .correo("ana@correo.com").contrasena("dueño123").celular("3001234567").estado("Activo").build());
        tablaDuenos.put(2, Dueno.builder().id(2).cedula("1020304051").nombre("Pedro Gómez")
                .correo("pedro@correo.com").contrasena("dueño123").celular("3007654321").estado("Activo").build());
        tablaDuenos.put(3, Dueno.builder().id(3).cedula("1020304052").nombre("María López")
                .correo("maria@correo.com").contrasena("dueño123").celular("3010001111").estado("Inactivo").build());
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
     * Guarda el dueño en el HashMap. Si el objeto ya trae id (viene del
     * formulario de edición) se actualiza el registro existente; si no,
     * se le asigna el siguiente id disponible .
     */
    @Override
    public void save(Dueno dueno) {
        if (dueno.getId() != null) {
            tablaDuenos.put(dueno.getId(), dueno);
        } else {
            int lastId = tablaDuenos.keySet().stream()
                    .max(Integer::compareTo)
                    .orElse(0);
            dueno.setId(lastId + 1);
            tablaDuenos.put(dueno.getId(), dueno);
        }
    }

    /**
     * Busca un dueño por su correo y contraseña (credenciales de inicio
     * de sesión). Solo se aceptan los dueños con estado "Activo": un
     * cliente desactivado por el veterinario no puede iniciar sesión.
     * Devuelve null si no existe coincidencia.
     */
    @Override
    public Dueno searchByCorreoYContrasena(String correo, String contrasena) {
        return tablaDuenos.values().stream()
                .filter(d -> d.getCorreo() != null && d.getCorreo().equalsIgnoreCase(correo)
                        && d.getContrasena() != null && d.getContrasena().equals(contrasena)
                        && "Activo".equalsIgnoreCase(d.getEstado()))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void cambiarEstado(Integer id, String estado) {
        Dueno dueno = tablaDuenos.get(id);
        if (dueno != null) {
            dueno.setEstado(estado);
        }
    }

    /**
     * Elimina el registro del dueño del HashMap (borrado físico, a
     * diferencia de cambiarEstado que realiza un borrado lógico). Si el
     * id no existe, remove() no altera la "tabla".
     */
    @Override
    public void eliminar(Integer id) {
        tablaDuenos.remove(id);
    }
}
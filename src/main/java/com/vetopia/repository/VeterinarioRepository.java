package com.vetopia.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Veterinario;

/**
 * REPOSITORIO - Veterinario
 * Operaciones estándar heredadas de JpaRepository más consultas derivadas
 * por nombre de propiedad. La búsqueda del portal del administrador cubre
 * la cédula o el nombre, y el login exige que el veterinario esté activo:
 * un veterinario desactivado no puede ingresar al sistema.
 */
@Repository
public interface VeterinarioRepository extends JpaRepository<Veterinario, Integer> {

    /**
     * Veterinario por sus credenciales y estado: solo los veterinarios con
     * estado "Activo" pueden iniciar sesión (misma política que Dueno).
     */
    Veterinario findByCorreoIgnoreCaseAndContrasenaAndEstado(String correo,
            String contrasena,
            String estado);

    /**
     * Veterinario por correo: soporta la validación de unicidad en el
     * service (un correo no puede repetirse entre veterinarios).
     */
    Veterinario findByCorreoIgnoreCase(String correo);

    /**
     * Veterinario por cédula: soporta la validación de unicidad en el
     * service y la búsqueda exacta por documento.
     */
    Veterinario findByCedula(String cedula);

    /**
     * Busca veterinarios cuya cédula o nombre contengan el término dado,
     * sin distinguir mayúsculas/minúsculas (barra de búsqueda del portal
     * del administrador). Si el término es nulo o vacío, el service
     * devuelve el listado completo.
     */
    List<Veterinario> findByCedulaContainingIgnoreCaseOrNombreContainingIgnoreCase(
            String cedula, String nombre);
}

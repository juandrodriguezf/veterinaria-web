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
     * Veterinario por correo: soporta la validación de unicidad global
     * entre clientes, veterinarios y administradores.
     */
    Veterinario findByCorreoIgnoreCase(String correo);

    /**
     * Veterinario por cédula: soporta la validación de unicidad global
     * entre clientes, veterinarios y administradores, además de la
     * búsqueda exacta por documento.
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

    /**
     * Cuenta los veterinarios en un estado determinado (Activo/Inactivo).
     * Alimenta los KPIs del dashboard del administrador.
     */
    long countByEstado(String estado);
}

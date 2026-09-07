package com.vetopia.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vetopia.entities.Dueno;

/**
 * REPOSITORIO - Dueno
 * Operaciones estándar heredadas de JpaRepository (Spring Data genera su
 * implementación) más consultas derivadas por nombre de propiedad.
 */
@Repository
public interface DuenoRepository extends JpaRepository<Dueno, Integer> {

    /**
     * Cliente por sus credenciales y activo: solo los dueños con estado
     * "Activo" pueden iniciar sesión (un cliente desactivado por el
     * veterinario no puede usar el portal). Spring Data traduce el nombre
     * del método a la consulta (el correo se compara sin importar
     * mayúsculas/minúsculas).
     */
    Dueno findByCorreoIgnoreCaseAndContrasenaAndEstado(String correo,
                                                        String contrasena,
                                                        String estado);

    /**
     * Cliente por correo: soporta la validación de unicidad en
     * el service (un correo no puede repetirse entre clientes).
     */
    Dueno findByCorreoIgnoreCase(String correo);

    /**
     * Cliente por cédula: soporta la validación de unicidad en
     * el service (una cédula no puede repetirse).
     */
    Dueno findByCedula(String cedula);
}

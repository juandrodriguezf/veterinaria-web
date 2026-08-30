package com.vetopia.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * ENTIDAD - Administrador
 * Representa al administrador de Vetopia. No contiene lógica de acceso a
 * datos ni de presentación, solo el estado y las reglas propias de la
 * entidad.
 *
 * Lombok genera automáticamente getters/setters (@Data), el constructor
 * vacío (@NoArgsConstructor) y el constructor con todos los atributos
 * (@AllArgsConstructor), eliminando el código boilerplate.
 *
 * Según el diagrama de clases (docs/diagrams/script-class-diagram.txt),
 * además gestiona veterinarios y genera el dashboard (lógica que se
 * implementará en la capa de servicio).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Administrador {

    /** Identificador único del administrador. */
    private Integer id;

    /** Número de documento de identidad del administrador. */
    private String cedula;

    /** Correo electrónico del administrador. */
    private String correo;

    /** Contraseña de acceso del administrador. */
    private String contrasena;

    /** Nombre completo del administrador. */
    private String nombre;
}

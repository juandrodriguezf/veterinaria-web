package com.vetopia.entities;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * ENTIDAD - UserEntity
 * Cuenta de acceso unificada del sistema. Los tres tipos de usuario de
 * Vetopia (administrador, veterinario y cliente) se autentican con esta
 * misma tabla: el perfil de negocio vive en su entidad propia y se enlaza
 * con la cuenta mediante una relación OneToOne.
 *
 * La contraseña se guarda cifrada (BCrypt) y nunca se serializa en la API.
 * Los permisos se resuelven con la relación ManyToMany contra Role, de modo
 * que un usuario puede tener uno o varios roles (ROLE_ADMINISTRADOR,
 * ROLE_VETERINARIO, ROLE_CLIENTE).
 */
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(exclude = {"roles"})
public class UserEntity {

    /** Identificador único de la cuenta (autogenerado por la base de datos). */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    /**
     * Usuario de acceso: el correo electrónico de la persona. Es único en
     * todo el sistema y se compara sin distinguir mayúsculas/minúsculas.
     */
    @Column(nullable = false, unique = true, length = 150)
    private String username;

    /** Contraseña cifrada con BCrypt (nunca viaja en las respuestas). */
    @Column(nullable = false, length = 255)
    private String password;

    /**
     * Indica si la cuenta puede iniciar sesión. Se sincroniza con el estado
     * del perfil: un veterinario o cliente inactivo no debe autenticarse.
     */
    @Column(nullable = false)
    private boolean activo;

    /**
     * Roles de la cuenta (ManyToMany con la tabla intermedia user_roles).
     * Se cargan de forma anticipada porque la autenticación necesita las
     * autoridades desde el primer acceso.
     */
    @ManyToMany
    @JoinTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private List<Role> roles = new ArrayList<>();
}

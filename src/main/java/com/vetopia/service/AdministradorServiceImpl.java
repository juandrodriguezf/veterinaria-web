package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.UserEntity;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.AdministradorRepository;

/**
 * CAPA SERVICIO - Implementación de AdministradorService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class AdministradorServiceImpl implements AdministradorService {

    /** Repositorio de administradores (flujo obligatorio: Service -> Repository). */
    @Autowired
    private AdministradorRepository administradorRepository;

    /** Validador de cédula y correo únicos entre las cuentas del sistema. */
    @Autowired
    private IdentificadorUnicoService identificadorUnicoService;

    /** Cuenta unificada de acceso (users/roles). */
    @Autowired
    private CuentaUsuarioService cuentaUsuarioService;

    @Override
    public List<Administrador> listarAdministradores() {
        return List.copyOf(administradorRepository.findAll());
    }

    @Override
    public Administrador obtenerAdministradorPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del administrador.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return administradorRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún administrador registrado con el identificador \"" + id + "\"."));
    }

    /**
     * {@inheritDoc}
     * La cédula y el correo identifican la cuenta en el login, así que se
     * validan contra clientes, veterinarios y administradores antes de
     * guardar. El validador central ignora este mismo registro al editar y
     * reserva también los identificadores de cuentas inactivas.
     */
    @Override
    public void guardar(Administrador administrador) {
        administrador.setCedula(
                administrador.getCedula() == null ? null : administrador.getCedula().trim());
        administrador.setCorreo(
                administrador.getCorreo() == null ? null : administrador.getCorreo().trim());
        identificadorUnicoService.validarAdministrador(
                administrador.getCedula(), administrador.getCorreo(), administrador.getId());

        // En edición se recuperan la clave y la cuenta que el formulario no
        // devuelve; en creación se usa la clave provisional del equipo.
        Administrador actual = administrador.getId() == null
                ? null
                : administradorRepository.findById(administrador.getId()).orElseThrow(
                        () -> new RecursoNoEncontradoException(
                                "No encontramos ningún administrador registrado con el identificador \""
                                        + administrador.getId() + "\"."));
        String clave = administrador.getContrasena();
        boolean claveNueva = clave != null && !clave.isBlank();
        if (actual == null && !claveNueva) {
            clave = "admin123";
            claveNueva = true;
        }

        // Cuenta unificada: correo como usuario, clave cifrada y rol.
        UserEntity cuenta = actual != null ? actual.getUser() : null;
        if (cuenta == null) {
            cuenta = cuentaUsuarioService.construir(
                    administrador.getCorreo(), claveNueva ? clave : "admin123",
                    CuentaUsuarioService.ROL_ADMINISTRADOR);
        } else {
            cuentaUsuarioService.sincronizar(
                    cuenta, administrador.getCorreo(), claveNueva ? clave : null,
                    CuentaUsuarioService.ROL_ADMINISTRADOR);
        }
        cuentaUsuarioService.cambiarEstado(cuenta, true);
        administrador.setUser(cuenta);
        administradorRepository.save(administrador);
    }
}

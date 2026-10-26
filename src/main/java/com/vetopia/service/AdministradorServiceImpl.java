package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
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
        administradorRepository.save(administrador);
    }

    @Override
    public Administrador obtenerPorCorreoYContrasena(String identificador, String contrasena) {
        // El formulario acepta correo o cédula: se busca primero por correo y,
        // si no hay coincidencia, por documento.
        Administrador porCorreo = administradorRepository
                .findByCorreoIgnoreCaseAndContrasena(identificador, contrasena);
        return porCorreo != null
                ? porCorreo
                : administradorRepository.findByCedulaAndContrasena(identificador, contrasena);
    }
}

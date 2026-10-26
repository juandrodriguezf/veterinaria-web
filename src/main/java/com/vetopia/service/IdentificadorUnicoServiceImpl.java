package com.vetopia.service;

import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.VeterinarioRepository;

/**
 * CAPA SERVICIO - Implementación de IdentificadorUnicoService
 * Consulta las tres tablas de cuentas antes de guardar. La comparación de
 * correo ignora mayúsculas/minúsculas y ambos valores se normalizan con
 * trim(), de modo que un espacio accidental no permita crear un duplicado.
 *
 * Los identificadores de cuentas inactivas también se reservan: reutilizar
 * una cédula o un correo histórico haría ambiguo el login y mezclaría dos
 * usuarios distintos.
 */
@Service
public class IdentificadorUnicoServiceImpl implements IdentificadorUnicoService {

    /** Repositorio de clientes. */
    @Autowired
    private DuenoRepository duenoRepository;

    /** Repositorio de veterinarios. */
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    /** Repositorio de administradores. */
    @Autowired
    private AdministradorRepository administradorRepository;

    @Override
    public void validarCliente(String cedula, String correo, Integer idActual) {
        String cedulaNormalizada = normalizar(cedula);
        String correoNormalizado = normalizar(correo);
        validarContraDueno(cedulaNormalizada, correoNormalizado, idActual);
        validarContraVeterinario(cedulaNormalizada, correoNormalizado, null);
        validarContraAdministrador(cedulaNormalizada, correoNormalizado, null);
    }

    @Override
    public void validarVeterinario(String cedula, String correo, Integer idActual) {
        String cedulaNormalizada = normalizar(cedula);
        String correoNormalizado = normalizar(correo);
        validarContraDueno(cedulaNormalizada, correoNormalizado, null);
        validarContraVeterinario(cedulaNormalizada, correoNormalizado, idActual);
        validarContraAdministrador(cedulaNormalizada, correoNormalizado, null);
    }

    @Override
    public void validarAdministrador(String cedula, String correo, Integer idActual) {
        String cedulaNormalizada = normalizar(cedula);
        String correoNormalizado = normalizar(correo);
        validarContraDueno(cedulaNormalizada, correoNormalizado, null);
        validarContraVeterinario(cedulaNormalizada, correoNormalizado, null);
        validarContraAdministrador(cedulaNormalizada, correoNormalizado, idActual);
    }

    /**
     * Comprueba el correo y la cédula contra los clientes. El id permitido
     * solo se usa cuando la cuenta que se guarda también es un cliente.
     */
    private void validarContraDueno(String cedula, String correo, Integer idPermitido) {
        if (correo != null && !correo.isBlank()) {
            Dueno porCorreo = duenoRepository.findByCorreoIgnoreCase(correo);
            if (porCorreo != null && !Objects.equals(porCorreo.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con el correo \"" + correo + "\".");
            }
        }
        if (cedula != null && !cedula.isBlank()) {
            Dueno porCedula = duenoRepository.findByCedula(cedula);
            if (porCedula != null && !Objects.equals(porCedula.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con la cédula \"" + cedula + "\".");
            }
        }
    }

    /** Comprueba el correo y la cédula contra los veterinarios. */
    private void validarContraVeterinario(String cedula, String correo, Integer idPermitido) {
        if (correo != null && !correo.isBlank()) {
            Veterinario porCorreo = veterinarioRepository.findByCorreoIgnoreCase(correo);
            if (porCorreo != null && !Objects.equals(porCorreo.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con el correo \"" + correo + "\".");
            }
        }
        if (cedula != null && !cedula.isBlank()) {
            Veterinario porCedula = veterinarioRepository.findByCedula(cedula);
            if (porCedula != null && !Objects.equals(porCedula.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con la cédula \"" + cedula + "\".");
            }
        }
    }

    /** Comprueba el correo y la cédula contra los administradores. */
    private void validarContraAdministrador(String cedula, String correo, Integer idPermitido) {
        if (correo != null && !correo.isBlank()) {
            Administrador porCorreo = administradorRepository.findByCorreoIgnoreCase(correo);
            if (porCorreo != null && !Objects.equals(porCorreo.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con el correo \"" + correo + "\".");
            }
        }
        if (cedula != null && !cedula.isBlank()) {
            Administrador porCedula = administradorRepository.findByCedula(cedula);
            if (porCedula != null && !Objects.equals(porCedula.getId(), idPermitido)) {
                throw new IllegalStateException(
                        "Ya existe una cuenta con la cédula \"" + cedula + "\".");
            }
        }
    }

    /** Elimina espacios accidentales del identificador recibido. */
    private String normalizar(String valor) {
        return valor == null ? null : valor.trim();
    }
}

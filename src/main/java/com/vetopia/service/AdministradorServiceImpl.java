package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Administrador;
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

    @Override
    public List<Administrador> listarAdministradores() {
        return List.copyOf(administradorRepository.searchAll());
    }

    @Override
    public Administrador obtenerAdministradorPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del administrador.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return administradorRepository.searchById(id);
    }

    @Override
    public void guardar(Administrador administrador) {
        administradorRepository.save(administrador);
    }

    @Override
    public Administrador obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return administradorRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}

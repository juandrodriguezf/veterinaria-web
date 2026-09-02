package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Dueno;
import com.vetopia.repository.DuenoRepository;

/**
 * CAPA SERVICIO - Implementación de DuenoService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class DuenoServiceImpl implements DuenoService {

    /** Repositorio de dueños (flujo obligatorio: Service -> Repository). */
    @Autowired
    private DuenoRepository duenoRepository;

    @Override
    public List<Dueno> listarDuenos() {
        return List.copyOf(duenoRepository.searchAll());
    }

    @Override
    public Dueno obtenerDuenoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return duenoRepository.searchById(id);
    }

    @Override
    public void guardar(Dueno dueno) {
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return duenoRepository.searchByCorreoYContrasena(correo, contrasena);
    }

    @Override
    public void cambiarEstado(Integer id, String estado) {
        duenoRepository.cambiarEstado(id, estado);
    }
}

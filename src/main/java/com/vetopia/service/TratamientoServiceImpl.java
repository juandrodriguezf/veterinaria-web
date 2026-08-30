package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Tratamiento;
import com.vetopia.repository.TratamientoRepository;

/**
 * CAPA SERVICIO - Implementación de TratamientoService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class TratamientoServiceImpl implements TratamientoService {

    /** Repositorio de tratamientos (flujo obligatorio: Service -> Repository). */
    @Autowired
    private TratamientoRepository tratamientoRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return List.copyOf(tratamientoRepository.searchAll());
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del tratamiento.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return tratamientoRepository.searchById(id);
    }

    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }
}

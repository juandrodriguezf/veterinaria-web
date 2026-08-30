package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.repository.DrogaRepository;

/**
 * CAPA SERVICIO - Implementación de DrogaService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class DrogaServiceImpl implements DrogaService {

    /** Repositorio de drogas (flujo obligatorio: Service -> Repository). */
    @Autowired
    private DrogaRepository drogaRepository;

    @Override
    public List<Droga> listarDrogas() {
        return List.copyOf(drogaRepository.searchAll());
    }

    @Override
    public Droga obtenerDrogaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la droga.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return drogaRepository.searchById(id);
    }

    @Override
    public void guardar(Droga droga) {
        drogaRepository.save(droga);
    }
}

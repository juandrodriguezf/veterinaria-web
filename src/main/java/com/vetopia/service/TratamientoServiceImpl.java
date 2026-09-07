package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
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

    /** Servicio de drogas (inventario que se descuenta al asignar). */
    @Autowired
    private DrogaService drogaService;

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

    /**
     * {@inheritDoc}
     * Mientras el proyecto no maneje sesión, el veterinario responsable
     * queda fijo (id 1), igual que en la versión anterior del controlador.
     */
    @Override
    public Droga asignar(Tratamiento tratamiento) {
        // Una asignación sin mascota no tiene sentido clínico ni a quién
        // cobrarse en la ficha.
        if (tratamiento == null || tratamiento.getMascotaId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar la mascota a tratar.");
        }
        // Sin medicamento no hay inventario que descontar.
        if (tratamiento.getDrogaId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar el medicamento.");
        }
        tratamiento.setVeterinarioId(1);
        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDrogaId());
        // El medicamento debe existir en el inventario para poder descontar.
        if (droga == null) {
            throw new IllegalStateException("El medicamento indicado no existe en el inventario.");
        }
        // Regla de inventario: cada tratamiento consume exactamente una
        // unidad; sin stock la asignación se rechaza y no se registra.
        int unidadesDescontadas = 1;
        int stockRestante = (droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles())
                - unidadesDescontadas;
        if (stockRestante < 0) {
            throw new IllegalStateException("No hay unidades disponibles del medicamento \""
                    + droga.getNombre() + "\".");
        }
        guardar(tratamiento);
        droga.setUnidadesDisponibles(stockRestante);
        drogaService.guardar(droga);
        return droga;
    }
}

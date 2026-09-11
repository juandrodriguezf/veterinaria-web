package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Tratamiento;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

import jakarta.transaction.Transactional;

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

    /** Repositorio de veterinarios (resuelve al responsable del tratamiento). */
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return List.copyOf(tratamientoRepository.findAll());
    }

    /**
     * {@inheritDoc}
     * Consulta derivada findByMascotaId + borrado uno a uno: el mismo
     * patrón del ejemplo para borrar por capas desde el service.
     */
    @Override
    @Transactional
    public void eliminarPorMascota(Integer mascotaId) {
        for (Tratamiento tratamiento : tratamientoRepository.findByMascotaId(mascotaId)) {
            tratamientoRepository.delete(tratamiento);
        }
    }

    @Override
    public Tratamiento obtenerTratamientoPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del tratamiento.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        // Si el id válido no existe en la base, el manejo global de
        // errores presenta la página amable con la causa exacta.
        return tratamientoRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún tratamiento registrado con el identificador \"" + id + "\"."));
    }

    @Override
    public void guardar(Tratamiento tratamiento) {
        tratamientoRepository.save(tratamiento);
    }

    /**
     * {@inheritDoc}
     * El veterinario responsable se resuelve desde el repositorio.
     */
    @Override
    public Droga asignar(Tratamiento tratamiento) {
        // Una asignación sin mascota no tiene sentido clínico ni a quién
        // cobrarse en la ficha. El formulario envía un "shell" con solo
        // el id de la mascota, que basta para delegar la resolución por id.
        if (tratamiento == null || tratamiento.getMascota() == null
                || tratamiento.getMascota().getId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar la mascota a tratar.");
        }
        // Sin medicamento no hay inventario que descontar.
        if (tratamiento.getDroga() == null || tratamiento.getDroga().getId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar el medicamento.");
        }
        // Mientras el proyecto no maneje sesión, el veterinario responsable
        // queda fijo (id 1), igual que en la versión anterior del controlador.
        tratamiento.setVeterinario(veterinarioRepository.findById(1).orElse(null));
        if (tratamiento.getVeterinario() == null) {
            throw new IllegalStateException("No se encontró el veterinario responsable del tratamiento.");
        }
        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDroga().getId());
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

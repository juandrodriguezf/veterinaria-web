package com.vetopia.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Mascota;
import com.vetopia.repository.MascotaRepository;

/**
 * CAPA SERVICIO - Implementación de MascotaService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por constructor (recomendación
 * oficial de Spring): garantiza dependencias obligatorias, facilita las
 * pruebas unitarias y mantiene la inmutabilidad del bean. Esta capa nunca
 * conoce los detalles de la persistencia, solo el contrato del repositorio.
 */
@Service
public class MascotaServiceImpl implements MascotaService {

    /** Repositorio de mascotas (flujo obligatorio: Service -> Repository). */
    @Autowired
    private MascotaRepository mascotaRepository;

    /**
     * {@inheritDoc}
     * El repositorio entrega una Lista con los valores del HashMap
     * (id -> Mascota); aquí se convierte a lista para que la vista la reciba.
     */
    @Override
    public List<Mascota> listarMascotas() {
        return List.copyOf(mascotaRepository.searchAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return mascotaRepository.searchById(id);
    }

    /**
     * {@inheritDoc}
     * Delega en el repositorio, que asigna el siguiente id disponible.
     */
    @Override
    public void guardar(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    /**
     * {@inheritDoc}
     * Delega en el repositorio, que actualiza el estado de la mascota.
     */
    @Override
    public void cambiarEstado(Integer id, String estado) {
        mascotaRepository.cambiarEstado(id, estado);
    }

    /**
     * {@inheritDoc}
     * Recorre las mascotas del repositorio y conserva las del dueño
     * indicado .
     */
    @Override
    public List<Mascota> listarMascotasPorDueno(Integer duenoId) {
        List<Mascota> resultado = new ArrayList<>();
        for (Mascota mascota : mascotaRepository.searchAll()) {
            if (duenoId.equals(mascota.getDuenoId())) {
                resultado.add(mascota);
            }
        }
        return resultado;
    }
}

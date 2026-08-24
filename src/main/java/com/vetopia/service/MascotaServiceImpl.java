package com.vetopia.service;

import java.text.Collator;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

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
    private final MascotaRepository mascotaRepository;

    /**
     * Constructor con inyección de dependencias: Spring busca el bean
     * marcado con @Repository que implemente MascotaRepository y lo
     * entrega automáticamente al crear este servicio.
     *
     * @param mascotaRepository repositorio de mascotas inyectado por Spring.
     */
    @Autowired
    public MascotaServiceImpl(MascotaRepository mascotaRepository) {
        this.mascotaRepository = mascotaRepository;
    }

    /**
     * {@inheritDoc}
     * El repositorio entrega una Collection con los valores del HashMap
     * (id -> Mascota); aquí se aplica la regla de negocio del ordenamiento
     * para que la vista reciba las filas ya ordenadas.
     */
    @Override
    public List<Mascota> listarMascotas() {
        // Orden alfabético en español (ignora mayúsculas y tildes).
        Collator collator = Collator.getInstance(new Locale("es", "CO"));
        collator.setStrength(Collator.PRIMARY);
        return mascotaRepository.searchAll().stream()
                .sorted(Comparator.comparing(Mascota::getNombre, collator))
                .toList();
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
}

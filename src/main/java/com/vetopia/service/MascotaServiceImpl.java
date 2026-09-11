package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Mascota;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;

import jakarta.transaction.Transactional;

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

    /** Servicio de tratamientos (se retiran antes de eliminar la mascota). */
    @Autowired
    private TratamientoService tratamientoService;

    /**
     * {@inheritDoc}
     * El repositorio entrega todas las filas de la tabla mascotas;
     * se recibe como lista inmutable para que la vista no la altere.
     */
    @Override
    public List<Mascota> listarMascotas() {
        return List.copyOf(mascotaRepository.findAll());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Mascota obtenerMascotaPorId(Integer id) {
        // Entrada inválida: sin id no hay nada que buscar en el repositorio.
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        // Los id válidos son positivos: cero o negativos solo llegan por
        // manipulación manual de la URL.
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        // Si el id válido no existe en la base, el manejo global de
        // errores presenta la página amable con la causa exacta.
        return mascotaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ninguna mascota registrada con el identificador \"" + id + "\"."));
    }

    /**
     * {@inheritDoc}
     * Spring Data JPA asigna el id automáticamente al insertar y hace
     * merge al actualizar (id presente).
     */
    @Override
    public void guardar(Mascota mascota) {
        mascotaRepository.save(mascota);
    }

    /**
     * {@inheritDoc}
     * Con JpaRepository el cambio de estado es cargar, mutar y guardar:
     * Hibernate genera el UPDATE de la fila correspondiente.
     */
    @Override
    public void cambiarEstado(Integer id, String estado) {
        Mascota mascota = mascotaRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ninguna mascota registrada con el identificador \"" + id + "\"."));
        mascota.setEstado(estado);
        mascotaRepository.save(mascota);
    }

    /**
     * {@inheritDoc}
     * Aplica la regla de negocio del borrado lógico: el estado nuevo es
     * el opuesto al actual (Activo <-> Inactivo).
     */
    @Override
    public String alternarEstado(Integer id) {
        // El id inválido o inexistente lo detecta obtenerMascotaPorId
        // con su excepción; la página de error comunica la causa.
        Mascota mascota = obtenerMascotaPorId(id);
        String nuevoEstado = "Inactivo".equals(mascota.getEstado()) ? "Activo" : "Inactivo";
        cambiarEstado(id, nuevoEstado);
        return nuevoEstado;
    }

    /**
     * {@inheritDoc}
     * Centraliza aquí la regla de guardado: el estado inicial de una
     * mascota nueva es "Activo" y toda mascota requiere dueño, pues sin
     * él quedaría huérfana y no aparecería en el portal de ningún cliente.
     */
    @Override
    public void guardarValidada(Mascota mascota) {
        // Datos ausentes: el formulario debió enviar la mascota completa.
        if (mascota == null) {
            throw new IllegalArgumentException("No se recibieron los datos de la mascota.");
        }
        // Regla del diagrama de clases: toda mascota nueva nace "Activa".
        if (mascota.getId() == null) {
            mascota.setEstado("Activo");
        }
        // Regla de la relación Dueno 1 -- 0..* Mascota: sin dueño la
        // mascota quedaría huérfana; se rechaza el guardado. El
        // formulario envía un "shell" con solo el id del dueño, que
        // basta para navegar la relación mientras se resuelve por id.
        if (mascota.getDueno() == null || mascota.getDueno().getId() == null) {
            throw new IllegalStateException("La mascota \"" + mascota.getNombre()
                    + "\" debe tener un dueño asignado.");
        }
        guardar(mascota);
    }

    /**
     * {@inheritDoc}
     * Además del id, valida la pertenencia de la mascota al dueño para
     * aislar los datos entre clientes del portal.
     */
    @Override
    public Mascota obtenerPropia(Integer id, Integer duenoId) {
        // Sin dueño no se puede validar la pertenencia de la mascota.
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el dueño de la mascota.");
        }
        // Si el id es válido pero la mascota no existe, obtenerMascotaPorId
        // ya lanzó la excepción con el mensaje exacto.
        Mascota mascota = obtenerMascotaPorId(id);
        // Aislamiento de datos: cada cliente solo consulta sus propias
        // mascotas, aunque conozca los id de las demás.
        if (!duenoId.equals(mascota.getDueno().getId())) {
            throw new IllegalStateException("Esta mascota no está registrada a tu nombre.");
        }
        return mascota;
    }

    /**
     * {@inheritDoc}
     * Borra por capas: primero los tratamientos de la mascota (listado
     * derivado + delete uno a uno) y después la mascota, para que la FK
     * tratamiento.mascota no rechace el borrado (mismo patrón del
     * ejemplo: el service se encarga de la cascada, no el DDL).
     */
    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        tratamientoService.eliminarPorMascota(id);
        mascotaRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * La consulta derivada findByDuenoId resuelve la ruta
     * mascota.dueno.id y conserva solo las mascotas del dueño indicado.
     */
    @Override
    public List<Mascota> listarMascotasPorDueno(Integer duenoId) {
        // Sin dueño la consulta no tiene sentido y produciría un
        // NullPointerException al comparar; se comunica como dato inválido.
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el dueño de la consulta.");
        }
        return mascotaRepository.findByDuenoId(duenoId);
    }
}

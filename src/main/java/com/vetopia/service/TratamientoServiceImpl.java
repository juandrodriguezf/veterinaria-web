package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;
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

    /** Repositorio de mascotas (verifica que la mascota esté activa). */
    @Autowired
    private MascotaRepository mascotaRepository;

    @Override
    public List<Tratamiento> listarTratamientos() {
        return List.copyOf(tratamientoRepository.findAll());
    }

    /**
     * {@inheritDoc}
     * Consulta derivada findByMascota_Id: como la colección de la entidad
     * viaja con @JsonIgnore en la API REST, el historial clínico se pide
     * aparte por el id de la mascota.
     */
    @Override
    public List<Tratamiento> listarTratamientosPorMascota(Integer mascotaId) {
        if (mascotaId == null) {
            throw new IllegalArgumentException("No se especificó la mascota del historial.");
        }
        return tratamientoRepository.findByMascota_Id(mascotaId);
    }

    /**
     * {@inheritDoc}
     * Consulta derivada findByMascota_Id y deja la referencia en nulo: el
     * tratamiento sobrevive a la eliminacion de la mascota, asi que el
     * historial clinico no se pierde. Hibernate confirma los UPDATE de la
     * FK antes del DELETE de la mascota dentro de la misma transaccion.
     */
    @Override
    @Transactional
    public void desvincularDeMascota(Integer mascotaId) {
        for (Tratamiento tratamiento : tratamientoRepository.findByMascota_Id(mascotaId)) {
            tratamiento.setMascota(null);
        }
    }

    /**
     * {@inheritDoc}
     * Igual que con la mascota, la FK del veterinario pasa a nulo y el
     * tratamiento se conserva: el historial clinico no se pierde cuando
     * el administrador retira a un veterinario del sistema.
     */
    @Override
    @Transactional
    public void desvincularDeVeterinario(Integer veterinarioId) {
        for (Tratamiento tratamiento : tratamientoRepository.findByVeterinario_Id(veterinarioId)) {
            tratamiento.setVeterinario(null);
        }
    }

    /**
     * {@inheritDoc}
     * Consulta derivada findByVeterinario_Id: la pestaña "Tratadas" agrupa
     * estos tratamientos por mascota para mostrar a quiénes ha atendido
     * el veterinario.
     */
    @Override
    public List<Tratamiento> listarTratamientosPorVeterinario(Integer veterinarioId) {
        if (veterinarioId == null) {
            throw new IllegalArgumentException("No se especificó el veterinario del historial.");
        }
        return tratamientoRepository.findByVeterinario_Id(veterinarioId);
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
     * El veterinario responsable llega en el cuerpo de la petición (solo su
     * id) y se resuelve desde el repositorio. La operación es transaccional:
     * el registro del tratamiento, el descuento del inventario y el contador
     * de atenciones del veterinario se confirman juntos o no se confirma
     * ninguno, para que no quede una asignación sin su descuento de stock ni
     * una atención sin registrar.
     */
    @Override
    @Transactional
    public Droga asignar(Tratamiento tratamiento) {
        // Una asignación sin mascota no tiene sentido clínico ni a quién
        // cobrarse en la ficha. El formulario envía un "shell" con solo
        // el id de la mascota, que basta para delegar la resolución por id.
        if (tratamiento == null || tratamiento.getMascota() == null
                || tratamiento.getMascota().getId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar la mascota a tratar.");
        }
        // La mascota debe existir y estar activa: la regla de negocio solo
        // permite dar tratamiento a mascotas hospitalizadas (activas).
        Mascota mascota = mascotaRepository.findById(tratamiento.getMascota().getId()).orElseThrow(
                () -> new RecursoNoEncontradoException("No encontramos ninguna mascota registrada con el identificador \""
                        + tratamiento.getMascota().getId() + "\"."));
        if (!"Activo".equals(mascota.getEstado())) {
            throw new IllegalStateException("La mascota \"" + mascota.getNombre()
                    + "\" está inactiva: solo se puede dar tratamiento a mascotas activas.");
        }
        tratamiento.setMascota(mascota);
        // Sin medicamento no hay inventario que descontar.
        if (tratamiento.getDroga() == null || tratamiento.getDroga().getId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar el medicamento.");
        }
        // El veterinario responsable llega desde la sesión del portal; el
        // backend valida que exista y que siga activo antes de registrar.
        if (tratamiento.getVeterinario() == null || tratamiento.getVeterinario().getId() == null) {
            throw new IllegalArgumentException("La asignación debe indicar el veterinario responsable.");
        }
        Integer veterinarioId = tratamiento.getVeterinario().getId();
        Veterinario veterinario = veterinarioRepository.findById(veterinarioId).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún veterinario registrado con el identificador \"" + veterinarioId + "\"."));
        if (!"Activo".equals(veterinario.getEstado())) {
            throw new IllegalStateException("El veterinario \"" + veterinario.getNombre()
                    + "\" está inactivo: no puede registrar tratamientos.");
        }
        tratamiento.setVeterinario(veterinario);
        Droga droga = drogaService.obtenerDrogaPorId(tratamiento.getDroga().getId());
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
        // La unidad que sale del stock se registra como vendida: así las
        // ventas y ganancias del dashboard reflejan cada tratamiento y el
        // top de medicamentos se calcula con datos reales.
        droga.setUnidadesVendidas(
                (droga.getUnidadesVendidas() == null ? 0 : droga.getUnidadesVendidas())
                        + unidadesDescontadas);
        drogaService.guardar(droga);
        // Cada tratamiento aplicado cuenta como una atención del
        // veterinario responsable: se acumula en su contador dentro de la
        // misma transacción (Veterinario.numeroAtenciones del diagrama).
        veterinario.setNumeroAtenciones(
                (veterinario.getNumeroAtenciones() == null ? 0 : veterinario.getNumeroAtenciones()) + 1);
        veterinarioRepository.save(veterinario);
        return droga;
    }
}

package com.vetopia.service;

import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Mascota;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;

import jakarta.transaction.Transactional;

/**
 * CAPA SERVICIO - Implementación de MascotaService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * Las dependencias se reciben por inyección por campo (@Autowired): Spring
 * las resuelve al crear el bean. Esta capa nunca conoce los detalles de la
 * persistencia, solo el contrato del repositorio.
 */
@Service
public class MascotaServiceImpl implements MascotaService {

    /** Repositorio de mascotas (flujo obligatorio: Service -> Repository). */
    @Autowired
    private MascotaRepository mascotaRepository;

    /** Repositorio de dueños (valida que el cliente exista al listar sus mascotas). */
    @Autowired
    private DuenoRepository duenoRepository;

    /** Servicio de tratamientos (se desvinculan antes de eliminar la mascota). */
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
        } else {
            // En edicion el formulario no envia los datos clinicos que no
            // edita; se recuperan del registro actual para no borrarlos.
            Mascota actual = obtenerMascotaPorId(mascota.getId());
            if (mascota.getFechaIngreso() == null) {
                mascota.setFechaIngreso(actual.getFechaIngreso());
            }
            if (mascota.getEnfermedad() == null) {
                mascota.setEnfermedad(actual.getEnfermedad());
            }
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
     * Antes de retirar la mascota se desvinculan sus tratamientos (la FK
     * tratamiento.mascota pasa a nulo), no se borran: el historial
     * clinico se conserva cuando se elimina la mascota o su dueno.
     */
    @Override
    @Transactional
    public void eliminar(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador de la mascota.");
        }
        tratamientoService.desvincularDeMascota(id);
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
        // Un id no positivo solo puede llegar por manipulación manual de la URL.
        if (duenoId <= 0) {
            throw new IllegalArgumentException("El identificador \"" + duenoId + "\" no es válido.");
        }
        // La lista vacía por sí sola no distingue entre un dueño sin mascotas
        // y un dueño inexistente; se valida primero para responder 404.
        if (!duenoRepository.existsById(duenoId)) {
            throw new RecursoNoEncontradoException(
                    "No encontramos ningún cliente registrado con el identificador \"" + duenoId + "\".");
        }
        return mascotaRepository.findByDueno_Id(duenoId);
    }

    /**
     * {@inheritDoc}
     * Búsqueda por coincidencia parcial del nombre sin distinguir
     * mayúsculas/minúsculas (findByNombreContainingIgnoreCase: la consulta
     * derivada del ejemplo del curso más las palabras clave Containing e
     * IgnoreCase que el proyecto ya usa en credenciales y en la búsqueda de
     * veterinarios). Cuando el término llega nulo o vacío se delega al
     * listado completo (un solo endpoint para listado y búsqueda).
     */
    @Override
    public List<Mascota> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarMascotas();
        }
        return mascotaRepository.findByNombreContainingIgnoreCase(nombre.trim());
    }

    /**
     * {@inheritDoc}
     * La relación con el dueño se resuelve con findByDuenoId (consulta
     * derivada simple, como en el ejemplo del curso) y sobre ese listado
     * se busca una coincidencia parcial del nombre sin distinguir
     * mayúsculas/minúsculas en memoria; no se usa una consulta compuesta.
     * Si el término está vacío cae al listado del dueño.
     */
    @Override
    public List<Mascota> buscarPorDuenoYNombre(Integer duenoId, String nombre) {
        List<Mascota> delDueno = listarMascotasPorDueno(duenoId);
        if (nombre == null || nombre.isBlank()) {
            return delDueno;
        }
        String termino = nombre.trim().toLowerCase(Locale.ROOT);
        return delDueno.stream()
                .filter(mascota -> mascota.getNombre().toLowerCase(Locale.ROOT).contains(termino))
                .toList();
    }

    /**
     * {@inheritDoc}
     * La consulta derivada findByEstado traduce el estado de negocio a
     * la columna estado de la tabla.
     */
    @Override
    public List<Mascota> listarMascotasActivas() {
        return List.copyOf(mascotaRepository.findByEstado("Activo"));
    }

    /**
     * {@inheritDoc}
     * Consulta derivada countByDuenoId: cuenta en la base sin materializar
     * las mascotas, para el conteo que muestra el listado de clientes.
     */
    @Override
    public long contarMascotasPorDueno(Integer duenoId) {
        if (duenoId == null) {
            throw new IllegalArgumentException("No se especificó el dueño del conteo.");
        }
        return mascotaRepository.countByDueno_Id(duenoId);
    }
}

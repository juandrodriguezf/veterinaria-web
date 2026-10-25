package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Veterinario;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.VeterinarioRepository;

import jakarta.transaction.Transactional;

/**
 * CAPA SERVICIO - Implementación de VeterinarioService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class VeterinarioServiceImpl implements VeterinarioService {

    /** Repositorio de veterinarios (flujo obligatorio: Service -> Repository). */
    @Autowired
    private VeterinarioRepository veterinarioRepository;

    /**
     * Servicio de tratamientos: antes de eliminar al veterinario se
     * desvinculan sus tratamientos para conservar el historial clínico.
     */
    @Autowired
    private TratamientoService tratamientoService;

    @Override
    public List<Veterinario> listarVeterinarios() {
        return List.copyOf(veterinarioRepository.findAll());
    }

    @Override
    public List<Veterinario> buscar(String termino) {
        // Sin término la barra de búsqueda se comporta como el listado
        // completo, igual que el buscarPorNombre de mascotas y dueños.
        if (termino == null || termino.isBlank()) {
            return listarVeterinarios();
        }
        String patron = termino.trim();
        return List.copyOf(veterinarioRepository
                .findByCedulaContainingIgnoreCaseOrNombreContainingIgnoreCase(patron, patron));
    }

    @Override
    public Veterinario obtenerVeterinarioPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return veterinarioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún veterinario registrado con el identificador \"" + id + "\"."));
    }

    /**
     * {@inheritDoc}
     * La validación de unicidad se hace ANTES de insertar: sin esto la base
     * rechazaría el INSERT por las restricciones UNIQUE con una excepción
     * críptica. Al editar (el veterinario conserva su id), "repetido en
     * otro id" no cuenta.
     */
    @Override
    public void guardar(Veterinario veterinario) {
        Veterinario porCorreo = veterinarioRepository.findByCorreoIgnoreCase(veterinario.getCorreo());
        if (porCorreo != null && !porCorreo.getId().equals(veterinario.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un veterinario con el correo \"" + veterinario.getCorreo() + "\".");
        }
        Veterinario porCedula = veterinarioRepository.findByCedula(veterinario.getCedula());
        if (porCedula != null && !porCedula.getId().equals(veterinario.getId())) {
            throw new IllegalArgumentException(
                    "Ya existe un veterinario con la cédula \"" + veterinario.getCedula() + "\".");
        }
        // Política de contraseñas: en edición, dejar el campo en blanco
        // significa "conservar la clave actual" (el formulario no la
        // devuelve); en creación se asigna la clave provisional del
        // equipo ("vet123"), igual que los datos de prueba.
        if (veterinario.getContrasena() == null || veterinario.getContrasena().isBlank()) {
            if (veterinario.getId() != null) {
                Veterinario actual = veterinarioRepository.findById(veterinario.getId()).orElseThrow(
                        () -> new RecursoNoEncontradoException(
                                "No encontramos ningún veterinario registrado con el identificador \""
                                        + veterinario.getId() + "\"."));
                veterinario.setContrasena(actual.getContrasena());
            } else {
                veterinario.setContrasena("vet123");
            }
        }
        if (veterinario.getEstado() == null || veterinario.getEstado().isBlank()) {
            veterinario.setEstado("Activo");
        }
        if (veterinario.getNumeroAtenciones() == null) {
            veterinario.setNumeroAtenciones(0);
        }
        veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena) {
        // Política del portal: solo un veterinario "Activo" inicia sesión.
        return veterinarioRepository.findByCorreoIgnoreCaseAndContrasenaAndEstado(
                correo, contrasena, "Activo");
    }

    /**
     * {@inheritDoc}
     * Con JpaRepository el cambio de estado es cargar, mutar y guardar.
     */
    @Override
    public void cambiarEstado(Integer id, String estado) {
        Veterinario veterinario = veterinarioRepository.findById(id).orElseThrow(
                () -> new RecursoNoEncontradoException(
                        "No encontramos ningún veterinario registrado con el identificador \"" + id + "\"."));
        veterinario.setEstado(estado);
        veterinarioRepository.save(veterinario);
    }

    /**
     * {@inheritDoc}
     * Aplica la regla de negocio del borrado lógico laboral: el estado
     * nuevo es el opuesto al actual (Activo <-> Inactivo).
     */
    @Override
    public String alternarEstado(Integer id) {
        // El id inválido o inexistente lo detecta obtenerVeterinarioPorId.
        Veterinario veterinario = obtenerVeterinarioPorId(id);
        String nuevoEstado = "Inactivo".equals(veterinario.getEstado()) ? "Activo" : "Inactivo";
        cambiarEstado(id, nuevoEstado);
        return nuevoEstado;
    }

    /**
     * {@inheritDoc}
     * Primero desvincula los tratamientos (la FK tratamiento.veterinario
     * pasa a nulo) y luego borra al veterinario: el historial clínico se
     * conserva aunque quede sin responsable.
     */
    @Override
    @Transactional // commit/rollback: la desvinculación y el borrado van juntos
    public void eliminar(Integer id) {
        // Valida el id y confirma que el veterinario exista antes de tocar
        // los tratamientos.
        obtenerVeterinarioPorId(id);
        tratamientoService.desvincularDeVeterinario(id);
        veterinarioRepository.deleteById(id);
    }
}

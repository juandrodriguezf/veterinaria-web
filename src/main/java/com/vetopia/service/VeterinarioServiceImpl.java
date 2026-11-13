package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.UserEntity;
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

    /** Validador de cédula y correo únicos entre las cuentas del sistema. */
    @Autowired
    private IdentificadorUnicoService identificadorUnicoService;

    /** Cuenta unificada de acceso (users/roles). */
    @Autowired
    private CuentaUsuarioService cuentaUsuarioService;

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
     * La cédula y el correo identifican la cuenta en el login, así que se
     * validan contra clientes, veterinarios y administradores antes de
     * guardar. El validador central ignora este mismo registro al editar y
     * reserva también los identificadores de cuentas inactivas.
     */
    @Override
    public void guardar(Veterinario veterinario) {
        veterinario.setCedula(
                veterinario.getCedula() == null ? null : veterinario.getCedula().trim());
        veterinario.setCorreo(
                veterinario.getCorreo() == null ? null : veterinario.getCorreo().trim());
        identificadorUnicoService.validarVeterinario(
                veterinario.getCedula(), veterinario.getCorreo(), veterinario.getId());
        // En edición se carga el registro actual una sola vez: de allí se
        // recuperan la clave, la cuenta y los campos que el formulario no
        // envía para no borrarlos.
        Veterinario actual = veterinario.getId() == null
                ? null
                : veterinarioRepository.findById(veterinario.getId()).orElseThrow(
                        () -> new RecursoNoEncontradoException(
                                "No encontramos ningún veterinario registrado con el identificador \""
                                        + veterinario.getId() + "\"."));

        // Contraseña: vacía en edición significa conservar la actual; en
        // creación se asigna la clave provisional del equipo ("vet123").
        String clave = veterinario.getContrasena();
        boolean claveNueva = clave != null && !clave.isBlank();
        if (!claveNueva) {
            clave = actual != null ? actual.getContrasena() : "vet123";
            veterinario.setContrasena(clave);
        }
        if (veterinario.getEstado() == null || veterinario.getEstado().isBlank()) {
            veterinario.setEstado(actual != null ? actual.getEstado() : "Activo");
        }
        if (veterinario.getNumeroAtenciones() == null) {
            veterinario.setNumeroAtenciones(actual != null ? actual.getNumeroAtenciones() : 0);
        }
        if (veterinario.getUrlFoto() == null) {
            veterinario.setUrlFoto(actual != null ? actual.getUrlFoto() : null);
        }

        // Cuenta unificada: correo como usuario, clave cifrada y rol.
        UserEntity cuenta = actual != null ? actual.getUser() : null;
        if (cuenta == null) {
            cuenta = cuentaUsuarioService.construir(
                    veterinario.getCorreo(), clave, CuentaUsuarioService.ROL_VETERINARIO);
        } else {
            cuentaUsuarioService.sincronizar(
                    cuenta, veterinario.getCorreo(), claveNueva ? clave : null,
                    CuentaUsuarioService.ROL_VETERINARIO);
        }
        cuentaUsuarioService.cambiarEstado(cuenta, "Activo".equals(veterinario.getEstado()));
        veterinario.setUser(cuenta);
        veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario obtenerPorCorreoYContrasena(String identificador, String contrasena) {
        // El formulario acepta correo o cédula: se busca primero por correo y,
        // si no hay coincidencia, por documento. Política del portal: solo un
        // veterinario "Activo" inicia sesión.
        Veterinario porCorreo = veterinarioRepository.findByCorreoIgnoreCaseAndContrasenaAndEstado(
                identificador, contrasena, "Activo");
        return porCorreo != null
                ? porCorreo
                : veterinarioRepository.findByCedulaAndContrasenaAndEstado(
                        identificador, contrasena, "Activo");
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
        // La cuenta de acceso sigue el mismo estado: un veterinario
        // inactivo no puede autenticarse.
        cuentaUsuarioService.cambiarEstado(veterinario.getUser(), "Activo".equals(estado));
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

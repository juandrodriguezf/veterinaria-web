package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.repository.DuenoRepository;

import jakarta.transaction.Transactional;

/**
 * CAPA SERVICIO - Implementación de DuenoService
 * Contiene la lógica de negocio. La anotación @Service registra la clase
 * como bean de negocio y Spring gestiona automáticamente su ciclo de vida.
 *
 * El repositorio se recibe por inyección por campo (@Autowired). Esta capa
 * nunca conoce los detalles de la persistencia, solo el contrato del
 * repositorio.
 */
@Service
public class DuenoServiceImpl implements DuenoService {

    /** Repositorio de dueños (flujo obligatorio: Service -> Repository). */
    @Autowired
    private DuenoRepository duenoRepository;

    /** Servicio de mascotas (para la eliminación en cascada). */
    @Autowired
    private MascotaService mascotaService;

    @Override
    public List<Dueno> listarDuenos() {
        return List.copyOf(duenoRepository.findAll());
    }

    @Override
    public Dueno obtenerDuenoPorId(Integer id) {
        // Entrada inválida: sin id no hay nada que buscar en el repositorio.
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        // Los id válidos son positivos: cero o negativos solo llegan por
        // manipulación manual de la URL.
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return duenoRepository.findById(id).orElse(null);
    }

    @Override
    @Transactional
    public void guardar(Dueno dueno) {
        // Validación de unicidad ANTES de insertar: sin esto la base
        // rechazaría el INSERT por las restricciones UNIQUE de cédula y
        // correo con una excepción críptica. Al validar aquí el mensaje
        // es claro y el formulario vuelve con el aviso. Al editar (el
        // dueno conserva su id), "repetido en otro id" no cuenta.
        Dueno porCorreo = duenoRepository.findByCorreoIgnoreCase(dueno.getCorreo());
        if (porCorreo != null && !porCorreo.getId().equals(dueno.getId())) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con el correo \"" + dueno.getCorreo() + "\".");
        }
        Dueno porCedula = duenoRepository.findByCedula(dueno.getCedula());
        if (porCedula != null && !porCedula.getId().equals(dueno.getId())) {
            throw new IllegalArgumentException(
                "Ya existe un cliente con la cédula \"" + dueno.getCedula() + "\".");
        }
        // Un cliente creado por el veterinario arranca Activo con una clave
        // provisional (puede cambiarla después en "editar cliente"). Cubre
        // al flujo original de registro, cuyo formulario no pide estos
        // campos, y evita la violación de columnas NOT NULL.
        if (dueno.getContrasena() == null || dueno.getContrasena().isBlank()) {
            dueno.setContrasena("vetopia123");
        }
        if (dueno.getEstado() == null || dueno.getEstado().isBlank()) {
            dueno.setEstado("Activo");
        }
        duenoRepository.save(dueno);
    }

    @Override
    public Dueno obtenerPorCorreoYContrasena(String correo, String contrasena) {
        // Misma política del portal: solo un cliente "Activo" inicia sesión.
        return duenoRepository.findByCorreoIgnoreCaseAndContrasenaAndEstado(
                correo, contrasena, "Activo");
    }

    /**
     * {@inheritDoc}
     * Con JpaRepository el cambio de estado es cargar, mutar y guardar.
     */
    @Override
    public void cambiarEstado(Integer id, String estado) {
        Dueno dueno = duenoRepository.findById(id).orElse(null);
        if (dueno != null) {
            dueno.setEstado(estado);
            duenoRepository.save(dueno);
        }
    }

    /**
     * {@inheritDoc}
     * Antes de retirar al dueño se retiran sus mascotas para no dejar
     * huérfanas (misma política del ejemplo: borrar por capas desde el
     * service, sin depender del DDL).
     */
    @Override
    @Transactional
    public void eliminar(Integer id) {
        duenoRepository.deleteById(id);
    }

    /**
     * {@inheritDoc}
     * Aplica la misma política del login: un cliente desactivado no
     * puede usar el portal, aunque conozca sus propias URLs.
     */
    @Override
    public Dueno obtenerActivo(Integer id) {
        Dueno dueno = obtenerDuenoPorId(id);
        // Un id válido que no está en la "tabla" no puede entrar al portal.
        if (dueno == null) {
            throw new IllegalStateException("El cliente indicado no existe.");
        }
        // Misma política del login: un cliente desactivado por el
        // veterinario no usa el portal, aunque conozca sus propias URLs.
        if (!"Activo".equals(dueno.getEstado())) {
            throw new IllegalStateException("El cliente se encuentra inactivo.");
        }
        return dueno;
    }

    /**
     * {@inheritDoc}
     * Aplica la regla de negocio del borrado lógico: el estado nuevo es
     * el opuesto al actual (Activo <-> Inactivo).
     */
    @Override
    public String alternarEstado(Integer id) {
        // El id inválido lo detecta obtenerDuenoPorId con su excepción;
        // si el id es válido pero el dueño no existe, se devuelve null.
        Dueno dueno = obtenerDuenoPorId(id);
        if (dueno == null) {
            return null;
        }
        String nuevoEstado = "Inactivo".equals(dueno.getEstado()) ? "Activo" : "Inactivo";
        cambiarEstado(id, nuevoEstado);
        return nuevoEstado;
    }

    /**
     * {@inheritDoc}
     * Primero retira las mascotas del dueño y luego al dueño, en el
     * mismo orden que exige la eliminación en cascada del diagrama.
     */
    @Override
    @Transactional // commit/rollback: la cascada no queda parcial
    public void eliminarEnCascada(Integer id) {
        // Sin dueño identificado la cascada no tiene punto de partida.
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del dueño.");
        }
        // Orden de la cascada: primero las mascotas (para no dejar
        // huérfanas) y al final el dueño.
        for (Mascota mascota : mascotaService.listarMascotasPorDueno(id)) {
            mascotaService.eliminar(mascota.getId());
        }
        eliminar(id);
    }
}

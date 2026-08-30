package com.vetopia.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vetopia.entities.Veterinario;
import com.vetopia.repository.VeterinarioRepository;

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

    @Override
    public List<Veterinario> listarVeterinarios() {
        return List.copyOf(veterinarioRepository.searchAll());
    }

    @Override
    public Veterinario obtenerVeterinarioPorId(Integer id) {
        if (id == null) {
            throw new IllegalArgumentException("No se especificó el identificador del veterinario.");
        }
        if (id <= 0) {
            throw new IllegalArgumentException("El identificador \"" + id + "\" no es válido.");
        }
        return veterinarioRepository.searchById(id);
    }

    @Override
    public void guardar(Veterinario veterinario) {
        veterinarioRepository.save(veterinario);
    }

    @Override
    public Veterinario obtenerPorCorreoYContrasena(String correo, String contrasena) {
        return veterinarioRepository.searchByCorreoYContrasena(correo, contrasena);
    }
}

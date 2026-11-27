package com.vetopia.security;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetopia.entities.UserEntity;
import com.vetopia.repository.UserRepository;

/**
 * PRUEBAS DE INTEGRACIÓN - Seguridad
 * Verifican el contrato real de la API con Spring Security activo: login
 * con JWT, 401 sin token, 403 por rol equivocado, propiedad de recursos y
 * que el veterinario del tratamiento salga del token y no del cuerpo.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class AutenticacionIntegrationTest {

    private static final String ADMIN = "admin.e2e@vetopia.com";
    private static final String ADMIN_CLAVE = "admin123";
    private static final String VETERINARIO = "carlos.e2e@vetopia.com";
    private static final String VETERINARIO_CLAVE = "vet123";
    private static final String CLIENTE = "ana.e2e@vetopia.com";
    private static final String CLIENTE_CLAVE = "ana123";
    private static final String CLIENTE_INACTIVO = "inactivo.e2e@vetopia.com";
    private static final String CLIENTE_INACTIVO_CLAVE = "inactivo123";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Test
    void sinTokenDevuelve401() throws Exception {
        mockMvc.perform(get("/mascota/all"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").isNotEmpty());
    }

    @Test
    void tokenAlteradoDevuelve401() throws Exception {
        String token = token(ADMIN, ADMIN_CLAVE);
        mockMvc.perform(get("/mascota/all")
                .header("Authorization", "Bearer " + token + "x"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void loginDevuelveTokenYRol() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(ADMIN, ADMIN_CLAVE)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tipo").value("Bearer"))
                .andExpect(jsonPath("$.rol").value("ADMINISTRADOR"));
    }

    @Test
    void clienteNoPuedeListarTodasLasMascotas() throws Exception {
        String token = token(CLIENTE, CLIENTE_CLAVE);
        mockMvc.perform(get("/mascota/all").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void veterinarioNoEntraAlDashboard() throws Exception {
        String token = token(VETERINARIO, VETERINARIO_CLAVE);
        mockMvc.perform(get("/administrador/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void administradorEntraAlDashboard() throws Exception {
        String token = token(ADMIN, ADMIN_CLAVE);
        mockMvc.perform(get("/administrador/dashboard")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
    }

    @Test
    void clienteSoloVeSuPropioPerfil() throws Exception {
        String token = token(CLIENTE, CLIENTE_CLAVE);

        mockMvc.perform(get("/dueno/me").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.correo").value(CLIENTE));

        mockMvc.perform(get("/dueno/activo/2").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void clienteSoloVeSusMascotas() throws Exception {
        String token = token(CLIENTE, CLIENTE_CLAVE);

        mockMvc.perform(get("/mascota/mias").header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Max"));

        mockMvc.perform(get("/mascota/mias/999").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        // El cliente no puede usar los endpoints que reciben un dueño por URL.
        mockMvc.perform(get("/mascota/byDueno/2").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/mascota/propia/1/1").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void veterinarioNoPuedeConsultarMascotaDeOtroDueno() throws Exception {
        String token = token(VETERINARIO, VETERINARIO_CLAVE);

        mockMvc.perform(get("/mascota/propia/1/2").header("Authorization", "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void clienteInactivoNoPuedeIniciarSesion() throws Exception {
        mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(CLIENTE_INACTIVO, CLIENTE_INACTIVO_CLAVE)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.mensaje").value("La cuenta se encuentra inactiva."));
    }

    @Test
    void tokenDeCuentaDesactivadaDevuelve401() throws Exception {
        String token = token(CLIENTE, CLIENTE_CLAVE);

        UserEntity cuenta = userRepository.findByUsernameIgnoreCase(CLIENTE).orElseThrow();
        boolean estadoOriginal = cuenta.isActivo();
        cuenta.setActivo(false);
        userRepository.save(cuenta);

        try {
            mockMvc.perform(get("/mascota/mias")
                    .header("Authorization", "Bearer " + token))
                    .andExpect(status().isUnauthorized());
        } finally {
            cuenta.setActivo(estadoOriginal);
            userRepository.save(cuenta);
        }
    }

    @Test
    void clienteBuscaSusMascotasPorNombre() throws Exception {
        String token = token(CLIENTE, CLIENTE_CLAVE);

        mockMvc.perform(get("/mascota/mias").param("nombre", "Max")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nombre").value("Max"));
    }

    @Test
    void veterinarioAsignaTratamientoConSuIdentidad() throws Exception {
        String token = token(VETERINARIO, VETERINARIO_CLAVE);

        String cuerpo = objectMapper.writeValueAsString(Map.of(
                "fecha", "2026-11-13",
                "mascotaId", 1,
                "drogaId", 1));

        mockMvc.perform(post("/tratamiento/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.unidadesDisponibles").value(9));

        mockMvc.perform(get("/tratamiento/mios")
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].veterinarioId").value(1));
    }

    @Test
    void administradorNoAsignaTratamientos() throws Exception {
        String token = token(ADMIN, ADMIN_CLAVE);
        String cuerpo = objectMapper.writeValueAsString(Map.of(
                "fecha", "2026-11-13",
                "mascotaId", 1,
                "drogaId", 1));

        mockMvc.perform(post("/tratamiento/add")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo))
                .andExpect(status().isForbidden());
    }

    /** Inicia sesión y devuelve el token JWT del usuario indicado. */
    private String token(String correo, String clave) throws Exception {
        MvcResult resultado = mockMvc.perform(post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(credenciales(correo, clave)))
                .andExpect(status().isOk())
                .andReturn();
        JsonNode json = objectMapper.readTree(resultado.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    /** Arma el cuerpo JSON del login. */
    private String credenciales(String correo, String clave) throws Exception {
        return objectMapper.writeValueAsString(Map.of(
                "correo", correo,
                "contrasena", clave));
    }
}

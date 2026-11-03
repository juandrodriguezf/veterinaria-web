package com.vetopia.controller.api;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.service.MascotaService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * PRUEBAS CON MOCKS - Controlador de Mascota
 * Probamos el controlador REST sin tocar la base de datos. Con MockMvc
 * simulamos las peticiones HTTP y con @MockBean simulamos el servicio
 * ubicado justamente debajo (como enseñó el ejemplo del curso).
 *
 * En cada prueba comprobamos el código de estado, el tipo de contenido y
 * los datos clave de la respuesta, de modo que el contrato con el frontend
 * quede verificado sin probar otra vez el servicio.
 */
@WebMvcTest(MascotaRestController.class)
@ActiveProfiles("test")
class MascotaRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MascotaService mascotaService;

    private Mascota mascotaActiva;
    private Mascota mascotaInactiva;

    @BeforeEach
    void prepararEntidades() {
        mascotaActiva = Mascota.builder()
                .id(1)
                .nombre("Pelusa Activa")
                .especie("Canino")
                .raza("Criollo")
                .edad(3.0)
                .sexo("Hembra")
                .pesoKg(12.5)
                .estado("Activo")
                .dueno(Dueno.builder().id(1).build())
                .build();
        mascotaInactiva = Mascota.builder()
                .id(2)
                .nombre("Pelusa Inactiva")
                .especie("Canino")
                .raza("Criollo")
                .edad(2.0)
                .sexo("Macho")
                .pesoKg(4.0)
                .estado("Inactivo")
                .dueno(Dueno.builder().id(1).build())
                .build();
    }

    @Test
    // Comprueba que GET /mascota/all devuelve 200 y JSON con el listado real
    // que simula el servicio, verificando tamanio y campos clave.
    void mascotaRestController_listarMascotas_devuelveLista() throws Exception {
        when(mascotaService.listarMascotas())
                .thenReturn(List.of(mascotaActiva, mascotaInactiva));

        ResultActions resultado = mockMvc.perform(get("/mascota/all"));

        resultado.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nombre").value("Pelusa Activa"))
                .andExpect(jsonPath("$[1].estado").value("Inactivo"));
        verify(mascotaService).listarMascotas();
    }

    @Test
    // Comprueba que GET /mascota/find/{id} devuelve 200 y JSON con la mascota
    // correcta para el id solicitado.
    void mascotaRestController_obtenerMascotaPorId_devuelveMascota() throws Exception {
        when(mascotaService.obtenerMascotaPorId(1)).thenReturn(mascotaActiva);

        ResultActions resultado = mockMvc.perform(get("/mascota/find/1"));

        resultado.andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nombre").value("Pelusa Activa"))
                .andExpect(jsonPath("$.duenoId").value(1));
        verify(mascotaService).obtenerMascotaPorId(1);
    }

    @Test
    // Comprueba que POST /mascota/add recibe JSON y delega en el servicio
    // para guardar una mascota con estado Activo.
    void mascotaRestController_agregarMascota_delegaEnServicio() throws Exception {
        Mascota nueva = Mascota.builder()
                .nombre("Nueva")
                .especie("Canino")
                .raza("Criollo")
                .edad(1.0)
                .sexo("Hembra")
                .estado("Activo")
                .dueno(Dueno.builder().id(1).build())
                .build();
        String cuerpo = objectMapper.writeValueAsString(nueva);

        ResultActions resultado = mockMvc.perform(post("/mascota/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));

        resultado.andExpect(status().isOk());
        verify(mascotaService).guardarValidada(any(Mascota.class));
    }

    @Test
    // Comprueba que PUT /mascota/update envia el cuerpo actualizado y
    // delega en el servicio para guardar los cambios.
    void mascotaRestController_actualizarMascota_delegaEnServicio() throws Exception {
        mascotaActiva.setNombre("Pelusa Renombrada");
        String cuerpo = objectMapper.writeValueAsString(mascotaActiva);

        ResultActions resultado = mockMvc.perform(put("/mascota/update")
                .contentType(MediaType.APPLICATION_JSON)
                .content(cuerpo));

        resultado.andExpect(status().isOk());
        verify(mascotaService).guardarValidada(any(Mascota.class));
    }

    @Test
    // Comprueba que DELETE /mascota/delete/{id} delega en el servicio
    // y responde 200 sin cuerpo.
    void mascotaRestController_eliminarMascota_delegaEnServicio() throws Exception {
        ResultActions resultado = mockMvc.perform(delete("/mascota/delete/2"));

        resultado.andExpect(status().isOk());
        verify(mascotaService).eliminar(2);
    }
}

package com.vetopia.service;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * PRUEBAS DE INTEGRACIÓN - Servicio de Tratamiento (sin mocks)
 * Probamos el "TratamientoServiceImpl" real sobre la base de datos H2
 * limpia del perfil "test", de modo que la capa servicio se valide con
 * repositorios y entidades reales, como pide la tarea.
 *
 * Con @Transactional cada prueba se revierte al terminar, así que ninguna
 * de ellas dependerá de otra y podremos probar el mismo escenario desde
 * cero varias veces.
 */
@SpringBootTest
@ActiveProfiles("test")
@Transactional
class TratamientoServiceIntegrationTest {

    private static final String ACTIVO = "Activo";

    @Autowired
    private TratamientoService tratamientoService;

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    @Autowired
    private VeterinarioRepository veterinarioRepository;

    @Autowired
    private DrogaRepository drogaRepository;

    @Autowired
    private TratamientoRepository tratamientoRepository;

    private Dueno dueno;
    private Mascota mascotaActiva;
    private Veterinario veterinarioActivo;
    private Droga drogaConStock;

    @BeforeEach
    void prepararDatos() {
        dueno = Dueno.builder()
                .cedula("1001")
                .nombre("Ana")
                .correo("ana@correo.com")
                .contrasena("dueño123")
                .celular("3000000000")
                .estado(ACTIVO)
                .build();
        dueno = duenoRepository.save(dueno);

        mascotaActiva = Mascota.builder()
                .nombre("Pelusa")
                .especie("Canino")
                .raza("Criollo")
                .edad(3.0)
                .sexo("Hembra")
                .pesoKg(12.5)
                .color("Café")
                .fechaIngreso(LocalDate.of(2026, 10, 1))
                .enfermedad("Revisión de rutina")
                .estado(ACTIVO)
                .dueno(dueno)
                .build();
        mascotaActiva = mascotaRepository.save(mascotaActiva);

        veterinarioActivo = Veterinario.builder()
                .cedula("2001")
                .contrasena("vet123")
                .correo("carlos@vetopia.com")
                .especialidad("Medicina general")
                .numeroAtenciones(0)
                .nombre("Carlos")
                .estado(ACTIVO)
                .urlFoto("http://placekitten.com/100/100")
                .build();
        veterinarioActivo = veterinarioRepository.save(veterinarioActivo);

        drogaConStock = Droga.builder()
                .nombre("TILOSINA")
                .precioVenta(15000.0)
                .precioCompra(8000.0)
                .unidadesDisponibles(5)
                .unidadesVendidas(0)
                .build();
        drogaConStock = drogaRepository.save(drogaConStock);
    }

    @Test
    @DisplayName("listarTratamientos_devuelveLaListaRealQueYaPersistimos")
    // Cuando ya hay un tratamiento guardado, el servicio debe listar
    // exactamente ese elemento con la misma droga.
    void listarTratamientos_devuelveLaListaRealQueYaPersistimos() {
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(mascotaActiva)
                .droga(drogaConStock)
                .veterinario(veterinarioActivo)
                .build();
        tratamientoService.guardar(tratamiento);

        var tratamientos = tratamientoService.listarTratamientos();

        assertThat(tratamientos).hasSize(1);
        assertThat(tratamientos.get(0).getDroga().getId()).isEqualTo(drogaConStock.getId());
    }

    @Test
    @DisplayName("obtenerTratamientoPorId_conIdInvalido_lanzaIllegalArgumentException")
    // Si el id es cero, el servicio rechaza la consulta porque no es
    // un identificador válido.
    void obtenerTratamientoPorId_conIdInvalido_lanzaIllegalArgumentException() {
        assertThatThrownBy(() -> tratamientoService.obtenerTratamientoPorId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no es válido");
    }

    @Test
    @DisplayName("obtenerTratamientoPorId_conIdInexistente_lanzaRecursoNoEncontrado")
    // Si el id no existe en la base de datos, el servicio debe
    // devolver una señal clara de recurso no encontrado.
    void obtenerTratamientoPorId_conIdInexistente_lanzaRecursoNoEncontrado() {
        assertThatThrownBy(() -> tratamientoService.obtenerTratamientoPorId(999))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    @DisplayName("guardar_persisteElTratamientoYSePuedeRecuperar")
    // Al guardar, el servicio debe asignar id y el tratamiento
    // debe poder recuperarse con el mismo id.
    void guardar_persisteElTratamientoYSePuedeRecuperar() {
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(mascotaActiva)
                .droga(drogaConStock)
                .veterinario(veterinarioActivo)
                .build();

        tratamientoService.guardar(tratamiento);

        assertThat(tratamiento.getId()).isNotNull();
        Tratamiento guardado = tratamientoService.obtenerTratamientoPorId(tratamiento.getId());
        assertThat(guardado).isNotNull();
        assertThat(guardado.getDroga().getId()).isEqualTo(drogaConStock.getId());
    }

    @Test
    @DisplayName("asignar_descontaStockSumaVentaYAtencionDelVeterinario")
    // Al asignar un tratamiento, el servicio debe registrar el
    // tratamiento, descontar stock y sumar atenciones del veterinario.
    void asignar_descontaStockSumaVentaYAtencionDelVeterinario() {
        Tratamiento solicitud = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(Mascota.builder().id(mascotaActiva.getId()).build())
                .droga(Droga.builder().id(drogaConStock.getId()).build())
                .veterinario(Veterinario.builder().id(veterinarioActivo.getId()).build())
                .build();

        Droga resultado = tratamientoService.asignar(solicitud);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUnidadesDisponibles()).isEqualTo(4);
        assertThat(resultado.getUnidadesVendidas()).isEqualTo(1);
        Veterinario veterinarioRecargado = veterinarioRepository.findById(veterinarioActivo.getId()).orElse(null);
        assertThat(veterinarioRecargado).isNotNull();
        assertThat(veterinarioRecargado.getNumeroAtenciones()).isEqualTo(1);
        assertThat(tratamientoService.listarTratamientos()).hasSize(1);
    }

    @Test
    @DisplayName("asignar_sinStock_lanzaIllegalStateException")
    // Cuando no queda inventario, el servicio debe evitar asignar
    // tratamiento y no tocar la base de datos.
    void asignar_sinStock_lanzaIllegalStateException() {
        drogaConStock.setUnidadesDisponibles(0);
        drogaRepository.save(drogaConStock);

        Tratamiento solicitud = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(Mascota.builder().id(mascotaActiva.getId()).build())
                .droga(Droga.builder().id(drogaConStock.getId()).build())
                .veterinario(Veterinario.builder().id(veterinarioActivo.getId()).build())
                .build();

        assertThatThrownBy(() -> tratamientoService.asignar(solicitud))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay unidades disponibles");

        // La asignación rechazada no registra el tratamiento ni descuenta
        // el stock (el servicio valida antes de tocar la base de datos).
        assertThat(tratamientoRepository.count()).isZero();
        assertThat(drogaRepository.findById(drogaConStock.getId()).orElse(null))
                .extracting(Droga::getUnidadesDisponibles)
                .isEqualTo(0);
        assertThat(veterinarioRepository.findById(veterinarioActivo.getId()).orElse(null))
                .extracting(Veterinario::getNumeroAtenciones)
                .isEqualTo(0);
    }
}

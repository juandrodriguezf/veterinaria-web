package com.vetopia.service;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
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

import org.assertj.core.api.Assertions;

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
    // Cuando ya hay un tratamiento guardado, el servicio debe listar
    // exactamente ese elemento con la misma droga.
    void tratamientoService_listarTratamientos_devuelveTratamientos() {
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(mascotaActiva)
                .droga(drogaConStock)
                .veterinario(veterinarioActivo)
                .build();
        tratamientoService.guardar(tratamiento);

        var tratamientos = tratamientoService.listarTratamientos();

        Assertions.assertThat(tratamientos).hasSize(1);
        Assertions.assertThat(tratamientos.get(0).getDroga().getId()).isEqualTo(drogaConStock.getId());
    }

    @Test
    // Si el id es cero, el servicio rechaza la consulta porque no es
    // un identificador válido.
    void tratamientoService_obtenerTratamientoPorId_idInvalidoLanzaExcepcion() {
        Assertions.assertThatThrownBy(() -> tratamientoService.obtenerTratamientoPorId(0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no es válido");
    }

    @Test
    // Si el id no existe en la base de datos, el servicio debe
    // devolver una señal clara de recurso no encontrado.
    void tratamientoService_obtenerTratamientoPorId_idInexistenteLanzaExcepcion() {
        Assertions.assertThatThrownBy(() -> tratamientoService.obtenerTratamientoPorId(999))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
    }

    @Test
    // Al guardar, el servicio debe asignar id y el tratamiento
    // debe poder recuperarse con el mismo id.
    void tratamientoService_guardar_persisteTratamiento() {
        Tratamiento tratamiento = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(mascotaActiva)
                .droga(drogaConStock)
                .veterinario(veterinarioActivo)
                .build();

        tratamientoService.guardar(tratamiento);

        Assertions.assertThat(tratamiento.getId()).isNotNull();
        Tratamiento guardado = tratamientoService.obtenerTratamientoPorId(tratamiento.getId());
        Assertions.assertThat(guardado).isNotNull();
        Assertions.assertThat(guardado.getDroga().getId()).isEqualTo(drogaConStock.getId());
    }

    @Test
    // Al asignar un tratamiento, el servicio debe registrar el
    // tratamiento, descontar stock y sumar atenciones del veterinario.
    void tratamientoService_asignar_descuentaStockYSumaAtencion() {
        Tratamiento solicitud = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(Mascota.builder().id(mascotaActiva.getId()).build())
                .droga(Droga.builder().id(drogaConStock.getId()).build())
                .veterinario(Veterinario.builder().id(veterinarioActivo.getId()).build())
                .build();

        Droga resultado = tratamientoService.asignar(solicitud);

        Assertions.assertThat(resultado).isNotNull();
        Assertions.assertThat(resultado.getUnidadesDisponibles()).isEqualTo(4);
        Assertions.assertThat(resultado.getUnidadesVendidas()).isEqualTo(1);
        Veterinario veterinarioRecargado = veterinarioRepository.findById(veterinarioActivo.getId()).orElse(null);
        Assertions.assertThat(veterinarioRecargado).isNotNull();
        Assertions.assertThat(veterinarioRecargado.getNumeroAtenciones()).isEqualTo(1);
        Assertions.assertThat(tratamientoService.listarTratamientos()).hasSize(1);
    }

    @Test
    // Cuando no queda inventario, el servicio debe evitar asignar
    // tratamiento y no tocar la base de datos.
    void tratamientoService_asignar_sinStockLanzaExcepcion() {
        drogaConStock.setUnidadesDisponibles(0);
        drogaRepository.save(drogaConStock);

        Tratamiento solicitud = Tratamiento.builder()
                .fecha(LocalDate.of(2026, 10, 1))
                .mascota(Mascota.builder().id(mascotaActiva.getId()).build())
                .droga(Droga.builder().id(drogaConStock.getId()).build())
                .veterinario(Veterinario.builder().id(veterinarioActivo.getId()).build())
                .build();

        Assertions.assertThatThrownBy(() -> tratamientoService.asignar(solicitud))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay unidades disponibles");

        // La asignación rechazada no registra el tratamiento ni descuenta
        // el stock (el servicio valida antes de tocar la base de datos).
        Assertions.assertThat(tratamientoRepository.count()).isZero();
        Assertions.assertThat(drogaRepository.findById(drogaConStock.getId()).orElse(null))
                .extracting(Droga::getUnidadesDisponibles)
                .isEqualTo(0);
        Assertions.assertThat(veterinarioRepository.findById(veterinarioActivo.getId()).orElse(null))
                .extracting(Veterinario::getNumeroAtenciones)
                .isEqualTo(0);
    }
}

package com.vetopia.service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.errors.RecursoNoEncontradoException;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * PRUEBAS CON MOCKS - Servicio de Tratamiento
 * Aquí no se usa base de datos real. Con Mockito simulamos los repositorios
 * y el servicio de drogas (dependencias de la capa servicio), de modo que
 * la prueba se concentre en la lógica de negocio en vez del acceso a datos,
 * tal como enseñó el ejemplo del curso.
 */
@ExtendWith(MockitoExtension.class)
class TratamientoServiceMockTest {

    private static final LocalDate FECHA = LocalDate.of(2026, 10, 1);

    @Mock
    private TratamientoRepository tratamientoRepository;

    @Mock
    private DrogaService drogaService;

    @Mock
    private VeterinarioRepository veterinarioRepository;

    @Mock
    private MascotaRepository mascotaRepository;

    @InjectMocks
    private TratamientoServiceImpl tratamientoService;

    private Mascota mascotaActiva;
    private Mascota mascotaInactiva;
    private Veterinario veterinarioActivo;
    private Droga drogaConStock;

    @BeforeEach
    void prepararEntidades() {
        mascotaActiva = Mascota.builder()
                .id(1)
                .nombre("Pelusa")
                .especie("Canino")
                .raza("Criollo")
                .edad(3.0)
                .sexo("Hembra")
                .pesoKg(12.5)
                .estado("Activo")
                .dueno(Dueno.builder().id(1).build())
                .build();
        mascotaInactiva = Mascota.builder()
                .id(1)
                .nombre("Pelusa Alta")
                .especie("Canino")
                .raza("Criollo")
                .edad(2.0)
                .sexo("Macho")
                .pesoKg(4.0)
                .estado("Inactivo")
                .dueno(Dueno.builder().id(1).build())
                .build();
        veterinarioActivo = Veterinario.builder()
                .id(1)
                .nombre("Carlos")
                .especialidad("Medicina general")
                .estado("Activo")
                .numeroAtenciones(0)
                .build();
        drogaConStock = Droga.builder()
                .id(1)
                .nombre("TILOSINA")
                .precioVenta(15000.0)
                .precioCompra(8000.0)
                .unidadesDisponibles(5)
                .unidadesVendidas(0)
                .build();
    }

    @Test
    @DisplayName("listarTratamientos_devuelveLosTratamientosQueRetornaElRepositorio")
    void listarTratamientos_devuelveLosTratamientosQueRetornaElRepositorio() {
        Tratamiento tratamientoMock = Tratamiento.builder()
                .id(1)
                .fecha(FECHA)
                .droga(drogaConStock)
                .build();

        when(tratamientoRepository.findAll()).thenReturn(List.of(tratamientoMock));

        var tratamientos = tratamientoService.listarTratamientos();

        assertThat(tratamientos).hasSize(1);
        assertThat(tratamientos.get(0).getId()).isEqualTo(1);
        verify(tratamientoRepository).findAll();
    }

    @Test
    @DisplayName("obtenerTratamientoPorId_deberiaRetornarElTratamientoDelRepositorioMockeado")
    void obtenerTratamientoPorId_deberiaRetornarElTratamientoDelRepositorioMockeado() {
        Tratamiento tratamientoMock = Tratamiento.builder()
                .id(1)
                .fecha(FECHA)
                .droga(drogaConStock)
                .build();

        when(tratamientoRepository.findById(1)).thenReturn(Optional.of(tratamientoMock));

        var resultado = tratamientoService.obtenerTratamientoPorId(1);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1);
        assertThat(resultado.getDroga().getId()).isEqualTo(1);
        verify(tratamientoRepository).findById(1);
    }

    @Test
    @DisplayName("obtenerTratamientoPorId_noExistente_lanzaRecursoNoEncontrado")
    void obtenerTratamientoPorId_noExistente_lanzaRecursoNoEncontrado() {
        when(tratamientoRepository.findById(999)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> tratamientoService.obtenerTratamientoPorId(999))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining("999");
        verify(tratamientoRepository).findById(999);
    }

    @Test
    @DisplayName("asignar_exitoso_retornaDrogaActualizadaYVerificaCadaInteraccion")
    void asignar_exitoso_retornaDrogaActualizadaYVerificaCadaInteraccion() {
        Tratamiento solicitud = Tratamiento.builder()
                .fecha(FECHA)
                .mascota(Mascota.builder().id(1).build())
                .droga(Droga.builder().id(1).build())
                .veterinario(Veterinario.builder().id(1).build())
                .build();

        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascotaActiva));
        when(veterinarioRepository.findById(1)).thenReturn(Optional.of(veterinarioActivo));
        when(drogaService.obtenerDrogaPorId(1)).thenReturn(drogaConStock);
        when(tratamientoRepository.save(any(Tratamiento.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));
        when(veterinarioRepository.save(any(Veterinario.class)))
                .thenAnswer(invocacion -> invocacion.getArgument(0));

        Droga resultado = tratamientoService.asignar(solicitud);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getUnidadesDisponibles()).isEqualTo(4);
        assertThat(resultado.getUnidadesVendidas()).isEqualTo(1);
        assertThat(veterinarioActivo.getNumeroAtenciones()).isEqualTo(1);
        verify(tratamientoRepository).save(any(Tratamiento.class));
        verify(veterinarioRepository).save(any(Veterinario.class));
        verify(drogaService).guardar(drogaConStock);
    }

    @Test
    @DisplayName("asignar_mascotaInactiva_lanzaIllegalStateException")
    void asignar_mascotaInactiva_lanzaIllegalStateException() {
        Tratamiento solicitud = Tratamiento.builder()
                .fecha(FECHA)
                .mascota(Mascota.builder().id(1).build())
                .droga(Droga.builder().id(1).build())
                .veterinario(Veterinario.builder().id(1).build())
                .build();

        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascotaInactiva));

        assertThatThrownBy(() -> tratamientoService.asignar(solicitud))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("inactiva");
        verify(mascotaRepository).findById(1);
    }

    @Test
    @DisplayName("asignar_sinStock_lanzaIllegalStateException")
    void asignar_sinStock_lanzaIllegalStateException() {
        drogaConStock.setUnidadesDisponibles(0);

        Tratamiento solicitud = Tratamiento.builder()
                .fecha(FECHA)
                .mascota(Mascota.builder().id(1).build())
                .droga(Droga.builder().id(1).build())
                .veterinario(Veterinario.builder().id(1).build())
                .build();

        when(mascotaRepository.findById(1)).thenReturn(Optional.of(mascotaActiva));
        when(veterinarioRepository.findById(1)).thenReturn(Optional.of(veterinarioActivo));
        when(drogaService.obtenerDrogaPorId(1)).thenReturn(drogaConStock);

        assertThatThrownBy(() -> tratamientoService.asignar(solicitud))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("No hay unidades disponibles");
        verify(mascotaRepository).findById(1);
        verify(veterinarioRepository).findById(1);
        verify(drogaService).obtenerDrogaPorId(1);
    }
}

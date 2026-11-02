package com.vetopia.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * PRUEBAS - Repositorio de Mascota
 * Probamos directamente la capa de persistencia (repositorio) sin mock:
 * un CRUD completo (alta, consulta, listado, actualización y borrado)
 * más las cinco consultas personalizadas escritas con @Query.
 *
 * Con @DataJpaTest cada prueba corre sobre una instancia de H2 limpia
 * (perfil "test"), sin los datos de producción, de modo que cada prueba
 * arranca desde cero y no se contaminan entre sí.
 */
@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("test")
class MascotaRepositoryTest {

    private static final String ACTIVO = "Activo";
    private static final String INACTIVO = "Inactivo";
    private static final String ESPECIE = "Canino";

    @Autowired
    private DuenoRepository duenoRepository;

    @Autowired
    private MascotaRepository mascotaRepository;

    private Dueno dueno;
    private Mascota mascotaActiva;
    private Mascota mascotaInactiva;

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
                .nombre("Pelusa Activa")
                .especie(ESPECIE)
                .raza("Criollo")
                .edad(3.0)
                .sexo("Hembra")
                .pesoKg(12.5)
                .color("Café")
                .fechaIngreso(LocalDate.of(2026, 10, 1))
                .enfermedad("Desayuno por revisión")
                .estado(ACTIVO)
                .dueno(dueno)
                .build();
        mascotaActiva = mascotaRepository.save(mascotaActiva);

        mascotaInactiva = Mascota.builder()
                .nombre("Pelusa Inactiva")
                .especie(ESPECIE)
                .raza("Criollo")
                .edad(2.0)
                .sexo("Macho")
                .pesoKg(4.0)
                .color("Blanco")
                .fechaIngreso(LocalDate.of(2025, 1, 1))
                .enfermedad("Alta definitiva")
                .estado(INACTIVO)
                .dueno(dueno)
                .build();
        mascotaInactiva = mascotaRepository.save(mascotaInactiva);
    }

    /* ===================== CRUD ===================== */

    @Test
    @DisplayName("guardarMascota_guardaLaMascotaYLeAsignaId")
    // Comprueba que save() inserta la mascota, genera id y devuelve la
    // entidad persistida.
    void guardarMascota_guardaLaMascotaYLeAsignaId() {
        Mascota nueva = Mascota.builder()
                .nombre("Nueva")
                .especie(ESPECIE)
                .raza("Criollo")
                .edad(1.0)
                .sexo("Hembra")
                .pesoKg(3.0)
                .estado(ACTIVO)
                .dueno(dueno)
                .build();

        Mascota guardada = mascotaRepository.save(nueva);

        assertThat(guardada).isNotNull();
        assertThat(guardada.getId()).isNotNull();
        assertThat(guardada.getNombre()).isEqualTo("Nueva");
    }

    @Test
    @DisplayName("obtenerMascotaPorId_encuentraLaMascotaGuardada")
    // Comprueba que findById() devuelve la mascota sembrada con los datos correctos
    // y estado Activo.
    void obtenerMascotaPorId_encuentraLaMascotaGuardada() {
        Mascota encontrada = mascotaRepository.findById(mascotaActiva.getId()).orElse(null);

        assertThat(encontrada).isNotNull();
        assertThat(encontrada.getId()).isEqualTo(mascotaActiva.getId());
        assertThat(encontrada.getNombre()).isEqualTo("Pelusa Activa");
        assertThat(encontrada.getEstado()).isEqualTo(ACTIVO);
    }

    @Test
    @DisplayName("listarMascotas_devuelveListaNoVaciaConLasMascotasSembradas")
    // Comprueba que findAll() devuelve las dos mascotas sembradas, con cualquier
    // orden pero datos correctos.
    void listarMascotas_devuelveListaNoVaciaConLasMascotasSembradas() {
        var mascotas = mascotaRepository.findAll();

        assertThat(mascotas).isNotNull();
        assertThat(mascotas).hasSize(2);
        assertThat(mascotas).extracting(Mascota::getNombre)
                .containsExactlyInAnyOrder("Pelusa Activa", "Pelusa Inactiva");
    }

    @Test
    @DisplayName("actualizarMascota_cambiaLosCamposAlGuardar")
    // Comprueba que save() con entidad existente aplica los cambios y los
    // persiste para futuras lecturas.
    void actualizarMascota_cambiaLosCamposAlGuardar() {
        mascotaActiva.setNombre("Pelusa Renombrada");
        mascotaActiva.setRaza("Labrador");
        Mascota actualizada = mascotaRepository.save(mascotaActiva);

        Mascota recargada = mascotaRepository.findById(mascotaActiva.getId()).orElse(null);

        assertThat(actualizada).isNotNull();
        assertThat(recargada).isNotNull();
        assertThat(recargada.getNombre()).isEqualTo("Pelusa Renombrada");
        assertThat(recargada.getRaza()).isEqualTo("Labrador");
    }

    @Test
    @DisplayName("eliminarMascota_borraLaMascotaYNoSeVuelveAEncontrar")
    // Comprueba que deleteById() elimina la mascota y ya no aparece en el listado.
    void eliminarMascota_borraLaMascotaYNoSeVuelveAEncontrar() {
        mascotaRepository.deleteById(mascotaInactiva.getId());

        assertThat(mascotaRepository.findById(mascotaInactiva.getId())).isEmpty();
        assertThat(mascotaRepository.findAll()).hasSize(1);
    }

    /* ===================== Consultas personalizadas ===================== */

    @Test
    @DisplayName("buscarPorDuenoYEstado_filtraPorEstadoDelDueno")
    // Comprueba la consulta por dueño y estado, filtrando solo mascotas
    // del dueño dado y estado Activo.
    void buscarPorDuenoYEstado_filtraPorEstadoDelDueno() {
        var activas = mascotaRepository.buscarPorDuenoYEstado(dueno.getId(), ACTIVO);

        assertThat(activas).hasSize(1);
        assertThat(activas.get(0).getEstado()).isEqualTo(ACTIVO);
        assertThat(activas.get(0).getDueno().getId()).isEqualTo(dueno.getId());
    }

    @Test
    @DisplayName("buscarPorNombreYEstado_filtraPorNombreParcialYEstadoIgnorandoMayusculas")
    // Comprueba la búsqueda por nombre parcial sin distinguir mayúsculas
    // y filtrando por estado.
    void buscarPorNombreYEstado_filtraPorNombreParcialYEstadoIgnorandoMayusculas() {
        var activas = mascotaRepository.buscarPorNombreYEstado("pelusa", ACTIVO);

        assertThat(activas).hasSize(1);
        assertThat(activas.get(0).getNombre()).isEqualTo("Pelusa Activa");
        assertThat(activas.get(0).getEstado()).isEqualTo(ACTIVO);
    }

    @Test
    @DisplayName("buscarConPesoMayorOIgual_filtraPorPesoMinimo")
    // Comprueba la consulta por peso mínimo inclusive.
    void buscarConPesoMayorOIgual_filtraPorPesoMinimo() {
        var pesadas = mascotaRepository.buscarConPesoMayorOIgual(10.0);

        assertThat(pesadas).hasSize(1);
        assertThat(pesadas.get(0).getPesoKg()).isEqualTo(12.5);
    }

    @Test
    @DisplayName("buscarIngresadasDesde_filtraPorFechaDeIngreso")
    // Comprueba la consulta por mascotas ingresadas desde una fecha dada.
    void buscarIngresadasDesde_filtraPorFechaDeIngreso() {
        var recientes = mascotaRepository.buscarIngresadasDesde(LocalDate.of(2026, 1, 1));

        assertThat(recientes).hasSize(1);
        assertThat(recientes.get(0).getFechaIngreso()).isEqualTo(LocalDate.of(2026, 10, 1));
    }

    @Test
    @DisplayName("contarPorEstadoYEspecie_cuentaSoloLaEspecieIndicada")
    // Comprueba el conteo de mascotas por estado y especie.
    void contarPorEstadoYEspecie_cuentaSoloLaEspecieIndicada() {
        long activasCaninas = mascotaRepository.contarPorEstadoYEspecie(ACTIVO, ESPECIE);

        assertThat(activasCaninas).isEqualTo(1L);
    }
}

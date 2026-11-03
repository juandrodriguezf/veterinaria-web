package com.vetopia.repository;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;

import org.assertj.core.api.Assertions;

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

    /*CRUD */

    @Test
    // Comprueba que save() inserta la mascota, genera id y devuelve la
    // entidad persistida.
    void mascotaRepository_guardarMascota_asignaId() {
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

        Assertions.assertThat(guardada).isNotNull();
        Assertions.assertThat(guardada.getId()).isNotNull();
        Assertions.assertThat(guardada.getNombre()).isEqualTo("Nueva");
    }

    @Test
    // Comprueba que findById() devuelve la mascota sembrada con los datos correctos
    // y estado Activo.
    void mascotaRepository_obtenerMascotaPorId_encuentraMascota() {
        Mascota encontrada = mascotaRepository.findById(mascotaActiva.getId()).orElse(null);

        Assertions.assertThat(encontrada).isNotNull();
        Assertions.assertThat(encontrada.getId()).isEqualTo(mascotaActiva.getId());
        Assertions.assertThat(encontrada.getNombre()).isEqualTo("Pelusa Activa");
        Assertions.assertThat(encontrada.getEstado()).isEqualTo(ACTIVO);
    }

    @Test
    // Comprueba que findAll() devuelve las dos mascotas sembradas, con cualquier
    // orden pero datos correctos.
    void mascotaRepository_listarMascotas_devuelveLista() {
        var mascotas = mascotaRepository.findAll();

        Assertions.assertThat(mascotas).isNotNull();
        Assertions.assertThat(mascotas).hasSize(2);
        Assertions.assertThat(mascotas).extracting(Mascota::getNombre)
                .containsExactlyInAnyOrder("Pelusa Activa", "Pelusa Inactiva");
    }

    @Test
    // Comprueba que save() con entidad existente aplica los cambios y los
    // persiste para futuras lecturas.
    void mascotaRepository_actualizarMascota_cambiaCampos() {
        mascotaActiva.setNombre("Pelusa Renombrada");
        mascotaActiva.setRaza("Labrador");
        Mascota actualizada = mascotaRepository.save(mascotaActiva);

        Mascota recargada = mascotaRepository.findById(mascotaActiva.getId()).orElse(null);

        Assertions.assertThat(actualizada).isNotNull();
        Assertions.assertThat(recargada).isNotNull();
        Assertions.assertThat(recargada.getNombre()).isEqualTo("Pelusa Renombrada");
        Assertions.assertThat(recargada.getRaza()).isEqualTo("Labrador");
    }

    @Test
    // Comprueba que deleteById() elimina la mascota y ya no aparece en el listado.
    void mascotaRepository_eliminarMascota_eliminaMascota() {
        mascotaRepository.deleteById(mascotaInactiva.getId());

        Assertions.assertThat(mascotaRepository.findById(mascotaInactiva.getId())).isEmpty();
        Assertions.assertThat(mascotaRepository.findAll()).hasSize(1);
    }

    /* Consultas personalizadas  */

    @Test
    // Comprueba la consulta por dueño y estado, filtrando solo mascotas
    // del dueño dado y estado Activo.
    void mascotaRepository_buscarPorDuenoYEstado_filtraMascotas() {
        var activas = mascotaRepository.buscarPorDuenoYEstado(dueno.getId(), ACTIVO);

        Assertions.assertThat(activas).hasSize(1);
        Assertions.assertThat(activas.get(0).getEstado()).isEqualTo(ACTIVO);
        Assertions.assertThat(activas.get(0).getDueno().getId()).isEqualTo(dueno.getId());
    }

    @Test
    // Comprueba la búsqueda por nombre parcial sin distinguir mayúsculas
    // y filtrando por estado.
    void mascotaRepository_buscarPorNombreYEstado_filtraMascotas() {
        var activas = mascotaRepository.buscarPorNombreYEstado("pelusa", ACTIVO);

        Assertions.assertThat(activas).hasSize(1);
        Assertions.assertThat(activas.get(0).getNombre()).isEqualTo("Pelusa Activa");
        Assertions.assertThat(activas.get(0).getEstado()).isEqualTo(ACTIVO);
    }

    @Test
    // Comprueba la consulta por peso mínimo inclusive.
    void mascotaRepository_buscarConPesoMayorOIgual_filtraMascotas() {
        var pesadas = mascotaRepository.buscarConPesoMayorOIgual(10.0);

        Assertions.assertThat(pesadas).hasSize(1);
        Assertions.assertThat(pesadas.get(0).getPesoKg()).isEqualTo(12.5);
    }

    @Test
    // Comprueba la consulta por mascotas ingresadas desde una fecha dada.
    void mascotaRepository_buscarIngresadasDesde_filtraMascotas() {
        var recientes = mascotaRepository.buscarIngresadasDesde(LocalDate.of(2026, 1, 1));

        Assertions.assertThat(recientes).hasSize(1);
        Assertions.assertThat(recientes.get(0).getFechaIngreso()).isEqualTo(LocalDate.of(2026, 10, 1));
    }

    @Test
    // Comprueba el conteo de mascotas por estado y especie.
    void mascotaRepository_contarPorEstadoYEspecie_cuentaMascotas() {
        long activasCaninas = mascotaRepository.contarPorEstadoYEspecie(ACTIVO, ESPECIE);

        Assertions.assertThat(activasCaninas).isEqualTo(1L);
    }
}

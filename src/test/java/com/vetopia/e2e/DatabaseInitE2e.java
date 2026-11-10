package com.vetopia.e2e;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

/**
 * CARGADOR DE DATOS - perfil de pruebas
 * Copia del DataLoader de la aplicación, activada solo con el perfil "test"
 * (igual que DatabaseInit en el ejemplo del curso). Siembra un escenario
 * mínimo y determinista -administrador, veterinario, dueño, mascota y
 * droga- para que las pruebas E2E naveguen sin sembrar datos desde el
 * código de prueba.
 *
 * La droga lleva el sufijo "E2E" porque TratamientoServiceIntegrationTest
 * crea otra droga llamada TILOSINA y el nombre es único en la entidad.
 */
@Component
@Order(0)
@Profile("test")
public class DatabaseInitE2e implements CommandLineRunner {

    private static final String VET_EMAIL = "carlos.e2e@vetopia.com";
    private static final String VET_PASSWORD = "vet123";
    private static final String VET_CEDULA = "1002003001";
    private static final String VET_NOMBRE = "Carlos E2E";

    private static final String ADMIN_EMAIL = "admin.e2e@vetopia.com";
    private static final String ADMIN_PASSWORD = "admin123";
    private static final String ADMIN_CEDULA = "1000000001";
    private static final String ADMIN_NOMBRE = "Admin E2E";

    private static final String OWNER_CEDULA = "1000000000";
    private static final String OWNER_NOMBRE = "Ana E2E";
    private static final String OWNER_EMAIL = "ana.e2e@vetopia.com";
    private static final String OWNER_PASSWORD = "ana123";

    private static final String PET_NOMBRE = "Max";
    private static final String PET_ESPECIE = "Perro";
    private static final String PET_RAZA = "Doberman";
    private static final double PET_EDAD = 5.0;
    private static final String PET_SEXO = "Macho";
    private static final double PET_PESO = 32.0;
    private static final String PET_COLOR = "Negro";

    private static final String DRUG_NOMBRE = "TILOSINA E2E";
    private static final double DRUG_PRECIO_COMPRA = 12000.0;
    private static final double DRUG_PRECIO_VENTA = 20000.0;
    private static final int DRUG_STOCK = 10;

    private final VeterinarioRepository veterinarioRepository;

    private final AdministradorRepository administradorRepository;

    private final DuenoRepository duenoRepository;

    private final MascotaRepository mascotaRepository;

    private final DrogaRepository drogaRepository;

    private final TratamientoRepository tratamientoRepository;

    public DatabaseInitE2e(VeterinarioRepository veterinarioRepository,
            AdministradorRepository administradorRepository,
            DuenoRepository duenoRepository,
            MascotaRepository mascotaRepository,
            DrogaRepository drogaRepository,
            TratamientoRepository tratamientoRepository) {
        this.veterinarioRepository = veterinarioRepository;
        this.administradorRepository = administradorRepository;
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
        this.drogaRepository = drogaRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    @Override
    public void run(String... args) {
        tratamientoRepository.deleteAll();
        mascotaRepository.deleteAll();
        duenoRepository.deleteAll();
        drogaRepository.deleteAll();
        veterinarioRepository.deleteAll();
        administradorRepository.deleteAll();

        administradorRepository.save(Administrador.builder()
                .cedula(ADMIN_CEDULA)
                .correo(ADMIN_EMAIL)
                .contrasena(ADMIN_PASSWORD)
                .nombre(ADMIN_NOMBRE)
                .build());

        veterinarioRepository.save(Veterinario.builder()
                .cedula(VET_CEDULA)
                .contrasena(VET_PASSWORD)
                .correo(VET_EMAIL)
                .especialidad("Medicina General")
                .numeroAtenciones(0)
                .nombre(VET_NOMBRE)
                .estado("Activo")
                .build());

        Dueno dueno = duenoRepository.save(Dueno.builder()
                .cedula(OWNER_CEDULA)
                .nombre(OWNER_NOMBRE)
                .correo(OWNER_EMAIL)
                .contrasena(OWNER_PASSWORD)
                .celular("3000000000")
                .estado("Activo")
                .build());

        mascotaRepository.save(Mascota.builder()
                .nombre(PET_NOMBRE)
                .especie(PET_ESPECIE)
                .raza(PET_RAZA)
                .edad(PET_EDAD)
                .sexo(PET_SEXO)
                .pesoKg(PET_PESO)
                .color(PET_COLOR)
                .fechaIngreso(LocalDate.now().minusDays(10))
                .enfermedad("Control de rutina")
                .dueno(dueno)
                .estado("Activo")
                .build());

        drogaRepository.save(Droga.builder()
                .nombre(DRUG_NOMBRE)
                .precioCompra(DRUG_PRECIO_COMPRA)
                .precioVenta(DRUG_PRECIO_VENTA)
                .unidadesDisponibles(DRUG_STOCK)
                .unidadesVendidas(0)
                .build());
    }
}

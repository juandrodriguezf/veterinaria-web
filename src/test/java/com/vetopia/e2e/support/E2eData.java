package com.vetopia.e2e.support;

import java.time.LocalDate;

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

public final class E2eData {

    public static final String VET_EMAIL = "carlos.e2e@vetopia.com";
    public static final String VET_PASSWORD = "vet123";
    public static final String VET_CEDULA = "1002003001";
    public static final String VET_NOMBRE = "Carlos E2E";

    public static final String ADMIN_EMAIL = "admin.e2e@vetopia.com";
    public static final String ADMIN_PASSWORD = "admin123";
    public static final String ADMIN_CEDULA = "1000000001";
    public static final String ADMIN_NOMBRE = "Admin E2E";

    public static final String OWNER_CEDULA = "1000000000";
    public static final String OWNER_NOMBRE = "Ana E2E";
    public static final String OWNER_EMAIL = "ana.e2e@vetopia.com";
    public static final String OWNER_PASSWORD = "ana123";

    public static final String PET_NOMBRE = "Max";
    public static final String PET_ESPECIE = "Perro";
    public static final String PET_RAZA = "Doberman";
    public static final double PET_EDAD = 5.0;
    public static final String PET_SEXO = "Macho";
    public static final double PET_PESO = 32.0;
    public static final String PET_COLOR = "Negro";

    public static final String DRUG_NOMBRE = "TILOSINA";
    public static final double DRUG_PRECIO_COMPRA = 12000.0;
    public static final double DRUG_PRECIO_VENTA = 20000.0;
    public static final int DRUG_STOCK = 10;

    public static final String NEW_CLIENT_CEDULA = "1099887766";
    public static final String NEW_CLIENT_NOMBRE = "Cliente E2E";
    public static final String NEW_CLIENT_EMAIL = "cliente.e2e@vetopia.com";
    public static final String NEW_CLIENT_PASSWORD = "cliente123";
    public static final String NEW_CLIENT_CELULAR = "3001239876";

    public static final String NEW_PET_NOMBRE = "Mascota E2E";
    public static final String NEW_PET_ESPECIE = "Perro";
    public static final String NEW_PET_RAZA = "Criollo";
    public static final String NEW_PET_SEXO = "Macho";
    public static final double NEW_PET_EDAD = 2.0;
    public static final double NEW_PET_PESO = 10.0;
    public static final String NEW_PET_COLOR = "Cafe";

    private E2eData() {
    }

    public static void sembrar(VeterinarioRepository veterinarios,
            AdministradorRepository administradores,
            DuenoRepository duenos,
            MascotaRepository mascotas,
            DrogaRepository drogas,
            TratamientoRepository tratamientos) {
        tratamientos.deleteAll();
        mascotas.deleteAll();
        duenos.deleteAll();
        drogas.deleteAll();
        veterinarios.deleteAll();
        administradores.deleteAll();

        administradores.save(Administrador.builder()
                .cedula(ADMIN_CEDULA)
                .correo(ADMIN_EMAIL)
                .contrasena(ADMIN_PASSWORD)
                .nombre(ADMIN_NOMBRE)
                .build());

        veterinarios.save(Veterinario.builder()
                .cedula(VET_CEDULA)
                .contrasena(VET_PASSWORD)
                .correo(VET_EMAIL)
                .especialidad("Medicina General")
                .numeroAtenciones(0)
                .nombre(VET_NOMBRE)
                .estado("Activo")
                .build());

        Dueno dueno = duenos.save(Dueno.builder()
                .cedula(OWNER_CEDULA)
                .nombre(OWNER_NOMBRE)
                .correo(OWNER_EMAIL)
                .contrasena(OWNER_PASSWORD)
                .celular("3000000000")
                .estado("Activo")
                .build());

        mascotas.save(Mascota.builder()
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

        drogas.save(Droga.builder()
                .nombre(DRUG_NOMBRE)
                .precioCompra(DRUG_PRECIO_COMPRA)
                .precioVenta(DRUG_PRECIO_VENTA)
                .unidadesDisponibles(DRUG_STOCK)
                .unidadesVendidas(0)
                .build());
    }
}

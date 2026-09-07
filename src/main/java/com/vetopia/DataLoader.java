package com.vetopia;

import java.time.LocalDate;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import com.vetopia.entities.Administrador;
import com.vetopia.entities.Droga;
import com.vetopia.entities.Dueno;
import com.vetopia.entities.Mascota;
import com.vetopia.entities.Tratamiento;
import com.vetopia.entities.Veterinario;
import com.vetopia.repository.AdministradorRepository;
import com.vetopia.repository.DrogaRepository;
import com.vetopia.repository.DuenoRepository;
import com.vetopia.repository.MascotaRepository;
import com.vetopia.repository.TratamientoRepository;
import com.vetopia.repository.VeterinarioRepository;

/**
 * CARGADOR DE DATOS - semillas de Vetopia
 * Siembra la base de datos al arrancar la aplicación (patrón
 * CommandLineRunner del ejemplo del curso). Solo semilla cuando la tabla
 * de dueños está vacía, de modo que los datos capturados por el usuario
 * sobrevivan a los reinicios hasta que se decida recrear la base.
 *
 * Orden de siembra (respetando las FK): dueños -> mascotas -> drogas ->
 * veterinarios -> tratamientos.
 */
@Component
public class DataLoader implements CommandLineRunner {

    private final DuenoRepository duenoRepository;
    private final MascotaRepository mascotaRepository;
    private final DrogaRepository drogaRepository;
    private final VeterinarioRepository veterinarioRepository;
    private final AdministradorRepository administradorRepository;
    private final TratamientoRepository tratamientoRepository;

    public DataLoader(DuenoRepository duenoRepository, MascotaRepository mascotaRepository,
                      DrogaRepository drogaRepository, VeterinarioRepository veterinarioRepository,
                      AdministradorRepository administradorRepository, TratamientoRepository tratamientoRepository) {
        this.duenoRepository = duenoRepository;
        this.mascotaRepository = mascotaRepository;
        this.drogaRepository = drogaRepository;
        this.veterinarioRepository = veterinarioRepository;
        this.administradorRepository = administradorRepository;
        this.tratamientoRepository = tratamientoRepository;
    }

    @Override
    public void run(String... args) {
        // Ya hay datos: la base no vuelve a semillarse (con el perfil h2
        // las tablas se recranean en cada arranque, así que siempre entra).
        if (duenoRepository.count() > 0) {
            return;
        }

        // ---- Dueños (clientes del portal; los id los asigna la BD) ----
        Dueno ana = Dueno.builder().cedula("1020304050").nombre("Ana Rodríguez")
                .correo("ana@correo.com").contrasena("dueño123").celular("3001234567").estado("Activo").build();
        Dueno pedro = Dueno.builder().cedula("1020304051").nombre("Pedro Gómez")
                .correo("pedro@correo.com").contrasena("dueño123").celular("3007654321").estado("Activo").build();
        Dueno maria = Dueno.builder().cedula("1020304052").nombre("María López")
                .correo("maria@correo.com").contrasena("dueño123").celular("3010001111").estado("Inactivo").build();
        duenoRepository.save(ana);
        duenoRepository.save(pedro);
        duenoRepository.save(maria);

        // ---- Mascotas (cada una conectada a su dueño real) ----
        mascotaRepository.save(Mascota.builder()
                .nombre("Max").especie("Perro").raza("Doberman")
                .edad(5.0).sexo("Macho")
                .imagen("https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw")
                .pesoKg(32.0).color("Negro").fechaIngreso(LocalDate.parse("2026-01-12"))
                .enfermedad("Control de rutina y vacunación")
                .dueno(ana).estado("Activo")
                .build());
        mascotaRepository.save(Mascota.builder()
                .nombre("Luna").especie("Gato").raza("Siamés")
                .edad(3.0).sexo("Hembra")
                .imagen("https://imgs.search.brave.com/lyQdUI6O4tK7o734zlUOxuFLdw-1kUFmZssTDwlkam0/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9tZWRp/YS5pc3RvY2twaG90/by5jb20vaWQvMTU0/MjEyMDY2L2VzL2Zv/dG8vZ2F0by1zaWFt/JUMzJUE5cy5qcGc_/cz02MTJ4NjEyJnc9/MCZrPTIwJmM9ZV9V/WDktSU5fZ3VPQWhw/bUxVYk40VGktWVBj/LU92c3hWQXJUOWpn/MWxVWT0")
                .pesoKg(4.2).color("Blanco y café").fechaIngreso(LocalDate.parse("2026-02-03"))
                .enfermedad("Infección respiratoria leve")
                .dueno(pedro).estado("Activo")
                .build());
        mascotaRepository.save(Mascota.builder()
                .nombre("Rocky").especie("Perro").raza("Bulldog Francés")
                .edad(0.6).sexo("Macho")
                .imagen("https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn")
                .pesoKg(7.5).color("Gris atigrado").fechaIngreso(LocalDate.parse("2025-11-20"))
                .enfermedad("Desparasitación")
                .dueno(maria).estado("Activo")
                .build());

        // ---- Drogas del inventario ----
        drogaRepository.save(Droga.builder().nombre("Amoxicilina")
                .precioCompra(8000.0).precioVenta(15000.0).unidadesDisponibles(100).unidadesVendidas(0).build());
        drogaRepository.save(Droga.builder().nombre("Ivermectina")
                .precioCompra(12000.0).precioVenta(22000.0).unidadesDisponibles(50).unidadesVendidas(0).build());
        drogaRepository.save(Droga.builder().nombre("Metronidazol")
                .precioCompra(6000.0).precioVenta(11000.0).unidadesDisponibles(75).unidadesVendidas(0).build());

        // ---- Veterinarios (administrador queda null por ahora) ----
        veterinarioRepository.save(Veterinario.builder().cedula("1002003001").contrasena("vet123")
                .correo("carlos.gutierrez@vetopia.com").especialidad("Medicina General")
                .numeroAtenciones(0).nombre("Carlos Gutiérrez").estado("Activo")
                .urlFoto("https://example.com/foto-carlos.jpg").build());
        veterinarioRepository.save(Veterinario.builder().cedula("1002003002").contrasena("vet123")
                .correo("laura.mendez@vetopia.com").especialidad("Cirugía")
                .numeroAtenciones(0).nombre("Laura Méndez").estado("Activo")
                .build());
        veterinarioRepository.save(Veterinario.builder().cedula("1002003003").contrasena("vet123")
                .correo("jorge.santana@vetopia.com").especialidad("Dermatología")
                .numeroAtenciones(0).nombre("Jorge Santana").estado("Inactivo")
                .build());

        // ---- Tratamientos (tabla intermedia con entidades reales) ----
        Mascota luna = mascotaRepository.findById(2).orElseThrow();
        Mascota max = mascotaRepository.findById(1).orElseThrow();
        Mascota rocky = mascotaRepository.findById(3).orElseThrow();
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-10"))
                .mascota(luna)
                .droga(drogaRepository.findById(1).orElseThrow())
                .veterinario(veterinarioRepository.findById(1).orElseThrow())
                .build());
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-15"))
                .mascota(max)
                .droga(drogaRepository.findById(2).orElseThrow())
                .veterinario(veterinarioRepository.findById(2).orElseThrow())
                .build());
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-20"))
                .mascota(rocky)
                .droga(drogaRepository.findById(3).orElseThrow())
                .veterinario(veterinarioRepository.findById(3).orElseThrow())
                .build());

        // Administradores al final (no dependen de nadie).
        administradorRepository.save(Administrador.builder().cedula("1000000001")
                .correo("admin@vetopia.com").contrasena("admin123").nombre("Dirección Vetopia").build());
        administradorRepository.save(Administrador.builder().cedula("1000000002")
                .correo("gerencia@vetopia.com").contrasena("gerencia123").nombre("Gerencia General").build());
        administradorRepository.save(Administrador.builder().cedula("1000000003")
                .correo("finanzas@vetopia.com").contrasena("finanzas123").nombre("Finanzas").build());
    }
}

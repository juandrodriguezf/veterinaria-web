package com.vetopia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

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
 * veterinarios -> tratamientos -> Administrador.
 *
 * A las 3 familias semilla originales (Ana/Pedro/María, con las
 * credenciales de prueba de los portales) se suman 50 clientes y 100
 * mascotas generadas con Random(41): la semilla del Random es fija, de
 * modo que cada arranque produce exactamente los mismos datos y las
 * capturas del proyecto son reproducibles (misma técnica del ejemplo
 * del curso).
 */
@Component
public class DataLoader implements CommandLineRunner {

    /** Semilla fija del generador . */
    private static final Random AZAR = new java.util.Random(41);

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
        List<Dueno> duenos = new ArrayList<>(List.of(ana, pedro, maria));
        duenos.addAll(generarClientes());
        duenoRepository.saveAll(duenos);

        // ---- Mascotas (cada una conectada a su dueño real) ----
        mascotaRepository.save(Mascota.builder()
                .nombre("Max").especie("Perro").raza("Doberman")
                .edad(5.0).sexo("Macho")
                .imagen(imagenPerro(0))
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
                .imagen(imagenPerro(1))
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
        sembrarVeterinarios();
        Veterinario carlos = veterinarioRepository.findById(1).orElseThrow();
        Veterinario laura = veterinarioRepository.findById(2).orElseThrow();
        Veterinario jorge = veterinarioRepository.findById(3).orElseThrow();

        // ---- Mascotas generadas a escala (100 cániles) ----
        List<Mascota> mascotasGeneradas = generarMascotas(duenos);
        mascotaRepository.saveAll(mascotasGeneradas);

        // ---- Tratamientos (tabla intermedia con entidades reales) ----
        Mascota luna = mascotaRepository.findById(2).orElseThrow();
        Mascota max = mascotaRepository.findById(1).orElseThrow();
        Mascota rocky = mascotaRepository.findById(3).orElseThrow();
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-10"))
                .mascota(luna)
                .droga(drogaRepository.findById(1).orElseThrow())
                .veterinario(carlos)
                .build());
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-15"))
                .mascota(max)
                .droga(drogaRepository.findById(2).orElseThrow())
                .veterinario(laura)
                .build());
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.parse("2026-08-20"))
                .mascota(rocky)
                .droga(drogaRepository.findById(3).orElseThrow())
                .veterinario(jorge)
                .build());

        // Administradores al final (no dependen de nadie).
        administradorRepository.save(Administrador.builder().cedula("1000000001")
                .correo("admin@vetopia.com").contrasena("admin123").nombre("Dirección Vetopia").build());
        administradorRepository.save(Administrador.builder().cedula("1000000002")
                .correo("gerencia@vetopia.com").contrasena("gerencia123").nombre("Gerencia General").build());
        administradorRepository.save(Administrador.builder().cedula("1000000003")
                .correo("finanzas@vetopia.com").contrasena("finanzas123").nombre("Finanzas").build());
    }

    /**
     * Siembra los tres veterinarios del equipo.
     */
    private void sembrarVeterinarios() {
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
    }

    /**
     * Genera 50 clientes adicionales (nombre de pila aleatorio + apellido
     * consecutivo) con datos y credenciales coherentes: correo único por
     * construcción (los índices garantizan unicidad), clave provisional
     * "dueño123" y cadencia de 1 inactivo cada 10 para probar el filtro
     * del login en escala.
     */
    private List<Dueno> generarClientes() {
        String[] nombres = { "Sofía", "Mateo", "Valentina", "Sebastián", "Camila", "Alejandro", "Isabella",
                "Diego", "Paula", "Andrés", "Juliana", "Daniel", "Daniela", "Felipe", "Carolina", "Javier",
                "Lucía", "Santiago", "Gabriela", "Tomás", "Natalia", "Emiliano", "Sara", "Nicolás", "Renata",
                "Samuel", "Antonella", "Ricardo", "Mariana", "Juan" };
        String[] apellidos = { "Sánchez", "Ramírez", "Torres", "Fernández", "Silva", "Acosta", "Vargas", "Rojas",
                "Castro", "Ortiz", "Pérez", "Suárez" };
        List<Dueno> creados = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            // El índice garantiza unicidad: cédula consecutiva y correo con
            // sufijo único (restricción UNIQUE de ambas columnas).
            Dueno dueno = Dueno.builder()
                    .cedula(String.format("102030%05d", 4105 + i))
                    .nombre(nombres[i % nombres.length] + " " + apellidos[i % apellidos.length])
                    .correo("cliente" + (i + 1) + "@correo.com")
                    .contrasena("dueño123")
                    .celular("300" + String.format("%07d", 1000000 + i * 137))
                    .estado(i % 10 == 9 ? "Inactivo" : "Activo")
                    .build();
            creados.add(dueno);
        }
        return creados;
    }

    /**
     * Genera 100 mascotas caninas (requisito del proyecto: al menos 100
     * perros) distribuidas entre los dueños sembrados, con atributos e
     * historial clínico coherentes. Una de cada nueve nace Inactiva para
     * probar el borrado lógico en escala.
     */
    private List<Mascota> generarMascotas(List<Dueno> duenos) {
        String[] nombresPerro = { "Canela", "Toby", "Bruno", "Milú", "Copito", "Balto", "Dante", "León", "Kiara",
                "Sasha", "Firulais", "Maya", "Zeus", "Nube", "Coco", "Laika", "Simba", "Trivia", "Frida", "Pongo",
                "Otoño", "Chispa", "Nala", "Tigre", "Darwin", "Perla", "Gaspar", "Bola", "Kenia", "Ronda", "Alfa",
                "Cholo", "Novia", "Canyón", "Dumbo", "Estrella", "Fausto", "Gaviota", "Humo", "Iris", "Juguete",
                "Karaoke", "Lomito", "Melón", "Nieve", "Orion", "Piolín", "Quijote", "Rambo", "Sucre", "Tabaco",
                "Uva", "Vinotinto", "Waffle", "Xilófono", "Yema", "Zapata", "Aqueo", "Bulbo", "Ceniza", "Dálmata",
                "Ecléctico", "Fantasma", "Galleta", "Hueso", "Inka", "Jade", "Kaos", "Lima", "Maní", "Nogal",
                "Ombú", "Pollito", "Quimera", "Rabanito", "Sésamo", "Tierra", "Ultimátum", "Verbena", "Wisky",
                "Ximena", "Yogur", "Zurdo", "Abrax", "Bucefalo", "Cerro", "Duna", "Estrellita", "Fusil", "Garbanzo",
                "Homero", "Imparo", "Juroo", "Kika", "Lúa", "Melaza", "Nefer", "Oblea", "Pomponio", "Queso", "Rolo" };
        String[] razas = { "Labrador", "Golden Retriever", "Beagle", "Corgi", "Husky", "Pastor Alemán", "Mestizo",
                "Chihuahua", "Poodle", "Shar Pei" };
        String[] colores = { "Marrón", "Negro", "Blanco", "Café con blanco", "Canela", "Gris", "Dorado", "Tricolor" };
        String[] enfermedades = { "Control de rutina y vacunación", "Desparasitación", "Dermatitis alérgica leve",
                "Gastritis aguda", "Chequeo post-operatorio", "Anemia por malnutrición",
                "Rehabilitación tras artroscopia", "Seguimiento por infección urinaria" };
        List<Mascota> creadas = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            // Reparto por rueda: la mascota i va al dueño (i % duenos.size()),
            // de modo que ninguna familia se queda sin mascota.
            Dueno duenoAsignado = duenos.get(i % duenos.size());
            creadas.add(Mascota.builder()
                    .nombre(nombresPerro[i])
                    .especie("Perro")
                    .raza(razas[AZAR.nextInt(razas.length)])
                    .edad(Math.round((AZAR.nextDouble() * 12 + 0.3) * 10) / 10.0)
                    .sexo(AZAR.nextBoolean() ? "Macho" : "Hembra")
                    .imagen(imagenPerro(i))
                    .pesoKg(Math.round((AZAR.nextDouble() * 40 + 2) * 10) / 10.0)
                    .color(colores[AZAR.nextInt(colores.length)])
                    .fechaIngreso(LocalDate.of(2025, 1, 1).plusDays(AZAR.nextInt(610)))
                    .enfermedad(enfermedades[AZAR.nextInt(enfermedades.length)])
                    .dueno(duenoAsignado)
                    .estado(i % 9 == 8 ? "Inactivo" : "Activo")
                    .build());
        }
        return creadas;
    }

    /** URL de foto de cachorro, alternando las dos que ya usa el sitio. */
    private String imagenPerro(int i) {
        return i % 2 == 0
                ? "https://imgs.search.brave.com/8AIQQXIiiwAhhRKhD87lE0EUhLc7Irg8eiwpgIx1x7U/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly9jZG4u/cGl4YWJheS5jb20v/cGhvdG8vMjAxNi8w/My8yNy8xOC8xMi9s/dW5hLTEyODMzNTZf/NjQwLmpwZw"
                : "https://imgs.search.brave.com/SpvOGsBrp_Lq8prflOD7fsZ5TRntmOqrUHvFvtSrWzg/rs:fit:860:0:0:0/g:ce/aHR0cHM6Ly90aHVt/YnMuZHJlYW1zdGlt/ZS5jb20vYi9zZW50/YWRhLWdyaXMtZGVs/LXBlcnJpdG8tZG9n/by1mcmFuYyVDMyVB/OXMtYWlzbGFkYS0x/MDE3NTU1MzAuanBn";
    }
}

package com.vetopia;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
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
 * Orden de siembra (respetando las FK): dueños -> mascotas -> veterinarios
 * -> tratamientos -> Administrador. Las drogas del inventario no se siembran
 * aquí: las carga DrogaExcelLoader desde el Excel antes de que corra este
 * cargador (@Order), y los tratamientos las referencian por nombre.
 *
 * A las 3 familias semilla originales (Ana/Pedro/María, con las
 * credenciales de prueba de los portales) se suman 50 clientes y 100
 * mascotas generadas con Random(41): la semilla del Random es fija, de
 * modo que cada arranque produce exactamente los mismos datos y las
 * capturas del proyecto son reproducibles (misma técnica del ejemplo
 * del curso).
 */
@Component
@Order(2)
public class DataLoader implements CommandLineRunner {

    /** Semilla fija del generador de datos (41). */
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
        // Con el perfil h2 las tablas se recranean en cada arranque, así
        // que la siembra siempre entra y los datos de demo se restauran.
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

        // ---- Veterinarios (5 activos para cumplir el estándar de
        // al menos 5 objetos por tabla) ----
        sembrarVeterinarios();
        Veterinario carlos = veterinarioRepository.findById(1).orElseThrow();
        Veterinario laura = veterinarioRepository.findById(2).orElseThrow();
        Veterinario jorge = veterinarioRepository.findById(3).orElseThrow();

        // ---- Mascotas generadas a escala (100 cániles) ----
        List<Mascota> mascotasGeneradas = generarMascotas(duenos);
        mascotaRepository.saveAll(mascotasGeneradas);

        // ---- Tratamientos (tabla intermedia con entidades reales;
        // 5 en total para cumplir el estándar de al menos 5 por tabla).
        // Las drogas se resuelven por nombre contra el inventario cargado
        // desde Excel, no por un id fijo. Las fechas son recientes (dentro
        // del último mes) para que el dashboard del administrador muestre
        // datos, y cada tratamiento ajusta el inventario como lo haría la
        // asignación real (una unidad menos disponible y una más vendida).
        Mascota luna = mascotaRepository.findById(2).orElseThrow();
        Mascota max = mascotaRepository.findById(1).orElseThrow();
        Mascota rocky = mascotaRepository.findById(3).orElseThrow();
        veterinarioRepository.findById(4).ifPresent(veterinario -> {
            tratamientoRepository.save(Tratamiento.builder()
                    .fecha(LocalDate.now().minusDays(3))
                    .mascota(luna)
                    .droga(droga("TILOSINA"))
                    .veterinario(veterinario)
                    .build());
            registrarVenta(droga("TILOSINA"));
        });
        veterinarioRepository.findById(5).ifPresent(veterinario -> {
            tratamientoRepository.save(Tratamiento.builder()
                    .fecha(LocalDate.now().minusDays(6))
                    .mascota(max)
                    .droga(droga("METROTAB"))
                    .veterinario(veterinario)
                    .build());
            registrarVenta(droga("METROTAB"));
        });
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.now().minusDays(12))
                .mascota(luna)
                .droga(droga("AMOXICILINA"))
                .veterinario(carlos)
                .build());
        registrarVenta(droga("AMOXICILINA"));
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.now().minusDays(18))
                .mascota(max)
                .droga(droga("DOXICICLINA"))
                .veterinario(laura)
                .build());
        registrarVenta(droga("DOXICICLINA"));
        tratamientoRepository.save(Tratamiento.builder()
                .fecha(LocalDate.now().minusDays(25))
                .mascota(rocky)
                .droga(droga("TETRACICLINA"))
                .veterinario(jorge)
                .build());
        registrarVenta(droga("TETRACICLINA"));

        // La siembra guarda los tratamientos directo en el repositorio, sin
        // pasar por TratamientoService (que es quien incrementa el contador
        // en la asignación real). Se sincroniza al final para que el listado
        // del administrador muestre las atenciones sembradas.
        sincronizarAtenciones();

        // Administradores al final (no dependen de nadie; 5 para cumplir
        // el estándar de al menos 5 objetos por tabla).
        administradorRepository.save(Administrador.builder().cedula("1000000001")
                .correo("admin@vetopia.com").contrasena("admin123").nombre("Dirección Vetopia").build());
        administradorRepository.save(Administrador.builder().cedula("1000000002")
                .correo("gerencia@vetopia.com").contrasena("gerencia123").nombre("Gerencia General").build());
        administradorRepository.save(Administrador.builder().cedula("1000000003")
                .correo("finanzas@vetopia.com").contrasena("finanzas123").nombre("Finanzas").build());
        administradorRepository.save(Administrador.builder().cedula("1000000004")
                .correo("inventario@vetopia.com").contrasena("inventario123").nombre("Control de Inventario").build());
        administradorRepository.save(Administrador.builder().cedula("1000000005")
                .correo("calidad@vetopia.com").contrasena("calidad123").nombre("Auditoría Clínica").build());
    }

    /**
     * Resuelve una droga del inventario cargado desde Excel por su nombre,
     * de modo que los tratamientos semilla no dependan de un id fijo.
     */
    private Droga droga(String nombre) {
        return drogaRepository.findByNombre(nombre).orElseThrow(
                () -> new IllegalStateException(
                        "No se encontró la droga \"" + nombre + "\" en el inventario del Excel."));
    }

    /**
     * Ajusta el contador de atenciones de cada veterinario al número de
     * tratamientos sembrados. Sin esto los veterinarios quedan en 0 aunque
     * tengan historial, porque la siembra no usa el service de asignación
     * (el único que incrementa el contador en la operación normal).
     */
    private void sincronizarAtenciones() {
        for (Veterinario veterinario : veterinarioRepository.findAll()) {
            int atenciones = tratamientoRepository
                    .findByVeterinario_Id(veterinario.getId()).size();
            veterinario.setNumeroAtenciones(atenciones);
            veterinarioRepository.save(veterinario);
        }
    }

    /**
     * Ajusta el inventario de una droga semilla como lo haría asignar():
     * una unidad menos disponible y una más vendida. Así los KPIs de
     * ventas, ganancias y el top de medicamentos arrancan con datos
     * coherentes con los tratamientos sembrados.
     */
    private void registrarVenta(Droga droga) {
        int disponibles = droga.getUnidadesDisponibles() == null ? 0 : droga.getUnidadesDisponibles();
        droga.setUnidadesDisponibles(Math.max(0, disponibles - 1));
        droga.setUnidadesVendidas(
                (droga.getUnidadesVendidas() == null ? 0 : droga.getUnidadesVendidas()) + 1);
        drogaRepository.save(droga);
    }

    /**
     * Siembra los cinco veterinarios activos del equipo.
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
                .numeroAtenciones(0).nombre("Jorge Santana").estado("Activo")
                .build());
        veterinarioRepository.save(Veterinario.builder().cedula("1002003004").contrasena("vet123")
                .correo("sofia.rincon@vetopia.com").especialidad("Odontología")
                .numeroAtenciones(0).nombre("Sofía Rincón").estado("Activo")
                .urlFoto("https://example.com/foto-sofia.jpg").build());
        veterinarioRepository.save(Veterinario.builder().cedula("1002003005").contrasena("vet123")
                .correo("diego.paredes@vetopia.com").especialidad("Nutrición")
                .numeroAtenciones(0).nombre("Diego Paredes").estado("Activo")
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

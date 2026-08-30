# Vetopia

**Vetopia – Hospital Veterinario del Futuro.** Aplicación web de un hospital veterinario con portal del cliente para consulta de mascotas registradas, construida con **Spring Boot** siguiendo el patrón de diseño de capas.

## Descripción

Aplicación servidor con separación clara de responsabilidades mediante el patrón de capas:

```
Controller  ->  Service  ->  Repository
(@Controller)   (@Service)   (@Repository, HashMap en memoria)
```

Cada capa se comunica únicamente con la capa inferior y las dependencias se gestionan con inyección por `@Autowired`, de modo que Spring crea e inyecta automáticamente las instancias de cada clase.

## Estructura del proyecto

```
Vetopia/
├── pom.xml                                  # Configuración Maven (Spring Boot 3.5.4, Lombok, Thymeleaf)
└── src/main/
    ├── java/com/vetopia/
    │   ├── VetopiaApplication.java          # Clase principal (@SpringBootApplication)
    │   ├── controller/
    │   │   ├── HomeController.java          # Raíz "/" y login "/login"
    │   │   ├── VeterinarioMascotaController.java  # @Controller + @RequestMapping("/veterinario/mascotas")
    │   │   └── ClienteMascotaController.java      # @Controller + @RequestMapping("/cliente/mascotas")
    │   ├── service/
    │   │   ├── MascotaService.java          # Interfaz de lógica de negocio
    │   │   └── MascotaServiceImpl.java      # @Service (inyecta el repositorio con @Autowired)
    │   ├── repository/
    │   │   ├── MascotaRepository.java       # Interfaz DAO
    │   │   └── MascotaRepositoryImpl.java   # @Repository con HashMap (persistencia simulada)
    │   └── entities/
    │       └── Mascota.java                 # Entidad con Lombok (@Data, @NoArgsConstructor, @AllArgsConstructor)
    └── resources/
        ├── application.properties
        ├── templates/                       # Vistas Thymeleaf
        │   ├── principal-cliente.html       # Listado de mascotas (portal del cliente)
        │   ├── fragmentos-portal.html       # Fragments head/footer del portal
        │   ├── fragmentos-landing.html      # Fragments head/footer de la landing
        │   ├── landing/index.html           # Landing institucional
        │   ├── login/login.html             # Página de inicio de sesión
        │   ├── cliente/
        │   │   └── detalle-mascota.html     # Detalle de una mascota (portal del cliente)
        │   └── veterinario/                 # Vistas del portal del veterinario
        │       ├── mascotas-cargo.html      # Listado de mascotas a cargo
        │       ├── ficha-clinica.html       # Ficha clínica de una mascota
        │       ├── registrar-cliente.html   # Formulario de registro de cliente
        │       ├── registrar-mascotas.html  # Formulario de registro de mascota
        │       ├── asignar-tratamiento.html # Formulario de asignación de tratamiento
        │       └── tratamiento-confirmacion.html # Confirmación de tratamiento
        └── static/
            ├── css/styles.css               # Estilos personalizados del landing
            ├── js/main.js                   # Efecto glass del header al hacer scroll
            └── images/                      # Logo y recursos visuales
```

## Requisitos

- JDK 21 o superior
- Maven 3.9+

## Cómo ejecutar

```bash
mvn spring-boot:run
```

Luego abre en el navegador:

| Ruta | Descripción |
|---|---|
| `http://localhost:8080/` | Landing institucional |
| `http://localhost:8080/login` | Inicio de sesión |
| `http://localhost:8080/veterinario/mascotas` | Portal del veterinario: listado de mascotas a cargo |
| `http://localhost:8080/veterinario/mascotas/ficha?id=1` | Ficha clínica de la mascota con id 1 |
| `http://localhost:8080/cliente/mascotas` | Portal del cliente: listado de mascotas |
| `http://localhost:8080/cliente/mascotas/detalle?id=1` | Detalle de la mascota con id 1 |

## Funcionalidades

- **Listado de mascotas** ordenadas alfabéticamente (regla de negocio en el Service).
- **Detalle de mascota** con validación del identificador y panel amigable cuando no existe.
- **Persistencia simulada** en memoria (`HashMap`) precargada con datos de ejemplo; al conectar una base de datos real solo cambia la implementación del repositorio.

## Tecnologías

- Java 21
- Spring Boot 3.5.4 (Web + Thymeleaf)
- Lombok
- Tailwind CSS (vía CDN) con paleta Material Design M3 personalizada
- Google Fonts: Hanken Grotesk, Bricolage Grotesque, JetBrains Mono y Material Symbols Outlined

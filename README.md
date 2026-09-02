# Vetopia

<p align="center">
  <img src="docs/logo/logo_Vetopia_letrasNegras.png" alt="Logo de Vetopia" width="320">
</p>

**Vetopia – Hospital Veterinario del Futuro.** Sistema web de gestión para la clínica veterinaria Vetopia: los veterinarios administran a los **dueños**, sus **mascotas** y sus **tratamientos**, y cada dueño consulta desde su propio portal únicamente la información de sus mascotas.

Construida con **Spring Boot** siguiendo el patrón de diseño de capas:

```
Controller  ->  Service  ->  Repository
(@Controller)   (@Service)   (@Repository, HashMap en memoria)
```

Cada capa se comunica únicamente con la capa inferior y las dependencias se gestionan con inyección por `@Autowired`, de modo que Spring crea e inyecta automáticamente las instancias de cada clase.

## Funcionalidades

- **Landing institucional** y **login por rol** (veterinario, administrador y cliente/dueño).
- **CRUD de mascotas por veterinario**: crear, listar, editar, desactivar y ver ficha clínica.
- **CRUD de dueños por veterinario**: crear, listar, editar, desactivar (baja lógica reversible) y **eliminar de forma definitiva** junto con todas sus mascotas (borrado en cascada).
- **Dueño obligatorio al registrar una mascota**: validado en el formulario y nuevamente en el servidor.
- **Asignación de tratamientos** con medicamentos del inventario y descuento de existencias.
- **Portal del cliente**: cada dueño ve únicamente **sus** mascotas, con su nombre en el encabezado.
- **Control de acceso por estado**: los clientes desactivados **no pueden iniciar sesión** hasta ser reactivados.

## Tecnologías

- Java 21
- Spring Boot 3.5.4 (Web + Thymeleaf)
- Lombok
- Tailwind CSS (vía CDN) con paleta Material Design M3 personalizada (`docs/color-palette/paleta-colores-vetopia.jpeg`)
- Google Fonts: Hanken Grotesk, Bricolage Grotesque, JetBrains Mono y Material Symbols Outlined
- Persistencia simulada en memoria (`HashMap`) precargada con datos de ejemplo; al conectar una base de datos real solo cambia la implementación del repositorio.

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
| `http://localhost:8080/veterinario/mascotas` | Portal del veterinario: listado de mascotas |
| `http://localhost:8080/veterinario/mascotas/clientes` | Portal del veterinario: listado de dueños |
| `http://localhost:8080/veterinario/mascotas/ficha?id=1` | Ficha clínica de la mascota con id 1 |
| `http://localhost:8080/cliente/mascotas?idUsuario=1` | Portal del cliente: mascotas del dueño con id 1 |

## Credenciales de demostración

| Rol | Usuario | Contraseña | Estado |
|---|---|---|---|
| Veterinario | `carlos.gutierrez@vetopia.com` | `vet123` | — |
| Administrador | `admin@vetopia.com` | `admin123` | — |
| Cliente | `ana@correo.com` | `dueño123` | Activa |
| Cliente | `pedro@correo.com` | `dueño123` | Activa |
| Cliente | `maria@correo.com` | `dueño123` | Inactiva (no puede iniciar sesión) |

## Diagrama de clases

El modelo de dominio del negocio está documentado en [`docs/diagrams/class-diagram.svg`](docs/diagrams/class-diagram.svg) (script PlantUML en [`docs/diagrams/script-class-diagram.txt`](docs/diagrams/script-class-diagram.txt)): `Veterinario`, `Dueno`, `Mascota`, `Tratamiento`, `Droga` y `Administrador`, con sus relaciones y operaciones.

## Estructura del proyecto

```
Vetopia/
├── pom.xml                                  # Configuración Maven (Spring Boot 3.5.4, Lombok, Thymeleaf)
├── docs/                                    # Logo, paleta de colores y diagrama de clases
└── src/main/
    ├── java/com/vetopia/
    │   ├── VetopiaApplication.java          # Clase principal (@SpringBootApplication)
    │   ├── controller/
    │   │   ├── HomeController.java          # Raíz "/" y login "/login"
    │   │   ├── VeterinarioMascotaController.java  # @Controller + @RequestMapping("/veterinario/mascotas")
    │   │   └── ClienteMascotaController.java      # @Controller + @RequestMapping("/cliente/mascotas")
    │   ├── service/                         # Contratos e implementaciones de lógica de negocio
    │   ├── repository/                      # Contratos DAO e implementaciones con HashMap
    │   └── entities/                        # Mascota, Dueno, Veterinario, Tratamiento, Droga, Administrador
    └── resources/
        ├── application.properties
        ├── templates/                       # Vistas Thymeleaf (landing, login, veterinario, cliente)
        └── static/                          # CSS, JS e imágenes del landing
```

## Estrategia de ramas

| Rama | Propósito |
|---|---|
| `main` | **Producción**: versión estable del entregable |
| `develop` | **Desarrollo**: integración continua del trabajo del equipo |
| `feature/*` | Funcionalidades específicas; se integran de `feature/*` → `develop` → `main` |

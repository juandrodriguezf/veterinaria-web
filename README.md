# Vetopia

<p align="center">
  <img src="docs/logo/logo_Vetopia_letrasNegras.png" alt="Logo de Vetopia" width="320">
</p>

**Vetopia – Hospital Veterinario del Futuro.** Sistema web de gestión para la clínica veterinaria Vetopia: los veterinarios administran a los **dueños**, sus **mascotas** y sus **tratamientos**, y cada dueño consulta desde su propio portal únicamente la información de sus mascotas.

Construida con **Spring Boot** siguiendo el patrón de diseño de capas:

```
Controller  ->  Service  ->  Repository
(@Controller)   (@Service)   (interfaces JpaRepository)
```

Cada capa se comunica únicamente con la capa inferior y las dependencias se gestionan con inyección de dependencias de Spring (constructor en `DataLoader`, `@Autowired` en el resto). La persistencia es real: **Spring Data JPA 100% con queries derivadas** (sin JPQL) sobre **H2**, siguiendo los patrones del material del curso (`7.JPA_avanzado`).

## Funcionalidades

- **Landing institucional** y **login por rol** (veterinario, administrador y cliente/dueño).
- **CRUD de mascotas por veterinario**: crear, listar, editar, desactivar y ver ficha clínica.
- **CRUD de dueños por veterinario**: crear, listar, editar, desactivar (baja lógica reversible) y **eliminar de forma definitiva** junto con todas sus mascotas (borrado en cascada).
- **Dueño obligatorio al registrar una mascota**: validado en el formulario y nuevamente en el servidor.
- **Asignación de tratamientos** con medicamentos del inventario y descuento de existencias.
- **Portal del cliente**: cada dueño ve únicamente **sus** mascotas, con su nombre en el encabezado.
- **Página amable de errores**: los recursos inexistentes y los errores inesperados aterrizan en `error.html` con su causa (`errors/GlobalExceptionHandler`, `@ControllerAdvice`).

## Tecnologías

- Java 21
- Spring Boot 3.5.4 (Web + Thymeleaf)
- **Spring Data JPA + H2**: entidades con `@Entity`, `@Column(nullable/unique/length)`, `@ManyToOne` con IDENTITY; repositorios `JpaRepository` con consultas derivadas (sin JPQL); borrado en cascada por capas desde el service con `@Transactional` (no depende del DDL)
- Lombok
- Tailwind CSS compilado a hojas de estilo propias (`static/css/tailwind-landing.css` y `tailwind-portal.css`) con paleta Material Design M3 personalizada (`docs/color-palette/paleta-colores-vetopia.jpeg`)
- Google Fonts: Hanken Grotesk, Bricolage Grotesque, JetBrains Mono y Material Symbols Outlined
- `DataLoader` (`CommandLineRunner`) siembra datos de prueba al arrancar si la base está vacía (50 clientes, 100 perros generados con `Random(41)`, semilla del ejemplo del curso)
- Manejo de errores con `errors/GlobalExceptionHandler` sobre `templates/error.html`

## Datos de prueba

Al arrancar con la base vacía, `DataLoader` siembra:

- **53 clientes** (los 3 credenciales de demostración + 50 generados, cadencia de 1 inactivo cada 10)
- **103 mascotas**: Max, Luna y Rocky + 100 perros generados (edades 0.3–12 años, pesos 2–45 kg, 8 enfermedades de ejemplo, cadencia de 1 inactiva cada 9)
- 3 drogas del inventario, 3 veterinarios y 3 administradores
- 3 tratamientos de ejemplo conectando mascota × droga × veterinario

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
| `http://localhost:8080/veterinario/mascotas/ficha?id=9999` | Página amable de error (recurso inexistente) |

## Credenciales de demostración

| Rol | Usuario | Contraseña | Estado |
|---|---|---|---|
| Veterinario | `carlos.gutierrez@vetopia.com` | `vet123` | — |
| Administrador | `admin@vetopia.com` | `admin123` | — |
| Cliente | `ana@correo.com` | `dueño123` | Activa |
| Cliente | `pedro@correo.com` | `dueño123` | Activa |
| Cliente | `maria@correo.com` | `dueño123` | Inactiva (no puede iniciar sesión) |

## Diagrama de clases

El modelo de dominio del negocio está documentado en [`docs/diagrams/class-diagram.svg`](docs/diagrams/class-diagram.svg): `Veterinario`, `Dueno`, `Mascota`, `Tratamiento`, `Droga` y `Administrador`, con sus relaciones y operaciones.

## Diagrama E/R

Modelo de la base de datos, documentado en [`docs/diagrams/er-diagram.svg`](docs/diagrams/er-diagram.svg). Es la versión persistida del modelo de dominio: las relaciones 1–N quedan como FK (`dueno_id`, `mascota_id`, `droga_id`, `veterinario_id` y la opcional `administrador_id`), las columnas conservan los mismos límites de las anotaciones `@Column` de las entidades JPA y la nulabilidad de cada una se señala como `«NOT NULL»` (obligatoria) u `«NULL»` (admite nulo).

## Mockups (Figma)

Prototipo de alta fidelidad de la interfaz: [Vetopia en Figma](https://www.figma.com/design/EUzRoGpnaSxhlcG9x3kk0s/Vetopia?node-id=0-1).

| Pantalla en Figma | Ruta / plantilla en la app | Estado |
|---|---|---|
| Landing Page | `/` → `landing/index.html` | Implementada |
| Log In | `/login` → `login/login.html` | Implementada |
| Principal Cliente | `/cliente/mascotas?idUsuario=…` → `principal-cliente.html` | Implementada |
| Detalle mascota Cliente | `/cliente/mascotas/detalle` → `cliente/detalle-mascota.html` | Implementada |
| Mascotas a Cargo | `/veterinario/mascotas` → `veterinario/mascotas-cargo.html` | Implementada |
| Ficha Clínica | `/veterinario/mascotas/ficha` → `veterinario/ficha-clinica.html` | Implementada |
| Registrar Nuevo Cliente (paso 1) | `/veterinario/mascotas/clientes/crear` → `veterinario/cliente-form.html` | Implementada |
| Section 2: Pet Information (paso 2) | `/veterinario/mascotas/registrar-mascota` → `veterinario/mascota-form.html` | Implementada |
| Asignar Nuevo Tratamiento | `/veterinario/mascotas/asignar-tratamiento` → `veterinario/asignar-tratamiento.html` | Implementada |
| Tratamiento Aplicado (modal) | misma ruta → `veterinario/tratamiento-confirmacion.html` | Implementada |
| Dashboard Administrativo | — | Futura |
| Gestión Veterinarios | — | Futura |

Las dos pantallas de administrador del prototipo quedan fuera del alcance de la Entrega 1: hoy el inicio de sesión con rol administrador redirige al landing. El prototipo también anticipa rutas aún no implementadas (`Citas`, `Historial Tratamientos`, `Registrar Nuevo Veterinario`, `Ver Perfil Completo` y recuperar contraseña).

## Frontend Angular (Entrega 2)

La carpeta [`frontend/`](frontend/README.md) contiene la aplicación cliente, construida con **Angular 19** (componentes standalone) y **Tailwind CSS 3**. Reproduce el landing institucional y las tres pantallas del CRUD de mascotas del veterinario (listado, formulario y ficha clínica), con los mismos datos de prueba sembrados en sus servicios y sin llamadas al backend todavía.

| Ruta | Descripción |
|---|---|
| `http://localhost:4200/` | Landing institucional |
| `http://localhost:4200/mascotas` | Portal del veterinario: listado de mascotas |
| `http://localhost:4200/mascotas/new` | Registrar mascota |
| `http://localhost:4200/mascotas/update/:id` | Editar mascota |
| `http://localhost:4200/mascotas/:id` | Ficha clínica de la mascota |

```bash
cd frontend
npm install
npm start
```

El detalle de la estructura, los datos quemados, las decisiones de diseño y las pruebas está en el [README del frontend](frontend/README.md).

## Estructura del proyecto

```
Vetopia/
├── pom.xml                                  # Configuración Maven (Spring Boot 3.5.4, JPA, H2, Lombok, Thymeleaf)
├── docs/                                    # Logo, paleta de colores y diagramas (clases + E/R)
├── frontend/                                # Aplicación cliente en Angular 19 + Tailwind (Entrega 2)
└── src/main/
    ├── java/com/vetopia/
    │   ├── VetopiaApplication.java          # Clase principal (@SpringBootApplication)
    │   ├── DataLoader.java                  # Siembra datos de prueba al arranque (CommandLineRunner)
    │   ├── controller/
    │   │   ├── HomeController.java          # Raíz "/" y login "/login"
    │   │   ├── VeterinarioController.java   # Portal del veterinario (mascotas, dueños, tratamientos)
    │   │   └── ClienteController.java       # Portal del cliente (mascotas visible solo de su dueño)
    │   ├── service/                         # Contratos e implementaciones de lógica de negocio
    │   ├── repository/                      # Interfaces JpaRepository (consultas derivadas, sin JPQL)
    │   ├── entities/                        # Mascota, Dueno, Veterinario, Tratamiento, Droga, Administrador
    │   └── errors/                          # RecursoNoEncontradoException + GlobalExceptionHandler (@ControllerAdvice)
    └── resources/
        ├── application.properties
        ├── templates/                       # Vistas Thymeleaf (landing, login, veterinario, cliente, error)
        └── static/                          # CSS, JS e imágenes del landing
```

## Estrategia de ramas

| Rama | Propósito |
|---|---|
| `main` | **Producción**: versión estable del entregable |
| `develop` | **Desarrollo**: integración continua del trabajo del equipo |
| `feature/*` | Funcionalidades específicas; se integran de `feature/*` → `develop` → `main` |

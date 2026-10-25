# Vetopia

<p align="center">
  <img src="docs/logo/logo_Vetopia_letrasNegras.png" alt="Logo de Vetopia" width="320">
</p>

**Vetopia – Hospital Veterinario del Futuro.** Sistema de gestión para la clínica veterinaria Vetopia: los veterinarios administran a los **dueños**, sus **mascotas** y sus **tratamientos**. El proyecto tiene dos partes: un **backend Spring Boot** que expone una **API REST** y un **frontend Angular** que la consume con `HttpClient`.

Construida con **Spring Boot** siguiendo el patrón de diseño de capas:

```
Controller       ->  Service  ->  Repository
(@RestController)   (@Service)   (interfaces JpaRepository)
```

Cada capa se comunica únicamente con la capa inferior y las dependencias se gestionan con inyección de dependencias de Spring (constructor en `DataLoader`, `@Autowired` en el resto). La persistencia es real: **Spring Data JPA 100% con queries derivadas** (sin JPQL) sobre **H2**, siguiendo los patrones del material del curso (`7.JPA_avanzado`).

## Funcionalidades

- **API REST de mascotas**: listar, consultar por id, buscar por nombre, listar por dueño (con búsqueda opcional por nombre), listar las activas, consultar una mascota propia de un dueño, crear, actualizar, alternar estado y eliminar.
- **API REST de dueños (clientes)**: listar, consultar, buscar por nombre, crear, actualizar, alternar estado (baja lógica reversible) y **eliminar de forma definitiva** junto con todas sus mascotas; el historial de tratamientos se conserva porque los tratamientos quedan **desvinculados** de la mascota en lugar de borrarse.
- **API REST de tratamientos y drogas**: consultar el historial de una mascota o de un veterinario, asignar un tratamiento (valida mascota activa y stock, descuenta una unidad del inventario, registra la unidad vendida y devuelve la droga actualizada) y listar/registrar el inventario de medicamentos, que se **carga desde un Excel fijo** al arrancar.
- **API REST de veterinarios y administradores**: CRUD de veterinarios con búsqueda por cédula o nombre, activar/desactivar y eliminar (desvinculando sus tratamientos); además el administrador consulta el **dashboard** con los indicadores del negocio.
- **API REST de login**: autentica por correo o cédula y contraseña, busca el usuario en las tablas de veterinario, administrador y dueño, y devuelve el **rol** y el **id** para que el frontend navegue a su portal; los veterinarios solo entran si están **activos**.
- **Portal del cliente**: cada dueño consulta únicamente **sus** mascotas (con búsqueda por nombre) y el detalle de cada una con su historial de tratamientos.
- **Portal del administrador**: dashboard con los indicadores del negocio (tratamientos del último mes, veterinarios activos e inactivos, mascotas activas e inactivas, ventas y ganancias, y las drogas más usadas) y gestión de veterinarios con búsqueda, alta, edición, activación/desactivación y eliminación.
- **Portal del veterinario**: pestaña **Tratadas** con las mascotas que ha tratado, ordenadas por la última atención, con búsqueda por nombre.
- **Documentación interactiva** de la API con **Swagger UI** y su especificación OpenAPI en JSON.
- **Frontend Angular** que consume la API con `HttpClient` (ver [`frontend/`](frontend/README.md)).

## Tecnologías

- Java 21
- Spring Boot 3.5.4 (Web)
- **Spring Data JPA + H2**: entidades con `@Entity`, `@Column(nullable/unique/length)`, `@ManyToOne` con IDENTITY; repositorios `JpaRepository` con consultas derivadas (sin JPQL); borrado en cascada por capas desde el service con `@Transactional` (no depende del DDL); la FK `tratamiento.mascota` admite nulo y el service la desvincula antes de eliminar la mascota, de modo que el historial clínico sobrevive a la eliminación del dueño y sus mascotas
- Lombok
- **springdoc-openapi** para documentar la API (Swagger UI en `/swagger-ui/index.html` y OpenAPI en `/v3/api-docs`)
- **Apache POI** para leer el inventario de medicamentos desde un Excel fijo (`resources/data/medicamentos.xlsx`)
- `DrogaExcelLoader` (`CommandLineRunner`) carga el inventario de drogas desde ese Excel al arrancar con **upsert por nombre**: si el nombre ya existe actualiza precios y unidades, y si no registra la droga
- `DataLoader` (`CommandLineRunner`) siembra el resto de los datos de prueba al arrancar si la base está vacía (53 clientes, 103 mascotas —100 perros generados con `Random(41)`, semilla del ejemplo del curso—, 5 veterinarios, 5 administradores y 5 tratamientos que referencian drogas del Excel, con fechas de los últimos días y su unidad vendida registrada para que el dashboard arranque con datos coherentes)
- La interfaz (Tailwind CSS con la paleta Material Design M3 de `docs/color-palette/paleta-colores-vetopia.jpeg` y Google Fonts) vive en el frontend Angular

## API REST

Todos los controladores son `@RestController`, viven en `controller/api/` y están abiertos a `http://localhost:4200` con `@CrossOrigin`. Las entidades se exponen directamente como JSON y las colecciones `@OneToMany` van con `@JsonIgnore` para cortar la recursión de Jackson; por eso las relaciones se consultan con rutas aparte (por ejemplo, las mascotas de un dueño o el historial de una mascota). En `Tratamiento` las relaciones viajan planas —`drogaId`, `drogaNombre`, `veterinarioId`, `veterinarioNombre`, `veterinarioEspecialidad`, `mascotaId` y `mascotaNombre`— y solo se reciben por id, de modo que Angular no anida objetos.

| Método | Ruta | Descripción |
|---|---|---|
| `GET` | `/mascota/all` | Todas las mascotas |
| `GET` | `/mascota/find/{id}` | Una mascota por id |
| `GET` | `/mascota/search/{nombre}` | Mascotas por nombre (coincidencia parcial, sin distinguir mayúsculas) |
| `GET` | `/mascota/byDueno/{duenoId}` | Mascotas de un dueño (búsqueda opcional con `?nombre=`) |
| `GET` | `/mascota/propia/{id}/{duenoId}` | Una mascota validando que pertenezca al dueño |
| `GET` | `/mascota/activas` | Solo las mascotas activas |
| `POST` | `/mascota/add` | Crea una mascota (dueño obligatorio, estado inicial Activo) |
| `PUT` | `/mascota/update` | Actualiza una mascota |
| `PUT` | `/mascota/toggle/{id}` | Alterna el estado (`Activo` ↔ `Inactivo`) y lo devuelve |
| `DELETE` | `/mascota/delete/{id}` | Elimina una mascota (desvincula su historial) |
| `GET` | `/dueno/all` | Todos los clientes |
| `GET` | `/dueno/find/{id}` | Un cliente por id |
| `GET` | `/dueno/search/{nombre}` | Clientes por nombre (coincidencia exacta, sin distinguir mayúsculas) |
| `POST` | `/dueno/add` | Crea un cliente (valida cédula y correo únicos) |
| `PUT` | `/dueno/update` | Actualiza un cliente |
| `PUT` | `/dueno/toggle/{id}` | Alterna el estado y lo devuelve |
| `DELETE` | `/dueno/delete/{id}` | Elimina un cliente y, en cascada, sus mascotas |
| `GET` | `/tratamiento/all` | Todos los tratamientos |
| `GET` | `/tratamiento/find/{id}` | Un tratamiento por id |
| `GET` | `/tratamiento/byMascota/{mascotaId}` | Historial de tratamientos de una mascota |
| `GET` | `/tratamiento/byVeterinario/{veterinarioId}` | Historial de tratamientos aplicados por un veterinario |
| `POST` | `/tratamiento/add` | Asigna un tratamiento (valida mascota activa y stock; descuenta inventario y registra la unidad vendida) |
| `GET` | `/droga/all` | Inventario de medicamentos |
| `GET` | `/droga/find/{id}` | Una droga por id |
| `POST` | `/droga/add` | Registra una droga |
| `GET` | `/veterinario/all` | Todos los veterinarios |
| `GET` | `/veterinario/find/{id}` | Un veterinario por id |
| `GET` | `/veterinario/search/{termino}` | Veterinarios por cédula o nombre (coincidencia parcial) |
| `POST` | `/veterinario/add` | Registra un veterinario (valida cédula y correo únicos; clave por defecto `vet123`) |
| `PUT` | `/veterinario/update` | Actualiza un veterinario (si la clave llega vacía se conserva) |
| `PUT` | `/veterinario/toggle/{id}` | Activa o desactiva un veterinario |
| `DELETE` | `/veterinario/delete/{id}` | Elimina un veterinario (desvincula sus tratamientos) |
| `GET` | `/administrador/all` | Todos los administradores |
| `GET` | `/administrador/find/{id}` | Un administrador por id |
| `GET` | `/administrador/dashboard` | Indicadores del negocio para el dashboard |
| `POST` | `/administrador/add` | Registra un administrador |
| `POST` | `/login` | Autentica por correo o cédula y contraseña; devuelve `{rol, id}` o `401 {mensaje}` |

La API se puede explorar y probar desde **Swagger UI** (`/swagger-ui/index.html`); cada operación lleva su `@Operation(summary = "…")` y cada recurso su `@Tag`.

## Datos de prueba

Al arrancar, la aplicación carga los datos de prueba:

- **523 drogas**: el inventario completo se lee del Excel fijo `resources/data/medicamentos.xlsx` con `DrogaExcelLoader` (upsert por nombre)
- **53 clientes** (los 3 clientes semilla + 50 generados, cadencia de 1 inactivo cada 10)
- **103 mascotas**: Max, Luna y Rocky + 100 perros generados (edades 0.3–12 años, pesos 2–45 kg, 8 enfermedades de ejemplo, cadencia de 1 inactiva cada 9)
- **5 veterinarios** y **5 administradores**
- **5 tratamientos** de ejemplo conectando mascota × droga × veterinario (las drogas se resuelven por nombre contra el inventario del Excel)

## Requisitos

- JDK 21 o superior
- Maven 3.9+

## Cómo ejecutar

```bash
mvn spring-boot:run
```

El backend queda en `http://localhost:8080`. Rutas útiles:

| Ruta | Descripción |
|---|---|
| `http://localhost:8080/swagger-ui/index.html` | Documentación interactiva de la API (Swagger UI) |
| `http://localhost:8080/v3/api-docs` | Especificación OpenAPI en JSON |
| `http://localhost:8080/h2` | Consola de la base de datos H2 |
| `http://localhost:8080/mascota/all` | Ejemplo: todas las mascotas |
| `http://localhost:8080/dueno/all` | Ejemplo: todos los dueños |

La interfaz de usuario vive en el [frontend Angular](frontend/README.md) (`http://localhost:4200`), que consume esta API con `HttpClient`.

## Credenciales de demostración

`DataLoader` siembra usuarios de ejemplo para los tres roles que resuelve el login (`POST /login`):

| Rol | Usuario | Contraseña | Estado |
|---|---|---|---|
| Veterinario | `carlos.gutierrez@vetopia.com` | `vet123` | — |
| Administrador | `admin@vetopia.com` | `admin123` | — |
| Cliente | `ana@correo.com` | `dueño123` | Activa |
| Cliente | `pedro@correo.com` | `dueño123` | Activa |
| Cliente | `maria@correo.com` | `dueño123` | Inactiva (no inicia sesión) |

El login también acepta la **cédula** como usuario (por ejemplo, `1002003001` para Carlos, `1000000001` para el administrador o `1020304050` para Ana), manteniendo la misma contraseña.

## Diagrama de clases

El modelo de dominio del negocio está documentado en [`docs/diagrams/class-diagram.svg`](docs/diagrams/class-diagram.svg): `Veterinario`, `Dueno`, `Mascota`, `Tratamiento`, `Droga` y `Administrador`, con sus relaciones y operaciones.

## Diagrama E/R

Modelo de la base de datos, documentado en [`docs/diagrams/er-diagram.svg`](docs/diagrams/er-diagram.svg). Es la versión persistida del modelo de dominio: las relaciones 1–N quedan como FK (`dueno_id`, `droga_id` y `veterinario_id`) y las que admiten nulo (`mascota_id` y `administrador_id`), las columnas conservan los mismos límites de las anotaciones `@Column` de las entidades JPA y la nulabilidad de cada una se señala como `«NOT NULL»` (obligatoria) u `«NULL»` (admite nulo).

## Mockups (Figma)

Prototipo de alta fidelidad de la interfaz: [Vetopia en Figma](https://www.figma.com/design/EUzRoGpnaSxhlcG9x3kk0s/Vetopia?node-id=0-1).

| Pantalla en Figma | Ruta en el frontend | Estado |
|---|---|---|
| Landing Page | `/` | Implementada |
| Log In | `/login` | Implementada |
| Principal Cliente | `/cliente/mascotas/:idUsuario` | Implementada |
| Detalle mascota Cliente | `/cliente/mascotas/:idUsuario/detalle/:id` | Implementada |
| Mascotas a Cargo | `/mascotas` | Implementada |
| Ficha Clínica | `/mascotas/:id` | Implementada |
| Registrar Nuevo Cliente (paso 1) | `/clientes/new` | Implementada |
| Section 2: Pet Information (paso 2) | `/mascotas/new` | Implementada |
| Asignar Nuevo Tratamiento | `/tratamiento/asignar` | Implementada |
| Tratamiento Aplicado (modal) | misma ruta (modal de confirmación) | Implementada |
| Dashboard Administrativo | `/admin/dashboard` | Implementada |
| Gestión Veterinarios | `/admin/veterinarios` | Implementada |

El prototipo también anticipa rutas aún no implementadas (`Citas`, `Historial Tratamientos`, `Ver Perfil Completo` y recuperar contraseña); "Registrar Nuevo Veterinario" se cubre desde el botón *Nuevo veterinario* del portal del administrador.

## Frontend Angular

La carpeta [`frontend/`](frontend/README.md) contiene la aplicación cliente, construida con **Angular 19** (componentes standalone) y **Tailwind CSS 3**. Reproduce el landing institucional, las tres pantallas del CRUD de mascotas (listado, formulario y ficha clínica) y las dos del CRUD de dueños (listado y formulario), y **consume esta API REST con `HttpClient`** (los servicios ya no tienen datos quemados).

| Ruta | Descripción |
|---|---|
| `http://localhost:4200/` | Landing institucional |
| `http://localhost:4200/login` | Inicio de sesión (rol veterinario, administrador o cliente) |
| `http://localhost:4200/mascotas` | Listado de mascotas |
| `http://localhost:4200/mascotas/new` | Registrar mascota |
| `http://localhost:4200/mascotas/update/:id` | Editar mascota |
| `http://localhost:4200/mascotas/:id` | Ficha clínica de la mascota |
| `http://localhost:4200/clientes` | Listado de dueños |
| `http://localhost:4200/clientes/new` | Registrar cliente |
| `http://localhost:4200/clientes/update/:id` | Editar cliente |
| `http://localhost:4200/cliente/mascotas/:idUsuario` | Portal del cliente: sus mascotas |
| `http://localhost:4200/cliente/mascotas/:idUsuario/detalle/:id` | Detalle de una mascota del cliente |
| `http://localhost:4200/tratamiento/asignar` | Asignar un tratamiento |
| `http://localhost:4200/veterinario/tratadas` | Mascotas tratadas por el veterinario |
| `http://localhost:4200/admin/dashboard` | Dashboard del administrador |
| `http://localhost:4200/admin/veterinarios` | Gestión de veterinarios |
| `http://localhost:4200/admin/veterinarios/new` | Registrar veterinario |
| `http://localhost:4200/admin/veterinarios/update/:id` | Editar veterinario |

```bash
cd frontend
npm install
npm start
```

El detalle de la estructura, los servicios HTTP, las decisiones de diseño y las pruebas está en el [README del frontend](frontend/README.md).

## Estructura del proyecto

```
Vetopia/
├── pom.xml                                  # Configuración Maven (Spring Boot 3.5.4, Web, JPA, H2, Lombok, springdoc, POI)
├── docs/                                    # Logo, paleta de colores y diagramas (clases + E/R)
├── frontend/                                # Aplicación cliente en Angular 19 + Tailwind
└── src/main/
    ├── java/com/vetopia/
    │   ├── VetopiaApplication.java          # Clase principal (@SpringBootApplication)
    │   ├── DataLoader.java                  # Siembra datos de prueba al arranque (CommandLineRunner)
    │   ├── DrogaExcelLoader.java            # Carga el inventario de drogas desde el Excel (CommandLineRunner)
    │   ├── controller/api/                  # Controladores REST (@RestController) por recurso
    │   ├── dto/                             # Payload plano del dashboard (ResumenDashboard, ConteoMedicamento)
    │   ├── service/                         # Contratos e implementaciones de lógica de negocio
    │   ├── repository/                      # Interfaces JpaRepository (consultas derivadas, sin JPQL)
    │   ├── entities/                        # Mascota, Dueno, Veterinario, Tratamiento, Droga, Administrador
    │   └── errors/                          # RecursoNoEncontradoException
    └── resources/
        ├── data/medicamentos.xlsx           # Inventario de medicamentos que carga DrogaExcelLoader
        ├── application.properties           # Puerto y perfil activo (h2)
        └── application-h2.properties        # Datasource H2 en archivo, consola y ddl-auto
```

## Estrategia de ramas

| Rama | Propósito |
|---|---|
| `main` | **Producción**: versión estable del entregable |
| `develop` | **Desarrollo**: integración continua del trabajo del equipo |
| `feature/*` | Funcionalidades específicas; se integran de `feature/*` → `develop` → `main` |

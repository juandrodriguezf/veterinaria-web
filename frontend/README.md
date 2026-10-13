# Vetopia Frontend

Frontend del sistema **Vetopia – Hospital Veterinario del Futuro**, construido con **Angular 19** (componentes standalone) y **Tailwind CSS 3**.

Vive en la carpeta `frontend/` de este repositorio, junto al backend Spring Boot (Maven, JPA y H2). **Consume la API REST** del backend con `HttpClient` (patrón de la clase de REST): los servicios ya no siembran datos en memoria y todas las consultas y comandos van contra `http://localhost:8080`.

## Alcance

Este proyecto reproduce las pantallas del prototipo que el backend ya soporta, para dejar el mismo aspecto y el mismo comportamiento, y las conecta a su API REST.

| Pantalla del prototipo | Ruta en Angular | Estado |
|---|---|---|
| Landing Page | `/` | Portada |
| Log In | `/login` | Portada |
| Mascotas a Cargo | `/mascotas` | Portada |
| Section 2: Pet Information (registrar mascota) | `/mascotas/new` | Portada |
| Section 2: Pet Information (editar mascota) | `/mascotas/update/:id` | Portada |
| Ficha Clínica | `/mascotas/:id` | Portada |
| Clientes (listado) | `/clientes` | Portada |
| Registrar Nuevo Cliente (crear) | `/clientes/new` | Portada |
| Registrar Nuevo Cliente (editar) | `/clientes/update/:id` | Portada |
| Asignar Nuevo Tratamiento / Tratamiento Aplicado | `/tratamiento/asignar` | Portada |
| Principal Cliente | `/cliente/mascotas/:idUsuario` | Portada |
| Detalle mascota Cliente | `/cliente/mascotas/:idUsuario/detalle/:id` | Portada |
| Página de Error (Thymeleaf) | `/error` y ruta comodín `**` | Portada |

El encabezado del portal enlaza `Pacientes` (`/mascotas`), `Clientes` (`/clientes`) y `Tratamientos` (`/tratamiento/asignar`); `Logout` vuelve al landing. El listado de mascotas tiene tres acciones en cada fila (`Ver ficha`, `Editar` y `Desactivar/Activar`), con la ficha clínica como única pantalla de detalle; el de clientes tiene `Editar`, `Desactivar/Activar` y `Eliminar`, y ese último borra en cascada las mascotas del dueño.

## Estructura

```
src/app/
├── components/
│   ├── navbar-landing/          Encabezado del landing (reacciona al scroll)
│   ├── footer-landing/          Pie del landing
│   └── footer-portal/           Pie del portal
├── models/                      Interfaces de las entidades del diagrama de clases
│   ├── administrador.model.ts
│   ├── droga.model.ts
│   ├── dueno.model.ts
│   ├── mascota.model.ts
│   ├── tratamiento.model.ts
│   └── veterinario.model.ts
├── pages/
│   ├── landing/                 Landing institucional (5 secciones + carrusel)
│   ├── login/                   Inicio de sesión (navega según el rol)
│   ├── clientes-listado/        Listado de clientes (dueños)
│   │   └── components/
│   │       ├── cliente-tabla/   Tabla (componente hijo)
│   │       └── page-title/      Título y subtítulo de la página
│   ├── clientes-formulario/     Formulario de registrar y editar cliente
│   ├── cliente-mascotas/        Portal del cliente: sus mascotas
│   ├── cliente-detalle/         Detalle de una mascota del cliente
│   ├── mascotas-listado/        Listado de mascotas a cargo
│   │   └── components/
│   │       ├── mascota-tabla/   Tabla (componente hijo)
│   │       └── page-title/      Título y subtítulo de la página
│   ├── mascotas-formulario/     Formulario de registrar y editar mascota
│   ├── mascotas-ficha/          Ficha clínica de una mascota
│   └── tratamiento-asignar/     Asignar tratamiento (formulario + modal)
│   └── error/                   Página de error (mensaje + volver al inicio/login)
├── service/                     Peticiones HTTP a la API REST
│   ├── login.service.ts         Autenticación (POST /login)
│   ├── dueno.service.ts         CRUD de clientes (GET/POST/PUT/DELETE)
│   ├── mascota.service.ts       CRUD de mascotas (GET/POST/PUT/DELETE)
│   ├── tratamiento.service.ts   Historial por mascota y asignación de tratamientos
│   ├── droga.service.ts         Inventario de medicamentos
│   └── veterinario.service.ts   Veterinarios
├── app.component.ts             Shell de la aplicación
├── app.config.ts                Configuración (provideRouter + HttpClientModule)
└── app.routes.ts                Rutas
```

Cada página monta su propio `navbar` + `main` + `footer` porque los dos encabezados del portal son distintos entre sí (ver *Decisiones*). El `footer` del portal sí se extrajo a `components/footer-portal` porque las dos pantallas del portal lo comparten textualmente.

## Modelos

Las interfaces copian los atributos y las asociaciones de las entidades JPA: las columnas declaradas como opcionales (`@Column` sin `nullable = false`) quedan como propiedades opcionales, las fechas `LocalDate` viajan como `string` y las relaciones con `Dueno`, `Tratamiento`, `Droga`, `Veterinario` y `Administrador` se declaran con `import type` para que las referencias circulares no lleguen al runtime.

## Servicios HTTP

Los servicios ya no siembran datos: cada método hace una petición a la API REST con `HttpClient`, usando `baseUrl = http://localhost:8080/<recurso>`. Siguiendo el patrón de la clase de REST, las **consultas devuelven el `Observable`** para que el componente reaccione a la respuesta, mientras que los **comandos que no actualizan la vista hacen el `subscribe` dentro del propio servicio**.

| Servicio | Método | Petición |
|---|---|---|
| `LoginService` | `autenticar(correo, contrasena)` | `POST /login`; devuelve `{rol, id}` |
| `DuenoService` | `listarDuenos()` | `GET /dueno/all` |
| `DuenoService` | `obtenerPorId(id)` | `GET /dueno/find/{id}` |
| `DuenoService` | `buscarPorNombre(nombre)` | `GET /dueno/search/{nombre}` |
| `DuenoService` | `guardarValidada(dueno)` | `POST /dueno/add` (sin id) o `PUT /dueno/update` (con id); comando |
| `DuenoService` | `alternarEstado(id)` | `PUT /dueno/toggle/{id}`; devuelve el estado |
| `DuenoService` | `eliminar(id)` | `DELETE /dueno/delete/{id}`; comando (borra en cascada) |
| `MascotaService` | `listarMascotas()` | `GET /mascota/all` |
| `MascotaService` | `obtenerMascotaPorId(id)` | `GET /mascota/find/{id}` |
| `MascotaService` | `buscarPorNombre(nombre)` | `GET /mascota/search/{nombre}` |
| `MascotaService` | `listarPorDueno(duenoId, nombre?)` | `GET /mascota/byDueno/{id}` (búsqueda con `?nombre=`) |
| `MascotaService` | `obtenerPropia(id, duenoId)` | `GET /mascota/propia/{id}/{duenoId}` (valida la pertenencia) |
| `MascotaService` | `listarMascotasActivas()` | `GET /mascota/activas` |
| `MascotaService` | `guardarValidada(mascota)` | `POST /mascota/add` (sin id) o `PUT /mascota/update` (con id); comando |
| `MascotaService` | `alternarEstado(id)` | `PUT /mascota/toggle/{id}`; devuelve el estado |
| `MascotaService` | `eliminar(id)` | `DELETE /mascota/delete/{id}`; comando |
| `TratamientoService` | `listarTratamientosPorMascota(id)` | `GET /tratamiento/byMascota/{id}` |
| `TratamientoService` | `asignar(tratamiento)` | `POST /tratamiento/add`; devuelve la droga actualizada |
| `DrogaService` | `listarDrogas()` | `GET /droga/all` |
| `VeterinarioService` | `listarVeterinarios()` | `GET /veterinario/all` |

En la creación se omite el `id` (`{ ...entidad, id: null }`) porque el backend trata un id nulo como registro nuevo; con `id` 0 lo tomaría como una edición (`merge`). Las reglas de negocio (cédula/correo únicos, dueño obligatorio, mascota activa y stock) viven ahora en el backend, así que el frontend ya no las replica.

La ficha clínica y el detalle del cliente encadenan dos consultas con **`mergeMap`** (patrón del video): primero la mascota (`obtenerMascotaPorId` / `obtenerPropia`) y, con esa respuesta, `listarTratamientosPorMascota`, en un solo `subscribe`, en lugar de anidar un `subscribe` dentro de otro.

## Decisiones

- **Tokens con prefijo `portal-`.** Los del landing quedan sin prefijo (tema oscuro) y los del portal con prefijo (tema claro) para que las dos paletas convivan en el mismo `tailwind.config.mjs` sin colisionar. El prototipo las separaba en dos hojas compiladas distintas.
- **Encabezado del portal dentro de cada página.** Los dos encabezados del portal **no son iguales** (el del listado lleva el logo enlazado, la campana, el avatar y el nombre del veterinario; el del formulario el logo sin campana ni avatar). Extraer un componente único obligaría a unificar y una de las dos pantallas dejaría de verse igual que el prototipo, así que se conservan por página. Solo el pie, idéntico en las pantallas del portal, se extrajo a `components/footer-portal`.
- **Los dos listados comparten el encabezado completo.** El listado de clientes usa el mismo encabezado completo que el de mascotas, a propósito, para que Pacientes y Clientes se vean iguales al navegar entre secciones, aunque en el prototipo el listado de clientes usa el encabezado simple. Es la única pantalla donde se prefirió la consistencia del portal a la fidelidad con el prototipo.
- **El aviso de error usa los tokens `portal-` del tema.** El prototipo lo pinta con `bg-red-900/40 text-red-300`, una paleta pensada para tema oscuro; sobre el fondo blanco del portal esa combinación da cerca de 1.2:1 de contraste y el mensaje queda ilegible. Aquí se usa `bg-portal-error-container` con `text-portal-on-error-container` y borde `portal-error` (7.2:1), que es la misma paleta de la pastilla de estado y del botón `Desactivar`.
- **Componentes hijos "tontos".** `mascota-tabla`, `cliente-tabla` y los `page-title` reciben lo que muestran por `input()` y avisan por `output()`; la búsqueda, el alternar estado y la eliminación en cascada quedan en la página, siguiendo el `student-table` del proyecto de referencia.
- **Formulario reactivo.** Se usó `ReactiveFormsModule` con `FormGroup`, como el `student-form` del ejemplo. El botón de guardar se deshabilita mientras el formulario es inválido y se muestran mensajes bajo los campos obligatorios (`nombre`, `especie` y `dueño` en mascota; `cedula`, `nombre` y `correo` en cliente).
- **Al editar se pierden los datos clínicos que no están en el formulario.** El formulario de mascota no incluye `fechaIngreso` ni `enfermedad`, y como el backend guarda la entidad completa (`save` con `merge`), esos campos quedan en nulo al editar. El `estado` sí se conserva porque el formulario lo lleva.
- **La ficha no tiene página de error propia.** La ficha encadena la mascota con su historial; si el id no existe la petición falla y `mascota` queda sin asignar, así que la plantilla muestra el panel *"Mascota no encontrada"* de la propia ficha. La **página de error global** (ver abajo) cubre las rutas desconocidas y los errores de backend.
- **El estado se cambia con un botón, no con un formulario.** El interruptor llama a `alternarEstado`, que hace `PUT /.../toggle/{id}` y devuelve el estado nuevo.
- **Las acciones de la tabla de clientes son botones, no enlaces.** La tabla emite un `output()` al componente padre en lugar de navegar. `Eliminar` pide confirmación con un `window.confirm` que anuncia cuántas mascotas se van, porque la cascada borra historial clínico y un clic equivocado no tiene vuelta atrás.
- **Los enlaces sin página se quedan en `#`.** `Tratamientos` en el encabezado y `Logout` que vuelve al landing; el enlace `Clientes` lleva al listado.
- **Fotografías.** El campo `imagen` guarda las mismas URLs del `DataLoader`. Son enlaces proxeados por `imgs.search.brave.com` que hoy responden **403**, así que las fotos se ven rotas en el listado. Es la única fuente de errores en la consola del navegador.
- **Login: el rol lo resuelve el backend.** `POST /login` busca en veterinario → administrador → dueño y devuelve `{rol, id}`; el componente navega (veterinario → `/mascotas`, cliente → `/cliente/mascotas/{id}`, administrador → `/`). Es la migración del login de Thymeleaf a REST, **sin sesión ni guard**: el tema de autenticación del curso queda para la última clase.
- **Manejo de errores: extensión fuera del video.** El video no maneja errores (asume que la consulta salió bien). Aquí sí: el `RestExceptionHandler` (`@RestControllerAdvice`) del backend traduce las excepciones de negocio a códigos HTTP con `{mensaje}` (**404** recurso inexistente, **400** dato o id inválido, **409** conflicto de estado, **500** genérico), y el `LoginRestController` conserva su **401** para credenciales incorrectas, que el banner del login muestra. Es el `GlobalExceptionHandler` de Thymeleaf llevado al formato API.
- **Página de error global.** `pages/error/` reproduce el `error.html` de Thymeleaf (mensaje amable + botones "Volver al inicio" e "Iniciar sesión") y se monta en la ruta `/error` y en la comodín `**`, de modo que una URL desconocida (antes en blanco) muestra esa página; el mensaje llega por `?mensaje=` cuando aplica.
- **El portal del cliente va por ruta, no por query.** El login navega a `/cliente/mascotas/:idUsuario` (y el detalle a `.../detalle/:id`), en lugar del `?idUsuario=` del backend Thymeleaf. La identidad viaja en la URL y el backend valida la pertenencia de la mascota (`/mascota/propia/{id}/{duenoId}`), de modo que un cliente no ve mascotas de otro cambiando el id.
- **Asignar tratamiento: `subscribe` en el componente.** A diferencia de los comandos "ciegos" (`guardarValidada`, `eliminar`), `asignar` devuelve la `Droga` con el stock ya descontado, así que el componente reacciona a esa respuesta para el modal "Tratamiento Aplicado" (misma regla del video: si hay que reaccionar, se hace desde la capa de arriba).

## Requisitos

- Node.js 20 o superior
- npm 10 o superior

## Cómo ejecutar

```bash
npm install
npm start
```

La aplicación queda disponible en `http://localhost:4200/`.

| Ruta | Descripción |
|---|---|
| `/` | Landing institucional |
| `/mascotas` | Listado de mascotas |
| `/mascotas/new` | Registrar mascota |
| `/mascotas/update/:id` | Editar mascota |
| `/mascotas/:id` | Ficha clínica de la mascota |
| `/clientes` | Listado de clientes |
| `/clientes/new` | Registrar cliente |
| `/clientes/update/:id` | Editar cliente |
| `/error` | Página de error (acepta `?mensaje=`) |
| `**` | Ruta comodín: cualquier URL desconocida → página de error |

El backend se levanta por separado (`mvn spring-boot:run` en la raíz del repositorio) en `http://localhost:8080/` y **debe estar corriendo** para que las pantallas carguen sus datos: los servicios apuntan a esa URL con `HttpClient`.

## Pruebas

El proyecto conserva la infraestructura de Karma y Jasmine (`target test` en `angular.json` y `tsconfig.spec.json`), con los schematics en `skipTests` para que los componentes y servicios se generen sin `.spec.ts`, igual que el proyecto de referencia.

```bash
npm test
```

Mientras no exista el primer archivo `.spec.ts`, el `include` de `tsconfig.spec.json` no encuentra entradas y TypeScript corta la corrida con `TS18003` (al proyecto de referencia le pasa lo mismo). Al agregar el primer spec el comando queda operativo; para una corrida única sin modo vigilancia:

```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

## Recorrido verificado

Con el backend levantado (`mvn spring-boot:run`) y el frontend en `npm start`, se comprobó en el navegador que las pantallas consumen la API (sin errores de consola más allá de los 403 de las fotos):

- **Listado de mascotas** (`/mascotas`): carga las **103 mascotas** del `DataLoader` con `GET /mascota/all`; la búsqueda llama a `GET /mascota/search/{nombre}`.
- **Listado de clientes** (`/clientes`): carga los **53 clientes** con `GET /dueno/all` y muestra el conteo de mascotas de cada uno.
- **Ficha clínica** (`/mascotas/1`): encadena `GET /mascota/find/1` con `GET /tratamiento/byMascota/1` mediante `mergeMap` y muestra el historial de tratamientos.
- **Alta de mascota**: guarda con `POST /mascota/add`; al volver al listado el contador pasa a **104** y aparece la mascota nueva.
- **Login** (`/login`): con `ana@correo.com` / `dueño123` llama a `POST /login` y navega a `/cliente/mascotas/1`; con `carlos.gutierrez@vetopia.com` / `vet123` va a `/mascotas`; con credenciales incorrectas el backend responde **401** y el banner muestra "Correo o contraseña incorrectos…".
- **Portal del cliente** (`/cliente/mascotas/1`): carga las mascotas de Ana con `GET /mascota/byDueno/1`; el detalle (`/cliente/mascotas/1/detalle/1`) encadena `GET /mascota/propia/1/1` con `GET /tratamiento/byMascota/1` mediante `mergeMap` y muestra el historial.
- **Asignar tratamiento** (`/tratamiento/asignar`): carga las mascotas activas y las drogas, y al aplicar (`POST /tratamiento/add`) aparece el modal "Tratamiento Aplicado" con el stock descontado (Amoxicilina pasó de 100 a 99 y Max sumó un tratamiento).
- **Página de error**: una URL desconocida (`/ruta-que-no-existe`) muestra la página de error con sus botones. Los códigos del backend se comprobaron directo (`GET /mascota/find/999999` → **404**, `/mascota/find/abc` → **400**, `/mascota/propia/1/2` → **409**, `POST /login` malo → **401**) y todos devuelven `{mensaje}`.

## Estilos

Los design tokens del proyecto están en `tailwind.config.mjs`: los de la landing sin prefijo (tema oscuro) y los del portal con prefijo `portal-` (tema claro), de modo que ambas paletas conviven sin colisionar. Las clases propias (header con blur, botones, animación de flotación) viven en `src/styles.scss`.

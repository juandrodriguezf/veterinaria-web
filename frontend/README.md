# Vetopia Frontend

Frontend del sistema **Vetopia – Hospital Veterinario del Futuro**, construido con **Angular 19** (componentes standalone) y **Tailwind CSS 3**.

Vive en la carpeta `frontend/` de este repositorio, junto al backend Spring Boot (Maven, JPA y H2). Por ahora **no consume el backend**: la capa de servicios siembra en memoria los mismos datos de prueba del `DataLoader` y es el único punto que habría que cambiar para hablar con la API real.

## Alcance

Este proyecto corresponde a la **Entrega 2** y solo reproduce las pantallas del prototipo que ya sirve el backend, para dejar el mismo aspecto y el mismo comportamiento.

| Pantalla del prototipo | Ruta en el backend | Ruta en Angular | Estado |
|---|---|---|---|
| Landing Page | `/` | `/` | Portada |
| Mascotas a Cargo | `/veterinario/mascotas` | `/mascotas` | Portada |
| Section 2: Pet Information (registrar mascota) | `/veterinario/mascotas/registrar-mascota` | `/mascotas/new` | Portada |
| Section 2: Pet Information (editar mascota) | `/veterinario/mascotas/editar-mascota` | `/mascotas/update/:id` | Portada |
| Log In | `/login` | — | No portada |
| Ficha Clínica | `/veterinario/mascotas/ficha` | `/mascotas/:id` | Portada |
| Clientes / Registrar Nuevo Cliente | `/veterinario/mascotas/clientes` | — | No portada |
| Asignar Nuevo Tratamiento / Tratamiento Aplicado | `/veterinario/mascotas/asignar-tratamiento` | — | No portada |
| Principal Cliente / Detalle mascota Cliente | `/cliente/mascotas` | — | No portada |

Los enlaces del encabezado que todavía no tienen página (`Clientes`, `Tratamientos`) llevan a `#` y `Logout` vuelve al landing, igual que en las plantillas del backend. El listado conserva además las tres acciones de cada fila (`Ver ficha`, `Editar` y `Desactivar/Activar`), con la ficha clínica como única pantalla de detalle.

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
│   ├── mascotas-listado/        Listado de mascotas a cargo
│   │   └── components/
│   │       ├── mascota-tabla/   Tabla (componente hijo)
│   │       └── page-title/      Título y subtítulo de la página
│   ├── mascotas-formulario/     Formulario de registrar y editar mascota
│   └── mascotas-ficha/          Ficha clínica de una mascota
├── service/
│   ├── dueno.service.ts         Dueños quemados
│   ├── mascota.service.ts       Mascotas quemadas
│   └── tratamiento.service.ts   Tratamientos, drogas y veterinarios quemados
├── app.component.ts             Shell de la aplicación
├── app.config.ts                Configuración (provideRouter)
└── app.routes.ts                Rutas
```

Cada página monta su propio `navbar` + `main` + `footer` porque los dos encabezados del portal son distintos entre sí (ver *Decisiones*). El `footer` del portal sí se extrajo a `components/footer-portal` porque las dos pantallas del portal lo comparten textualmente.

## Modelos

Las interfaces copian los atributos y las asociaciones de las entidades JPA: las columnas declaradas como opcionales (`@Column` sin `nullable = false`) quedan como propiedades opcionales, las fechas `LocalDate` viajan como `string` y las relaciones con `Dueno`, `Tratamiento`, `Droga`, `Veterinario` y `Administrador` se declaran con `import type` para que las referencias circulares no lleguen al runtime. No se modeló `ResultadoLogin` porque es una clase de apoyo del inicio de sesión y no una entidad de la base.

## Datos quemados

`DuenoService`, `MascotaService` y `TratamientoService` siembran un subconjunto de los datos del `DataLoader` (allá son 53 dueños, 103 mascotas y 5 tratamientos) suficiente para que el listado tenga la misma forma, la búsqueda tenga sentido y la ficha muestre historial:

- **8 dueños** (ids 1–8): Ana Rodríguez, Pedro Gómez y María López —los tres de las credenciales de demostración— más cinco clientes generados. María López queda **Inactiva**.
- **12 mascotas** (ids 1–12): Max, Luna y Rocky más nueve generadas, repartidas entre los ocho dueños. Dante y Kiara quedan **Inactivas**.
- **5 drogas**, **5 veterinarios** y **5 tratamientos**: los del `DataLoader`, sobre Max, Luna y Rocky (los únicos del subconjunto que los reciben), de modo que la ficha clínica tenga historial que mostrar y otras mascotas queden sin tratamientos.

| Servicio | Método | Comportamiento |
|---|---|---|
| `DuenoService` | `listarDuenos()` | Copia de los ocho dueños |
| `DuenoService` | `obtenerPorId(id)` | El dueño o `undefined` |
| `MascotaService` | `listarMascotas()` | Copia de las doce mascotas |
| `MascotaService` | `obtenerMascotaPorId(id)` | La mascota o `undefined` |
| `MascotaService` | `buscarPorNombre(nombre)` | Coincidencia **exacta** sin distinguir mayúsculas; vacío devuelve todo |
| `MascotaService` | `guardarValidada(mascota)` | Reglas de alta y edición (abajo) |
| `MascotaService` | `alternarEstado(id)` | Cambia entre `Activo` e `Inactivo` |
| `TratamientoService` | `listarTratamientos()` | Copia de los cinco tratamientos |
| `TratamientoService` | `listarTratamientosPorMascota(id)` | Los tratamientos de esa mascota |
| `TratamientoService` | `sembrarTratamientos(mascotas)` | Siembra los cinco del `DataLoader` y arma las dos caras |

`guardarValidada` aplica las mismas reglas que `MascotaServiceImpl`: si el id no existe da de alta la mascota con estado `Activo` y un id nuevo (`max + 1`, como la estrategia `IDENTITY` de JPA) y la agrega a la lista del dueño; si ya existe, actualiza la mascota encontrada. En los dos casos el servicio mantiene las **dos caras de la relación** `Dueno 1 — 0..* Mascota`, igual que haría JPA.

`MascotaService` inyecta `TratamientoService` (nunca al revés, para no crear un ciclo) y le pasa las mascotas ya sembradas: así el historial queda cargado de forma determinista y `mascota.tratamientos` sale ordenado por `fecha` ascendente, igual que el `@OrderBy("fecha ASC")` de la entidad.

## Decisiones

- **Tokens con prefijo `portal-`.** Los del landing quedan sin prefijo (tema oscuro) y los del portal con prefijo (tema claro) para que las dos paletas convivan en el mismo `tailwind.config.mjs` sin colisionar. El prototipo las separaba en dos hojas compiladas distintas.
- **Encabezado del portal dentro de cada página.** En el backend `mascotas-cargo.html` y `mascota-form.html` tienen encabezados que **no son iguales** (uno lleva el logo enlazado con la imagen de Google, la campana, el avatar y el nombre del veterinario; el otro el logo local sin campana ni avatar). Extraer un componente único obligaría a unificar y una de las dos pantallas dejaría de verse igual que el prototipo, así que se conservan por página. Solo el pie, idéntico en las tres pantallas del portal, se extrajo.
- **Componentes hijos "tontos".** `mascota-tabla` y `page-title` reciben lo que muestran por `input()` y avisan por `output()`; la búsqueda y el alternar estado quedan en la página, siguiendo el `student-table` del proyecto de referencia.
- **Formulario reactivo.** Se usó `ReactiveFormsModule` con `FormGroup`, como el `student-form` del ejemplo. El botón de guardar se deshabilita mientras el formulario es inválido y se muestran mensajes bajo `nombre`, `especie` y `dueño`; el backend no trae esos mensajes (solo el `required` del dueño en el HTML) y acepta cualquier otro campo vacío.
- **Al editar no se pierden datos clínicos.** El frontend conserva `fechaIngreso` y `enfermedad` de la mascota; el backend hoy los deja en nulo al guardar porque el merge del `@ModelAttribute` no los recibe del formulario.
- **La ficha no tiene página de error propia.** Con un id inexistente el backend lanza `RecursoNoEncontradoException` y cae en `error.html`; el port muestra el panel *"Mascota no encontrada"* de la propia ficha con el mismo texto de la causa. Los ids inválidos (`0`, negativos o no numéricos) sí se comportan igual en ambos: panel con el mensaje exacto del service (`El identificador "0" no es válido.` / `El identificador suministrado no es válido.`).
- **El estado se cambia con un botón, no con un `POST`.** El backend envía un formulario a `/veterinario/mascotas/ficha/estado`; aquí el interruptor llama a `alternarEstado` del servicio.
- **"Ver Perfil Completo" apunta a `#`** porque el listado de clientes no está portado, igual que los enlaces `Clientes` y `Tratamientos` del encabezado.
- **Fotografías.** El campo `imagen` guarda las mismas URLs del `DataLoader`. Son enlaces proxeados por `imgs.search.brave.com` que hoy responden **403**, así que las fotos se ven rotas en el listado (en el backend solo se ven si el navegador las tiene en caché). Es la única fuente de errores en la consola del navegador.

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

El backend se sigue levantando por separado (`mvn spring-boot:run` en la raíz del repositorio) en `http://localhost:8080/`, pero por ahora las dos aplicaciones no se comunican.

## Pruebas

El proyecto conserva la infraestructura de Karma y Jasmine (`target test` en `angular.json` y `tsconfig.spec.json`), con los schematics en `skipTests` para que los componentes y servicios se generen sin `.spec.ts`, igual que el proyecto de referencia.

```bash
npm test
```

Mientras no exista el primer archivo `.spec.ts`, el `include` de `tsconfig.spec.json` no encuentra entradas y TypeScript corta la corrida con `TS18003` (al proyecto de referencia le pasa lo mismo). Al agregar el primer spec el comando queda operativo; para una corrida única sin modo vigilancia:

```bash
npm test -- --watch=false --browsers=ChromeHeadless
```

## Estilos

Los design tokens del proyecto están en `tailwind.config.mjs`: los de la landing sin prefijo (tema oscuro) y los del portal con prefijo `portal-` (tema claro), de modo que ambas paletas conviven sin colisionar. Las clases propias (header con blur, botones, animación de flotación) viven en `src/styles.scss`.

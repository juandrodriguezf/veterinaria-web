# Vetopia Frontend

Frontend del sistema **Vetopia – Hospital Veterinario del Futuro**, construido con **Angular 19** (componentes standalone) y **Tailwind CSS 3**.

El backend Spring Boot (Maven, JPA y H2) vive en la raíz de este repositorio; este proyecto es la aplicación cliente y por ahora trabaja con **datos quemados en los servicios**, sin llamadas al backend.

## Estructura

```
src/app/
├── components/        Componentes compartidos (navbar, footer)
├── models/            Interfaces de las entidades (Mascota, Dueno, ...)
├── pages/             Vistas por ruta (landing, mascotas)
├── service/           Servicios con los datos quemados
├── app.component.ts   Shell de la aplicación
├── app.config.ts      Configuración (provideRouter)
└── app.routes.ts      Rutas
```

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
| `/mascotas/nueva` | Registrar mascota |
| `/mascotas/editar/:id` | Editar mascota |

## Estilos

Los design tokens del proyecto están en `tailwind.config.mjs`: los de la landing sin prefijo (tema oscuro) y los del portal con prefijo `portal-` (tema claro), de modo que ambas paletas conviven sin colisionar. Las clases propias (header con blur, botones, animación de flotación) viven en `src/styles.scss`.

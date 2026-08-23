# Vetopia

**Vetopia – Hospital Veterinario del Futuro.** Página web institucional de un hospital veterinario de diagnóstico avanzado, atención personalizada y tecnología de vanguardia.

**Sitio en vivo (GitHub Pages):** https://david-beltrang.github.io/Vetopia/

## Descripción

Landing page estática con diseño de alto impacto visual, construida con HTML, CSS, JavaScript y estilizada con **Tailwind CSS**.

## Características

- **Diseño responsive** adaptable a los tamaños de los dispositivos.
- **Tema oscuro** con paleta de colores Material Design (M3) personalizada.
- **Header tipo "glass"**: transparente al inicio y con efecto de vidrio (blur) al hacer scroll (manejado por `js/main.js`).
- **Tipografías web** Google Fonts: Bricolage Grotesque, Hanken Grotesk, JetBrains Mono y Material Symbols Outlined.
- **Secciones**: Hero, Servicios (Hospitalización, Tratamientos Personalizados, Monitoreo en Tiempo Real), Timeline del proceso en 4 pasos y Footer con enlaces legales.
- **Efectos hover** en tarjetas y botones definidos en `css/styles.css`.

## Estructura del proyecto

```
Vetopia/
└── app/
    ├── index.html        # Página principal
    ├── css/
    │   └── styles.css    # Estilos personalizados (header glass, hovers, botones)
    ├── js/
    │   └── main.js       # Efecto de scroll del header
    └── images/           # Logo y recursos visuales
```

## Cómo ejecutar localmente

Abre `app/index.html` en el navegador o usa cualquier servidor estático (por ejemplo, la extensión **Live Server** de VS Code sobre la carpeta `app/`).

## Tecnologías

- HTML5
- CSS3
- JavaScript
- Tailwind CSS
- Google Fonts

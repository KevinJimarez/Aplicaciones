# Subproducto 08 - Aplicación web maquetada

**EcoRuta Puebla** es una maqueta web responsiva para descubrir rutas urbanas sostenibles, espacios verdes y proyectos comunitarios. Esta entrega funciona como base visual de una aplicación que después puede evolucionar a PWA.

## Requisitos cubiertos

- Barra de navegación adaptable a escritorio y móvil.
- Tema libre e innovador relacionado con movilidad y sostenibilidad.
- Dos páginas enlazadas: `index.html` y `explorar.html`.
- Imágenes vectoriales, textos y video local.
- Diseño responsivo para móvil, tableta y escritorio.
- Carpetas separadas para estilos, scripts, imágenes y video.
- Interacciones en JavaScript: menú móvil, filtros, modal y recomendador.
- Manifiesto web enlazado desde ambas páginas con nombre, ruta inicial, alcance, modo de visualización, colores e iconos.

## Estructura

```text
08-aplicacion-web-maquetada-ecoruta/
├── assets/
│   ├── images/
│   ├── icons/
│   └── video/
├── css/
│   └── styles.css
├── js/
│   └── app.js
├── explorar.html
├── index.html
├── manifest.json
└── README.md
```

## Ejecución

No requiere instalar dependencias. Se puede abrir con **Live Server** en VS Code o copiar dentro de `htdocs` y visitar la ruta desde XAMPP.

La forma más directa es abrir la carpeta en VS Code, hacer clic derecho sobre `index.html` y seleccionar **Open with Live Server**.

## Fase actual de evolución a PWA

Esta entrega incorpora `manifest.json` e iconos de 192 y 512 píxeles. El manifiesto define la identidad de EcoRuta, la dirección de inicio, el alcance, el modo de visualización y los colores que puede utilizar el navegador.

El manifiesto es una fase preparatoria y no convierte por sí solo el sitio en una PWA completa. La instalación y el funcionamiento sin conexión se documentarán cuando se agreguen el service worker y la estrategia de caché.

El video demostrativo se basa en el recurso de ejemplo utilizado por MDN en la documentación del elemento HTML `video`.

Autor: Kevin Salas Jimarez - Matrícula 2311080876.

# Subproducto 08 - Aplicación web maquetada

**EcoRuta Puebla** es una aplicación web progresiva responsiva para descubrir rutas urbanas sostenibles, espacios verdes y proyectos comunitarios. Puede instalarse desde un navegador compatible y conserva su interfaz principal sin conexión después de la primera visita.

## Requisitos cubiertos

- Barra de navegación adaptable a escritorio y móvil.
- Tema libre e innovador relacionado con movilidad y sostenibilidad.
- Dos páginas enlazadas: `index.html` y `explorar.html`.
- Imágenes vectoriales, textos y video local.
- Diseño responsivo para móvil, tableta y escritorio.
- Carpetas separadas para estilos, scripts, imágenes y video.
- Interacciones en JavaScript: menú móvil, filtros, modal y recomendador.
- Manifiesto web enlazado desde ambas páginas con nombre, ruta inicial, alcance, modo de visualización, colores e iconos.
- Service Worker con precaché, actualización de recursos y limpieza de versiones anteriores.
- Instalación desde Chrome en computadora y desde la opción Agregar a pantalla de inicio en iPhone.

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
├── service-worker.js
└── README.md
```

## Ejecución

No requiere instalar dependencias. Se puede abrir con **Live Server** en VS Code o copiar dentro de `htdocs` y visitar la ruta desde XAMPP.

La forma más directa es abrir la carpeta en VS Code, hacer clic derecho sobre `index.html` y seleccionar **Open with Live Server**.

## Implementación PWA

Esta entrega incorpora `manifest.json`, iconos de 192 y 512 píxeles y `service-worker.js`. El manifiesto define la identidad de EcoRuta, la dirección de inicio, el alcance, el modo de visualización y los colores que utiliza el navegador.

El Service Worker guarda el núcleo de la aplicación, actualiza recursos consultados y permite abrir las páginas principales sin conexión. La instalación requiere servir el proyecto mediante `localhost` o HTTPS; GitHub Pages proporciona el HTTPS necesario para probarlo desde computadora y móvil.

El video demostrativo se basa en el recurso de ejemplo utilizado por MDN en la documentación del elemento HTML `video`.

Autor: Kevin Salas Jimarez - Matrícula 2311080876.

# Actividad 05 — Aplicación multiplataforma web NEXORA

NEXORA es un gestor de proyectos desarrollado con React, TypeScript y Vite. Esta carpeta conserva una copia independiente del código utilizado en la [Actividad 02](../02-aplicacion-web-nexora/), junto con un reporte y nuevas evidencias centradas en su alcance multiplataforma.

## ¿En qué sentido es multiplataforma?

La misma aplicación se ejecuta en un navegador web moderno de equipos de escritorio y dispositivos móviles, sin crear versiones separadas del código para cada sistema operativo. Su interfaz se adapta a distintos tamaños de pantalla mediante CSS Grid, Flexbox y reglas `@media` a 1100, 800 y 600 píxeles. Las capturas incluidas documentan las vistas de escritorio y de tamaño móvil.

Esta clasificación se refiere a **multiplataforma web**. El proyecto no contiene una aplicación nativa de Android, iOS o escritorio y tampoco está configurado como PWA instalable. La compatibilidad con cada combinación de navegador y sistema operativo debe verificarse en el dispositivo correspondiente; las capturas no prueban por sí solas todas las plataformas.

## Funciones principales

- Panel de inicio con métricas de proyectos y tareas.
- Administración de proyectos y tablero Kanban de cuatro estados.
- Creación, edición, filtros y seguimiento de tareas y subtareas.
- Asistente NORA de análisis local, sin conexión a un proveedor externo en la configuración entregada.
- Persistencia mediante `localStorage` del navegador.

Los datos guardados en `localStorage` permanecen en ese navegador y origen. No se sincronizan automáticamente entre dispositivos porque esta versión no tiene servidor ni base de datos compartida.

## Tecnologías

React, React DOM, TypeScript, Vite, CSS responsive, Lucide React y Web Storage API.

## Ejecutar y compilar

Se requiere Node.js y npm. Desde esta carpeta:

```bash
npm ci
npm run dev
```

Abrir la URL local indicada por Vite. Para verificar la compilación:

```bash
npm run build
```

El repositorio contiene el código fuente; no aloja por sí mismo una versión pública en ejecución.

## Evidencias y reporte

- [Capturas de escritorio y móvil](./docs/imagenes/)
- [Reporte del Producto 5](./docs/Reporte_Aplicacion_Multiplataforma_NEXORA_Kevin_Salas_Jimarez.docx)

## Datos académicos

- **Alumno:** Kevin Salas Jimarez
- **Matrícula:** 2311080876
- **Profesora:** Mather Xóchitl Mendoza Píscil
- **Asignatura:** Aplicaciones Web Progresivas
- **Grupo:** 10 B - 2026

# Actividad 02 — NEXORA Gestor Inteligente de Proyectos

NEXORA es una aplicación web de gestión de proyectos pensada para equipos pequeños y presentaciones académicas. Permite organizar el trabajo en proyectos, visualizar métricas reales, administrar tareas en un tablero Kanban y consultar a NORA, un asistente local que analiza los datos sin necesitar servicios externos.

## Objetivo

Ofrecer una experiencia clara y profesional para planear, asignar y dar seguimiento a tareas, manteniendo una instalación sencilla y todos los datos en el navegador.

## Funcionalidades

- Dashboard con métricas calculadas desde las tareas reales, progreso por estado, próximos vencimientos y actividad reciente.
- Creación, edición, selección y eliminación de proyectos.
- Tablero Kanban con estados Pendiente, En proceso, En revisión y Completado.
- Creación, edición y eliminación de tareas, asignación, prioridad, fecha, progreso, etiquetas y subtareas.
- Movimiento de tareas entre columnas mediante controles seguros en cada tarjeta.
- Búsqueda por nombre y filtros por prioridad y responsable.
- Panel de detalles completo al seleccionar una tarjeta.
- NORA con respuestas locales basadas en tareas, fechas, estados, prioridades y carga de trabajo.
- Sugerencias de subtareas que se pueden incorporar directamente a una tarea.
- Preferencias de nombre, densidad de interfaz y animaciones.
- Restauración de datos de demostración y borrado de datos locales.
- Persistencia automática y protección ante datos corruptos en `localStorage`.
- Confirmaciones, mensajes de éxito/error, estados vacíos y `ErrorBoundary`.
- Interfaz responsive para escritorio, tableta y móvil.

## Tecnologías

- React
- TypeScript
- Vite
- CSS moderno
- Lucide React
- LocalStorage

No utiliza base de datos, servidor propio, autenticación, Firebase, Supabase ni Docker.

## Requisitos

- Node.js 20 o superior recomendado
- npm 10 o superior
- Navegador moderno con LocalStorage habilitado

## Instalación y ejecución

Desde el directorio del proyecto:

```bash
npm install
npm run dev
```

Vite mostrará la dirección local, normalmente `http://localhost:5173`.

Para comprobar y generar la versión de producción:

```bash
npm run build
```

Para previsualizar esa versión:

```bash
npm run preview
```

## Uso de la aplicación

1. En **Inicio**, revisa las estadísticas del proyecto activo.
2. En **Mis proyectos**, crea un proyecto o abre uno existente.
3. En **Tablero de tareas**, crea tareas, usa los filtros y muévelas entre estados.
4. Selecciona una tarjeta para editar todos sus detalles y subtareas.
5. En **Asistente NORA**, escribe una pregunta o utiliza una sugerencia rápida.
6. En **Configuración**, personaliza la interfaz o administra los datos locales.

## Inteligencia artificial local

NORA funciona mediante análisis determinista y reglas de priorización. Cada respuesta se calcula en el momento a partir del proyecto activo. Puede:

- Contar tareas por estado y prioridad.
- Detectar tareas atrasadas y próximas a vencer.
- Calcular el progreso promedio.
- Comparar la carga activa de los integrantes.
- Recomendar la siguiente tarea según prioridad, vencimiento y progreso.
- Generar resúmenes del proyecto.
- Proponer subtareas con plantillas adaptadas a palabras como “diseñar”, “desarrollar” o “presentación”.

El botón **Agregar todas a la tarea** guarda las subtareas sugeridas en el tablero. El modo local no realiza peticiones de red y está siempre disponible.

Preguntas de ejemplo:

- `¿Qué tareas tengo pendientes?`
- `¿Qué tareas tienen prioridad alta?`
- `¿Qué tareas están próximas a vencer?`
- `¿Cuál es el avance del proyecto?`
- `¿Quién tiene más tareas asignadas?`
- `¿Qué debería hacer primero?`
- `Genera un resumen del proyecto`
- `Divide esta tarea en subtareas`

## Integración opcional con API

El proyecto incluye `.env.example` con la variable reservada:

```env
VITE_AI_API_KEY=
```

La arquitectura separa la lógica de NORA en `src/services/nora.ts`, por lo que se puede añadir un adaptador remoto sin modificar las páginas ni el modelo de datos. Si no hay clave —la configuración normal y recomendada para la demostración— se usa automáticamente el motor local. No se incluye ninguna clave real.

> Nota de seguridad: las variables con prefijo `VITE_` se incorporan al cliente. En una implementación pública, la llamada a un proveedor de IA debería pasar por un backend para no exponer credenciales.

## Datos de demostración

Al abrir NEXORA por primera vez aparece el proyecto **Desarrollo de plataforma universitaria**, con seis tareas y estos integrantes:

- Kevin Jimarez
- Arely Basurto
- Carlos Ortiz
- Miguel Juárez

No hay credenciales ni inicio de sesión. Los datos se guardan bajo la clave `nexora_data_v1` de LocalStorage.

## Estructura

```text
src/
├── components/   Componentes reutilizables, formularios y layout
├── data/         Datos iniciales de demostración
├── hooks/        Estado global, CRUD y persistencia
├── pages/        Dashboard, proyectos, tablero, NORA y configuración
├── services/     Motor de análisis local de NORA
├── styles/       Sistema visual y responsive
├── types/        Tipos centrales de TypeScript
└── utils/        Fechas, progreso, identificadores y etiquetas
```

## Verificación realizada

- Compilación con TypeScript estricto.
- Build de producción con Vite.
- Revisión de imports y componentes.
- Validación de formularios y confirmaciones de borrado.
- Persistencia automática y recuperación ante JSON local inválido.
- Funcionamiento del asistente sin variable de entorno.
- Reglas responsive para escritorio, tableta y móvil.

## Reporte de la actividad

El reporte académico con portada UTP, índice, explicación técnica, ejemplo de funcionamiento y evidencias visuales está disponible en [`docs/Reporte_Aplicacion_Web_NEXORA_Kevin_Salas_Jimarez.docx`](./docs/Reporte_Aplicacion_Web_NEXORA_Kevin_Salas_Jimarez.docx).

## Posibles mejoras futuras

- Arrastrar y soltar tarjetas con soporte accesible.
- Exportar e importar proyectos en JSON.
- Sincronización multiusuario mediante un backend.
- Adaptador seguro de IA desde un servidor.
- Pruebas automatizadas de componentes y flujos de usuario.

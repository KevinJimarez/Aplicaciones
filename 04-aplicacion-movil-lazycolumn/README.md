# Producto 4 — Aplicación móvil con LazyColumn

Aplicación Android desarrollada en Kotlin con Jetpack Compose para demostrar listas verticales y horizontales, manejo de estado local y recursos gráficos.

## Funcionalidad

- Muestra diez botones dentro de una `LazyColumn`.
- Permite seleccionar un botón y cambia su color de azul a gris.
- Confirma la selección mediante un texto en pantalla.
- Presenta una galería horizontal de imágenes mediante `LazyRow`.
- Funciona de forma local, sin servidor ni conexión a Internet.

## Tecnologías

- Kotlin 2.2.10
- Jetpack Compose y Material 3
- Android Gradle Plugin 9.2.1
- SDK mínimo 35, objetivo 36 y compilación 36.1
- Java 11

## Abrir y ejecutar

1. Abrir esta carpeta como proyecto en Android Studio.
2. Esperar a que finalice la sincronización de Gradle.
3. Seleccionar un emulador o teléfono Android compatible.
4. Ejecutar la configuración `app`.

También puede verificarse la compilación desde una terminal con:

```bash
./gradlew assembleDebug
```

El APK de depuración se genera localmente en `app/build/outputs/apk/debug/app-debug.apk`. Los archivos de compilación no se almacenan en el repositorio.

## Evidencias y reporte

- [Capturas de la aplicación](./docs/imagenes/)
- [Reporte académico](./docs/Reporte_Aplicacion_Movil_LazyColumn_Kevin_Salas_Jimarez.docx)

## Datos académicos

- **Alumno:** Kevin Salas Jimarez
- **Matrícula:** 2311080876
- **Profesora:** Mather Xóchitl Mendoza Píscil
- **Asignatura:** Aplicaciones Web Progresivas
- **Grupo:** 10 B - 2026

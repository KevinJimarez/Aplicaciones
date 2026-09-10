# Actividad 01 — Aplicación nativa para Wear OS

Proyecto académico de una aplicación nativa para smartwatch Android. La aplicación muestra una carátula de reloj digital y un menú con herramientas de salud, actividad y utilidades.

## Por qué es una aplicación nativa

El proyecto se ejecuta directamente en Wear OS y utiliza recursos propios del dispositivo mediante el SDK de Android. Está desarrollado con Kotlin y Jetpack Compose para Wear OS; además, declara compatibilidad con relojes y acceso opcional a los sensores de frecuencia cardiaca y contador de pasos.

## Funciones principales

- Reloj digital con fecha y zona horaria de México.
- Menú táctil adaptado a pantallas circulares.
- Módulo NutriWatch para citas, perfil y contactos de salud.
- Lectura de frecuencia cardiaca y pasos cuando el dispositivo dispone de los sensores.
- Actividad física, calculadora, música y accesos a otras aplicaciones.
- Funcionamiento básico en emulador aunque no existan sensores físicos.

## Tecnologías

- Kotlin
- Android SDK
- Wear OS
- Jetpack Compose para Wear OS
- Material 3 para Wear OS
- Gradle con Kotlin DSL

## Cómo ejecutar

1. Abrir esta carpeta en Android Studio.
2. Esperar a que Gradle termine la sincronización.
3. Seleccionar un emulador Wear OS o un smartwatch Android compatible.
4. Ejecutar el módulo `app`.

El proyecto requiere Android 11 o posterior para Wear OS (`minSdk 30`).

## Reporte y evidencias

- [Reporte de la aplicación nativa](./docs/Reporte_Aplicacion_Nativa_Wear_OS_Kevin_Salas.docx)
- [Capturas de funcionamiento](./docs/imagenes/)

## Compilación verificada

La variante `debug` se compiló correctamente con la tarea `assembleDebug`.

## Autor

**Kevin Salas Jimarez**  
Matrícula: **2311080876**

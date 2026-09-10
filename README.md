# Aplicaciones Web Progresivas

Repositorio académico de **Kevin Salas Jimarez** (matrícula **2311080876**) para la materia Aplicaciones Web Progresivas, grupo 10 B - 2026.

## Actividades

1. **Aplicación nativa para Wear OS:** el proyecto Android se conserva en la raíz para mantener compatible la entrega anterior.
2. **Aplicación web NEXORA:** [código, instrucciones, evidencias y reporte](./02-aplicacion-web-nexora/).

Cada actividad se mantiene en una ubicación independiente dentro del mismo repositorio. Así, los productos pueden consultarse juntos sin mezclar sus dependencias ni utilizar ramas como carpetas de entrega.

## Actividad 1: aplicación nativa para Wear OS

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

1. Abrir este repositorio en Android Studio.
2. Esperar a que Gradle termine la sincronización.
3. Seleccionar un emulador Wear OS o un smartwatch Android compatible.
4. Ejecutar el módulo `app`.

El proyecto requiere Android 11 o posterior para Wear OS (`minSdk 30`).

## Compilación verificada

La variante `debug` se compiló correctamente con la tarea `assembleDebug`.

## Autor

**Kevin Salas Jimarez**  
Matrícula: **2311080876**

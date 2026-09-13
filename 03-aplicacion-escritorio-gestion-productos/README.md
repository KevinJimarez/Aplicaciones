# Actividad 03 — Aplicación de escritorio

Aplicación de escritorio para administrar trabajadores, exhibidores y productos. El proyecto original fue desarrollado en **C#**, **.NET 8** y **Windows Forms** con Visual Studio.

## Funciones principales

- Captura de los datos de un trabajador.
- Alta, consulta y eliminación de exhibidores.
- Alta, consulta y eliminación de productos por exhibidor.
- Asociación de una imagen y cantidad de piezas a cada producto.
- Organización de productos con una lista ligada y de exhibidores con una lista doblemente ligada.

## Proyectos incluidos

| Carpeta | Tipo | Responsabilidad |
| --- | --- | --- |
| `ClaseProducto` | Biblioteca de clases | Modelo de datos `Producto`. |
| `ClassExibidorProducts` | Biblioteca de clases | Trabajadores, exhibidores, nodos y listas ligadas. |
| `Producto2Guillermo` | Windows Forms | Formularios e interacción con el usuario. |

El código se conserva tal como fue entregado originalmente. Los archivos temporales de Visual Studio y las carpetas de compilación no forman parte del repositorio.

## Cómo abrir y ejecutar

La interfaz requiere **Windows 10 u 11**, **Visual Studio 2022**, la carga de trabajo *Desarrollo de escritorio de .NET* y el **SDK de .NET 8**.

1. Compilar `ClaseProducto/ClaseProducto.sln`.
2. Compilar `ClassExibidorProducts/ClassExibidorProducts.sln`.
3. Abrir `Producto2Guillermo/Producto2Guillermo.sln`.
4. Seleccionar `Producto2Guillermo` como proyecto de inicio y ejecutar.

La secuencia es importante porque el proyecto original referencia las bibliotecas compiladas mediante rutas a sus archivos DLL.

## Evidencias y reporte

- [Formulario principal](./docs/imagenes/formulario_principal.png)
- [Gestión de productos](./docs/imagenes/formulario_productos.png)
- [Solución abierta en Visual Studio](./docs/imagenes/visual_studio_solucion.jpeg)
- El reporte académico se encuentra en la carpeta [`docs`](./docs/).

## Datos académicos

- **Alumno:** Kevin Salas Jimarez
- **Matrícula:** 2311080876
- **Profesora:** Mather Xóchitl Mendoza Píscil
- **Asignatura:** Aplicaciones Web Progresivas
- **Grupo:** 10 B - 2026

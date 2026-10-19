# Guía breve para presentar la base de datos de EcoRuta

## 1. Qué se implementó

EcoRuta utiliza una base de datos PostgreSQL alojada en Supabase. La PWA publicada en GitHub Pages se comunica con Supabase mediante solicitudes HTTPS, por lo que los mismos registros se comparten entre computadora y teléfono.

El acceso se divide en dos niveles:

- Visitante: puede enviar el formulario para crear un registro.
- Administrador autenticado: puede consultar, actualizar y eliminar registros.

## 2. Recorrido de una operación

```text
usuarios.html -> usuarios.js -> database.js -> Data API de Supabase -> PostgreSQL
```

1. `usuarios.html` presenta los formularios y la tabla.
2. `usuarios.js` escucha botones y formularios, valida los datos y actualiza la interfaz.
3. `database.js` convierte cada acción en una solicitud HTTP.
4. Supabase valida la clave pública, la sesión y las políticas RLS.
5. La Data API realiza la operación equivalente en PostgreSQL.

## 3. Equivalencias CRUD

| Demostración en la PWA | Método HTTP | SQL equivalente |
|---|---|---|
| Enviar registro | `POST` | `INSERT` |
| Actualizar lista | `GET` | `SELECT` |
| Guardar cambios | `PATCH` | `UPDATE` |
| Confirmar eliminación | `DELETE` | `DELETE` |

## 4. Demostración sugerida

1. Abrir `usuarios.html` sin iniciar sesión y registrar un usuario ficticio.
2. Explicar que un visitante no puede ver la tabla completa.
3. Iniciar sesión con la cuenta administrativa creada en Supabase.
4. Presionar **Actualizar lista** para demostrar la consulta.
5. Presionar **Editar**, cambiar el interés y guardar.
6. Presionar **Eliminar** y confirmar únicamente sobre un registro de prueba.
7. Abrir Supabase > **Table Editor** > `suscriptores` para comprobar el resultado.
8. Abrir Supabase > **SQL Editor** y mostrar `consultas-demostracion.sql`.

## 5. Dónde ejecutar SQL

Las sentencias se ejecutan en el panel del proyecto de Supabase, dentro de **SQL Editor**. El archivo `consultas-demostracion.sql` incluye ejemplos de `INSERT`, `SELECT`, `UPDATE` y `DELETE`. Deben ejecutarse una por una y solamente con datos ficticios.

## 6. Seguridad aplicada

- La PWA contiene únicamente la clave pública `publishable`.
- La clave `secret` y la clave `service_role` no se incluyen en el repositorio.
- Row Level Security está activado en la tabla `suscriptores`.
- El rol público solo tiene permiso para insertar.
- Los roles autenticados pueden leer, insertar, actualizar y eliminar.
- El registro público de cuentas administrativas está desactivado en Supabase.
- La sesión administrativa se conserva únicamente durante la sesión del navegador.

La contraseña del administrador no se guarda en el código ni en este documento.

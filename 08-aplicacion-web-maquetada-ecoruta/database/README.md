# Conexión de la base de datos de EcoRuta

## Arquitectura

```text
usuarios.html
      |
      v
usuarios.js (interfaz y eventos)
      |
      v
database.js (operaciones CRUD)
      |
      +-- Formulario público: POST / INSERT
      |
      +-- Administrador autenticado: GET, PATCH y DELETE
                                      |
                                      v
                                PostgreSQL en Internet
```

GitHub Pages publica HTML, CSS y JavaScript, pero no ejecuta PHP, Java, Python ni MySQL. Por ese motivo la PWA no puede conectarse al MySQL local de Workbench cuando está publicada. Workbench es solamente una herramienta para administrar un servidor MySQL; no convierte la computadora en un backend público.

Supabase proporciona una base PostgreSQL accesible mediante HTTPS. La PWA envía solicitudes a la API REST y Supabase transforma esas solicitudes en operaciones SQL.

## CRUD

| Acción | Solicitud HTTP | Operación SQL equivalente | Acceso |
|---|---|---|---|
| Crear usuario | `POST /rest/v1/suscriptores` | `INSERT` | Público |
| Consultar usuarios | `GET /rest/v1/suscriptores` | `SELECT` | Administrador |
| Actualizar usuario | `PATCH /rest/v1/suscriptores?id=eq.ID` | `UPDATE` | Administrador |
| Eliminar usuario | `DELETE /rest/v1/suscriptores?id=eq.ID` | `DELETE` | Administrador |

## Activación de Supabase

1. Crear el proyecto gratuito **EcoRuta PWA** en Supabase.
2. Abrir **SQL Editor** y ejecutar `supabase.sql`.
3. Crear manualmente el usuario administrador en **Authentication > Users**.
4. Desactivar nuevos registros públicos de cuentas administrativas.
5. Copiar la URL y la clave `publishable` en `../js/supabase-config.js`.
6. Incrementar la versión de caché en `../service-worker.js`.
7. Publicar los cambios en GitHub Pages.

Ejemplo de configuración:

```js
export const SUPABASE_CONFIG = {
  url: "https://proyecto.supabase.co",
  publishableKey: "sb_publishable_clave-publica"
};
```

La clave `publishable` puede estar en el frontend porque sus permisos están limitados mediante Row Level Security. La clave `secret` y la clave heredada `service_role` son privadas y nunca deben copiarse al proyecto ni al repositorio.

Las políticas RLS permiten `INSERT` al visitante, pero reservan `SELECT`, `UPDATE` y `DELETE` para una sesión autenticada. El archivo `consultas-demostracion.sql` contiene las cuatro sentencias SQL equivalentes para explicarlas en clase.

La explicación paso a paso para una exposición se encuentra en `GUIA_PRESENTACION.md`.

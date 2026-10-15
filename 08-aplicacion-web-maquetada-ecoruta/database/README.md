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
      +-- Sin configuración: IndexedDB del navegador
      |
      +-- Con configuración: API REST de Supabase
                               |
                               v
                         PostgreSQL en Internet
```

GitHub Pages publica HTML, CSS y JavaScript, pero no ejecuta PHP, Java, Python ni MySQL. Por ese motivo la PWA no puede conectarse al MySQL local de Workbench cuando está publicada. Workbench es solamente una herramienta para administrar un servidor MySQL; no convierte la computadora en un backend público.

Supabase proporciona una base PostgreSQL accesible mediante HTTPS. La PWA envía solicitudes a la API REST y Supabase transforma esas solicitudes en operaciones SQL.

## CRUD

| Acción | Solicitud HTTP | Operación SQL equivalente |
|---|---|---|
| Crear usuario | `POST /rest/v1/suscriptores` | `INSERT` |
| Consultar usuarios | `GET /rest/v1/suscriptores` | `SELECT` |
| Actualizar usuario | `PATCH /rest/v1/suscriptores?id=eq.ID` | `UPDATE` |
| Eliminar usuario | `DELETE /rest/v1/suscriptores?id=eq.ID` | `DELETE` |

## Activación de Supabase

1. Crear un proyecto gratuito en Supabase.
2. Abrir **SQL Editor**.
3. Ejecutar el contenido de `supabase.sql`.
4. Abrir **Project Settings > API**.
5. Copiar la URL del proyecto.
6. Copiar la clave pública `anon`.
7. Colocar ambos valores en `../js/supabase-config.js`.
8. Incrementar la versión de `CACHE_NAME` en `../service-worker.js`.
9. Publicar los cambios.

Ejemplo de configuración:

```js
export const SUPABASE_CONFIG = {
  url: "https://proyecto.supabase.co",
  anonKey: "clave-publica-anon"
};
```

La clave `anon` puede estar en el frontend porque sus permisos están limitados mediante Row Level Security. La clave `service_role` es privada y nunca debe copiarse al proyecto ni al repositorio.

Las políticas SQL incluidas permiten que cualquier visitante realice el CRUD para fines de demostración. Solo deben utilizarse datos ficticios. En una aplicación real se requeriría autenticación y políticas asociadas a cada usuario.

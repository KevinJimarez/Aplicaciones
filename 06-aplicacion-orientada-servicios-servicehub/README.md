# Subproducto 06 - Aplicación Orientada a Servicios

**ServiceHub SaaS** es un proyecto académico pequeño que demuestra una arquitectura orientada a servicios. La interfaz web no contiene directamente los datos del negocio: los solicita mediante endpoints REST y recibe respuestas JSON.

## Servicios disponibles

| Método | Endpoint | Función |
| --- | --- | --- |
| GET | `/api/health` | Comprueba el estado de la API |
| GET | `/api/summary` | Devuelve indicadores del panel |
| GET | `/api/plans` | Lista los planes SaaS |
| GET | `/api/customers` | Lista los clientes |
| GET | `/api/subscriptions` | Lista las suscripciones |

## Ejecución

Requiere Python 3, sin instalar librerías externas.

```bash
python3 server.py
```

Después se abre `http://127.0.0.1:8060` en el navegador.

Para observar una respuesta REST de manera didáctica se puede abrir
`http://127.0.0.1:8060/api-view.html`.

## Tecnologías

- Python y `http.server` para la API y el servidor web.
- HTML, CSS y JavaScript para la interfaz.
- HTTP y JSON para la comunicación entre la interfaz y los servicios.

## Entrega

- El reporte académico en Word se encuentra en [`docs`](./docs/).
- Las capturas del panel y del servicio JSON se encuentran en [`docs/imagenes`](./docs/imagenes/).

Autor: Kevin Salas Jimarez - Matrícula 2311080876.

// Identificador común: permite reconocer únicamente las cachés creadas por EcoRuta.
const CACHE_PREFIX = "ecoruta";

// La versión cambia cuando se modifica el contenido que debe quedar disponible sin conexión.
const CACHE_VERSION = "v7";

// La caché del shell conserva los archivos indispensables para abrir la PWA.
const SHELL_CACHE = `${CACHE_PREFIX}-shell-${CACHE_VERSION}`;

// La caché de ejecución guarda recursos descubiertos mientras la persona navega.
const RUNTIME_CACHE = `${CACHE_PREFIX}-runtime-${CACHE_VERSION}`;

// Límite preventivo para evitar que la caché dinámica crezca indefinidamente.
const MAX_RUNTIME_ENTRIES = 40;

// Recursos mínimos que permiten abrir y recorrer EcoRuta sin conexión.
const APP_SHELL = [
  "./",
  "./index.html",
  "./explorar.html",
  "./usuarios.html",
  "./css/styles.css",
  "./js/app.js",
  "./js/database.js",
  "./js/supabase-config.js",
  "./js/usuarios.js",
  "./manifest.json",
  "./assets/icons/icon-192.png",
  "./assets/icons/icon-512.png",
  "./assets/images/hero-ecoruta.svg",
  "./assets/images/ruta-bici.svg",
  "./assets/images/ruta-comunidad.svg",
  "./assets/images/ruta-jardin.svg",
  "./assets/images/ruta-rio.svg",
  "./assets/images/video-poster.svg"
];

// Devuelve true solo para respuestas completas, correctas y del mismo origen.
// Esta validación evita el error de Cache.put() con respuestas parciales 206.
function isCacheableResponse(response) {
  return Boolean(response && response.status === 200 && response.type === "basic");
}

// Abre una caché concreta y guarda una copia de la respuesta bajo la solicitud original.
async function putInCache(cacheName, request, response) {
  if (!isCacheableResponse(response)) {
    return;
  }

  const cache = await caches.open(cacheName);
  await cache.put(request, response.clone());
}

// Consulta las entradas de una caché y elimina las más antiguas cuando supera el límite.
async function trimCache(cacheName, maxEntries) {
  const cache = await caches.open(cacheName);
  const requests = await cache.keys();
  const excess = requests.length - maxEntries;

  if (excess <= 0) {
    return;
  }

  await Promise.all(requests.slice(0, excess).map((request) => cache.delete(request)));
}

// Estrategia Network First: prioriza información actual y usa caché cuando falla la red.
async function networkFirst(request) {
  try {
    const response = await fetch(request);

    if (isCacheableResponse(response)) {
      await putInCache(RUNTIME_CACHE, request, response);
    }

    return response;
  } catch (error) {
    const cachedResponse = await caches.match(request);

    // Si la navegación exacta no existe, se muestra la pantalla guardada para contingencia.
    return cachedResponse || caches.match("./offline.html");
  }
}

// Estrategia Cache First: entrega primero los recursos locales y consulta la red si faltan.
async function cacheFirst(request) {
  // Cache.match() busca primero dentro de la caché dinámica específica.
  const runtimeCache = await caches.open(RUNTIME_CACHE);
  const runtimeResponse = await runtimeCache.match(request);

  // Si no existe, CacheStorage.match() amplía la búsqueda a todas las cachés del origen.
  const cachedResponse = runtimeResponse || (await caches.match(request));

  if (cachedResponse) {
    return cachedResponse;
  }

  const response = await fetch(request);

  if (isCacheableResponse(response)) {
    await putInCache(RUNTIME_CACHE, request, response);
    await trimCache(RUNTIME_CACHE, MAX_RUNTIME_ENTRIES);
  }

  return response;
}

// install crea la caché del shell y añade los recursos necesarios para trabajar sin conexión.
self.addEventListener("install", (event) => {
  event.waitUntil(
    caches
      .open(SHELL_CACHE)
      .then(async (cache) => {
        // Cache.add() incorpora un recurso individual: la pantalla de contingencia offline.
        await cache.add("./offline.html");

        // Cache.addAll() incorpora el conjunto completo del shell de la aplicación.
        await cache.addAll(APP_SHELL);
      })
      .then(() => self.skipWaiting())
  );
});

// activate obtiene los nombres disponibles y borra las versiones antiguas de EcoRuta.
self.addEventListener("activate", (event) => {
  const currentCaches = [SHELL_CACHE, RUNTIME_CACHE];

  event.waitUntil(
    caches
      .keys()
      .then((names) =>
        Promise.all(
          names
            .filter((name) => name.startsWith(CACHE_PREFIX) && !currentCaches.includes(name))
            .map((name) => caches.delete(name))
        )
      )
      .then(() => self.clients.claim())
  );
});

// fetch intercepta únicamente solicitudes GET que pertenecen al origen de la PWA.
self.addEventListener("fetch", (event) => {
  const request = event.request;
  const requestURL = new URL(request.url);

  if (request.method !== "GET" || requestURL.origin !== self.location.origin) {
    return;
  }

  // El navegador administra directamente las solicitudes por rangos, comunes en video.
  // No guardarlas impide el error "Partial response (status code 206) is unsupported".
  if (request.headers.has("range")) {
    return;
  }

  // Los documentos HTML usan Network First para mostrar contenido actualizado.
  if (request.mode === "navigate") {
    event.respondWith(networkFirst(request));
    return;
  }

  // CSS, JavaScript, imágenes y fuentes usan Cache First para cargar con rapidez y sin red.
  if (["style", "script", "image", "font"].includes(request.destination)) {
    event.respondWith(cacheFirst(request));
    return;
  }

  // Los demás recursos del mismo origen también se consultan mediante Cache First.
  event.respondWith(cacheFirst(request));
});

// Permite comprobar o limpiar la caché dinámica desde DevTools sin borrar el shell offline.
self.addEventListener("message", (event) => {
  if (event.data?.type === "GET_CACHE_STATUS") {
    event.waitUntil(
      Promise.all([caches.has(SHELL_CACHE), caches.has(RUNTIME_CACHE), caches.keys()]).then(
        ([shellReady, runtimeReady, cacheNames]) => {
          event.source?.postMessage({ shellReady, runtimeReady, cacheNames });
        }
      )
    );
  }

  if (event.data?.type === "CLEAR_RUNTIME_CACHE") {
    event.waitUntil(caches.delete(RUNTIME_CACHE));
  }
});

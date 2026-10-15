// Nombre versionado de la caché. La versión 2 incorpora el CRUD de usuarios.
const CACHE_NAME = "ecoruta-shell-v2";

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

// El evento install crea la caché y guarda el núcleo de la aplicación.
self.addEventListener("install", (event) => {
  // waitUntil mantiene la instalación activa hasta terminar el precaché.
  event.waitUntil(
    caches
      .open(CACHE_NAME)
      .then((cache) => cache.addAll(APP_SHELL))
      .then(() => self.skipWaiting())
  );
});

// El evento activate elimina cachés antiguas y toma control de las páginas abiertas.
self.addEventListener("activate", (event) => {
  // Se conservan únicamente los recursos de la versión declarada en CACHE_NAME.
  event.waitUntil(
    caches
      .keys()
      .then((names) => Promise.all(names.filter((name) => name !== CACHE_NAME).map((name) => caches.delete(name))))
      .then(() => self.clients.claim())
  );
});

// El evento fetch intercepta las solicitudes GET realizadas por la aplicación.
self.addEventListener("fetch", (event) => {
  const request = event.request;

  // No se alteran envíos de formularios ni recursos pertenecientes a otros dominios.
  if (request.method !== "GET" || new URL(request.url).origin !== self.location.origin) {
    return;
  }

  // Para páginas HTML se intenta primero la red y se usa la caché si no hay conexión.
  if (request.mode === "navigate") {
    event.respondWith(
      fetch(request)
        .then((response) => {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
          return response;
        })
        .catch(() => caches.match(request).then((cached) => cached || caches.match("./index.html")))
    );
    return;
  }

  // Para estilos, scripts, iconos e imágenes se responde primero desde la caché.
  event.respondWith(
    caches.match(request).then((cached) => {
      if (cached) {
        return cached;
      }

      return fetch(request).then((response) => {
        // Solo se almacenan respuestas válidas para no guardar errores de red.
        if (response.ok) {
          const copy = response.clone();
          caches.open(CACHE_NAME).then((cache) => cache.put(request, copy));
        }
        return response;
      });
    })
  );
});

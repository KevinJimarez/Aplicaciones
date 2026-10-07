const menuButton = document.querySelector(".menu-button");
const mainNav = document.querySelector(".main-nav");

menuButton?.addEventListener("click", () => {
  const isOpen = mainNav.classList.toggle("open");
  menuButton.setAttribute("aria-expanded", String(isOpen));
});

mainNav?.querySelectorAll("a").forEach((link) => {
  link.addEventListener("click", () => {
    mainNav.classList.remove("open");
    menuButton?.setAttribute("aria-expanded", "false");
  });
});

const header = document.querySelector(".site-header");
const updateHeader = () => header?.classList.toggle("scrolled", window.scrollY > 12);
window.addEventListener("scroll", updateHeader, { passive: true });
updateHeader();

const observer = new IntersectionObserver(
  (entries) => entries.forEach((entry) => entry.isIntersecting && entry.target.classList.add("visible")),
  { threshold: 0.12 }
);
document.querySelectorAll(".reveal").forEach((element) => observer.observe(element));

document.querySelectorAll("[data-year]").forEach((element) => {
  element.textContent = new Date().getFullYear();
});

const filterButtons = document.querySelectorAll("[data-filter]");
const routeCards = document.querySelectorAll("[data-category]");

filterButtons.forEach((button) => {
  button.addEventListener("click", () => {
    const filter = button.dataset.filter;
    filterButtons.forEach((item) => item.classList.toggle("active", item === button));
    routeCards.forEach((card) => {
      card.hidden = filter !== "todas" && card.dataset.category !== filter;
    });
  });
});

const dialog = document.querySelector("[data-dialog]");
const dialogTitle = document.querySelector("[data-dialog-title]");

document.querySelectorAll("[data-route]").forEach((button) => {
  button.addEventListener("click", () => {
    dialogTitle.textContent = button.dataset.route;
    dialog?.showModal();
  });
});

document.querySelector(".dialog-close")?.addEventListener("click", () => dialog?.close());
dialog?.addEventListener("click", (event) => {
  if (event.target === dialog) dialog.close();
});

const recommendations = {
  moverme: "Circuito Aire Limpio es ideal: 4.2 km en bicicleta y dificultad fácil.",
  respirar: "Jardines que alimentan combina naturaleza, calma y aprendizaje comunitario.",
  conocer: "Origen local te conecta con productores, talleres y sabores de la región."
};

document.querySelector("[data-planner]")?.addEventListener("submit", (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  form.querySelector(".planner-result").textContent = recommendations[form.elements.mood.value];
});

// Registra el Service Worker únicamente cuando el navegador ofrece esta API.
if ("serviceWorker" in navigator) {
  window.addEventListener("load", async () => {
    try {
      await navigator.serviceWorker.register("./service-worker.js", { scope: "./" });
      console.info("EcoRuta: Service Worker registrado correctamente.");
    } catch (error) {
      console.error("EcoRuta: no fue posible registrar el Service Worker.", error);
    }
  });
}

// Guarda temporalmente el evento de instalación que Chrome entrega a la página.
let deferredInstallPrompt = null;
const installButtons = document.querySelectorAll("[data-install]");
const isIos = /iphone|ipad|ipod/i.test(navigator.userAgent);
const isStandalone = window.matchMedia("(display-mode: standalone)").matches || window.navigator.standalone === true;

// Muestra el botón propio cuando Chrome confirma que la PWA cumple los requisitos.
window.addEventListener("beforeinstallprompt", (event) => {
  event.preventDefault();
  deferredInstallPrompt = event;
  installButtons.forEach((button) => { button.hidden = false; });
});

// En iPhone no existe beforeinstallprompt; se ofrece la guía del menú Compartir.
if (isIos && !isStandalone) {
  installButtons.forEach((button) => { button.hidden = false; });
}

installButtons.forEach((button) => {
  button.addEventListener("click", async () => {
    if (deferredInstallPrompt) {
      deferredInstallPrompt.prompt();
      await deferredInstallPrompt.userChoice;
      deferredInstallPrompt = null;
      button.hidden = true;
      return;
    }

    if (isIos) {
      window.alert("Para instalar EcoRuta en iPhone: abre Compartir y elige Agregar a pantalla de inicio.");
    }
  });
});

// Oculta el control cuando la instalación termina satisfactoriamente.
window.addEventListener("appinstalled", () => {
  deferredInstallPrompt = null;
  installButtons.forEach((button) => { button.hidden = true; });
  console.info("EcoRuta: aplicación instalada.");
});

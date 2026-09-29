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

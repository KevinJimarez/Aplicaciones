const menuToggle = document.querySelector(".menu-toggle");
const navigation = document.querySelector(".main-nav");

menuToggle?.addEventListener("click", () => {
  const open = navigation.classList.toggle("open");
  menuToggle.setAttribute("aria-expanded", String(open));
});

navigation?.querySelectorAll("a").forEach((link) => link.addEventListener("click", () => {
  navigation.classList.remove("open");
  menuToggle?.setAttribute("aria-expanded", "false");
}));

const header = document.querySelector(".site-header");
const updateHeader = () => header?.classList.toggle("scrolled", window.scrollY > 10);
window.addEventListener("scroll", updateHeader, { passive: true });
updateHeader();

const observer = new IntersectionObserver(
  (entries) => entries.forEach((entry) => entry.isIntersecting && entry.target.classList.add("visible")),
  { threshold: 0.1 }
);
document.querySelectorAll(".reveal").forEach((element) => observer.observe(element));
document.querySelectorAll("[data-year]").forEach((element) => { element.textContent = new Date().getFullYear(); });

const filterButtons = document.querySelectorAll("[data-filter]");
const missionCards = document.querySelectorAll("[data-category]");
filterButtons.forEach((button) => button.addEventListener("click", () => {
  const filter = button.dataset.filter;
  filterButtons.forEach((item) => item.classList.toggle("active", item === button));
  missionCards.forEach((card) => { card.hidden = filter !== "todas" && card.dataset.category !== filter; });
}));

const dialog = document.querySelector("[data-dialog]");
const dialogTitle = document.querySelector("[data-dialog-title]");
document.querySelectorAll("[data-mission]").forEach((button) => button.addEventListener("click", () => {
  dialogTitle.textContent = button.dataset.mission;
  dialog?.showModal();
}));
document.querySelector(".dialog-close")?.addEventListener("click", () => dialog?.close());
dialog?.addEventListener("click", (event) => { if (event.target === dialog) dialog.close(); });

const recommendations = {
  vida: "Océano oculto: investiga Europa y las condiciones que podría reunir su océano.",
  geologia: "Crónicas de Marte: sigue las huellas del agua, los volcanes y los antiguos valles.",
  atmosfera: "Nubes de Venus: compara climas extremos y descubre el efecto invernadero."
};
document.querySelector("[data-selector]")?.addEventListener("submit", (event) => {
  event.preventDefault();
  const form = event.currentTarget;
  const duration = form.elements.time.value === "breve" ? " Versión breve disponible." : "";
  form.querySelector(".selector-result").textContent = recommendations[form.elements.interest.value] + duration;
});

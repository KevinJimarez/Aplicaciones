import { database } from "./database.js";

const form = document.querySelector("[data-subscriber-form]");
const recordsBody = document.querySelector("[data-records-body]");
const emptyState = document.querySelector("[data-empty-state]");
const recordCount = document.querySelector("[data-record-count]");
const formMessage = document.querySelector("[data-form-message]");
const formTitle = document.querySelector("[data-form-title]");
const submitButton = document.querySelector("[data-submit-button]");
const cancelButton = document.querySelector("[data-cancel-edit]");
const storageMode = document.querySelector("[data-storage-mode]");

let records = [];

storageMode.textContent =
  database.mode === "supabase" ? "Supabase · PostgreSQL en línea" : "IndexedDB · Base local de la PWA";

const interestLabels = {
  bicicleta: "Bicicleta",
  naturaleza: "Naturaleza",
  comunidad: "Comunidad"
};

const showMessage = (message, type = "success") => {
  formMessage.textContent = message;
  formMessage.dataset.type = type;
};

const resetForm = () => {
  form.reset();
  form.elements.id.value = "";
  formTitle.textContent = "Registrar usuario";
  submitButton.textContent = "Guardar usuario";
  cancelButton.hidden = true;
};

const makeActionButton = (label, action, id) => {
  const button = document.createElement("button");
  button.type = "button";
  button.className = `table-action ${action}`;
  button.textContent = label;
  button.dataset.action = action;
  button.dataset.id = id;
  return button;
};

const renderRecords = () => {
  recordsBody.replaceChildren();
  emptyState.hidden = records.length > 0;
  recordCount.textContent = `${records.length} ${records.length === 1 ? "registro" : "registros"}`;

  records.forEach((record) => {
    const row = document.createElement("tr");

    const userCell = document.createElement("td");
    const name = document.createElement("strong");
    const email = document.createElement("small");
    name.textContent = record.nombre;
    email.textContent = record.email;
    userCell.append(name, email);

    const interestCell = document.createElement("td");
    interestCell.textContent = interestLabels[record.interes] || record.interes;

    const updatesCell = document.createElement("td");
    const badge = document.createElement("span");
    badge.className = `subscription-badge ${record.recibir_actualizaciones ? "yes" : "no"}`;
    badge.textContent = record.recibir_actualizaciones ? "Sí" : "No";
    updatesCell.append(badge);

    const actionsCell = document.createElement("td");
    const actions = document.createElement("div");
    actions.className = "table-actions";
    actions.append(
      makeActionButton("Editar", "edit", record.id),
      makeActionButton("Eliminar", "delete", record.id)
    );
    actionsCell.append(actions);

    row.append(userCell, interestCell, updatesCell, actionsCell);
    recordsBody.append(row);
  });
};

const loadRecords = async () => {
  try {
    records = await database.list();
    renderRecords();
  } catch (error) {
    showMessage(`No fue posible consultar los usuarios: ${error.message}`, "error");
  }
};

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage("");

  if (!form.checkValidity()) {
    form.reportValidity();
    return;
  }

  const id = form.elements.id.value;
  const data = {
    nombre: form.elements.nombre.value.trim(),
    email: form.elements.email.value.trim().toLowerCase(),
    interes: form.elements.interes.value,
    recibir_actualizaciones: form.elements.recibir_actualizaciones.checked
  };

  submitButton.disabled = true;
  submitButton.textContent = id ? "Actualizando…" : "Guardando…";

  try {
    if (id) {
      await database.update(id, data);
      showMessage("Usuario actualizado correctamente.");
    } else {
      await database.create(data);
      showMessage("Usuario registrado correctamente.");
    }
    resetForm();
    await loadRecords();
  } catch (error) {
    const duplicate = /unique|constraint|correo|email/i.test(error.message);
    showMessage(duplicate ? "Ese correo ya se encuentra registrado." : `No fue posible guardar: ${error.message}`, "error");
  } finally {
    submitButton.disabled = false;
    if (!form.elements.id.value) submitButton.textContent = "Guardar usuario";
  }
});

recordsBody.addEventListener("click", async (event) => {
  const button = event.target.closest("[data-action]");
  if (!button) return;

  const record = records.find((item) => item.id === button.dataset.id);
  if (!record) return;

  if (button.dataset.action === "edit") {
    form.elements.id.value = record.id;
    form.elements.nombre.value = record.nombre;
    form.elements.email.value = record.email;
    form.elements.interes.value = record.interes;
    form.elements.recibir_actualizaciones.checked = record.recibir_actualizaciones;
    formTitle.textContent = "Actualizar usuario";
    submitButton.textContent = "Guardar cambios";
    cancelButton.hidden = false;
    form.scrollIntoView({ behavior: "smooth", block: "center" });
    form.elements.nombre.focus({ preventScroll: true });
    return;
  }

  if (button.dataset.action === "delete") {
    const confirmed = window.confirm(`¿Eliminar el registro de ${record.nombre}?`);
    if (!confirmed) return;

    try {
      await database.remove(record.id);
      showMessage("Usuario eliminado correctamente.");
      if (form.elements.id.value === record.id) resetForm();
      await loadRecords();
    } catch (error) {
      showMessage(`No fue posible eliminar: ${error.message}`, "error");
    }
  }
});

cancelButton.addEventListener("click", () => {
  resetForm();
  showMessage("Edición cancelada.");
});

loadRecords();

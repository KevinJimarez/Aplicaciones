import { database } from "./database.js?v=8";

const form = document.querySelector("[data-subscriber-form]");
const recordsBody = document.querySelector("[data-records-body]");
const emptyState = document.querySelector("[data-empty-state]");
const recordCount = document.querySelector("[data-record-count]");
const formMessage = document.querySelector("[data-form-message]");
const formTitle = document.querySelector("[data-form-title]");
const submitButton = document.querySelector("[data-submit-button]");
const cancelButton = document.querySelector("[data-cancel-edit]");
const storageMode = document.querySelector("[data-storage-mode]");
const refreshButton = document.querySelector("[data-refresh-records]");
const loginForm = document.querySelector("[data-admin-login]");
const loginMessage = document.querySelector("[data-login-message]");
const adminGate = document.querySelector("[data-admin-gate]");
const recordsPanel = document.querySelector("[data-records-panel]");
const adminIdentity = document.querySelector("[data-admin-identity]");
const logoutButton = document.querySelector("[data-admin-logout]");

let records = [];
let currentAdmin = null;

storageMode.textContent =
  database.mode === "supabase" ? "En línea" : "Temporalmente no disponible";

const interestLabels = {
  bicicleta: "Bicicleta",
  naturaleza: "Naturaleza",
  comunidad: "Comunidad"
};

const showMessage = (element, message, type = "success") => {
  element.textContent = message;
  element.dataset.type = type;
};

const resetForm = () => {
  form.reset();
  form.elements.id.value = "";
  formTitle.textContent = "Registrar interés";
  submitButton.textContent = "Enviar registro";
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
  if (!currentAdmin) return;

  recordsBody.setAttribute("aria-busy", "true");
  refreshButton.disabled = true;
  refreshButton.textContent = "Consultando…";

  try {
    records = await database.list();
    renderRecords();
  } catch (error) {
    showMessage(loginMessage, `No fue posible consultar: ${error.message}`, "error");
  } finally {
    recordsBody.removeAttribute("aria-busy");
    refreshButton.disabled = false;
    refreshButton.textContent = "Actualizar lista";
  }
};

const updateAdminView = async () => {
  currentAdmin = await database.currentUser();
  adminGate.hidden = Boolean(currentAdmin);
  recordsPanel.hidden = !currentAdmin;

  if (currentAdmin) {
    adminIdentity.textContent = currentAdmin.email;
    await loadRecords();
  } else {
    records = [];
    renderRecords();
  }
};

form.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(formMessage, "");

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
  submitButton.textContent = id ? "Actualizando…" : "Enviando…";

  try {
    if (id) {
      await database.update(id, data);
      showMessage(formMessage, "Usuario actualizado correctamente.");
    } else {
      await database.create(data);
      showMessage(formMessage, "Registro enviado correctamente a Supabase.");
    }
    resetForm();
    if (currentAdmin) await loadRecords();
  } catch (error) {
    const duplicate = /unique|constraint|correo|email/i.test(error.message);
    showMessage(
      formMessage,
      duplicate ? "Ese correo ya se encuentra registrado." : `No fue posible guardar: ${error.message}`,
      "error"
    );
  } finally {
    submitButton.disabled = false;
    if (!form.elements.id.value) submitButton.textContent = "Enviar registro";
  }
});

loginForm.addEventListener("submit", async (event) => {
  event.preventDefault();
  showMessage(loginMessage, "");
  const button = loginForm.querySelector("button[type='submit']");
  button.disabled = true;
  button.textContent = "Ingresando…";

  try {
    await database.signIn(
      loginForm.elements.admin_email.value.trim().toLowerCase(),
      loginForm.elements.admin_password.value
    );
    loginForm.reset();
    await updateAdminView();
  } catch (error) {
    showMessage(loginMessage, `Acceso rechazado: ${error.message}`, "error");
  } finally {
    button.disabled = false;
    button.textContent = "Entrar como administrador";
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
      showMessage(formMessage, "Usuario eliminado correctamente.");
      if (form.elements.id.value === record.id) resetForm();
      await loadRecords();
    } catch (error) {
      showMessage(formMessage, `No fue posible eliminar: ${error.message}`, "error");
    }
  }
});

cancelButton.addEventListener("click", () => {
  resetForm();
  showMessage(formMessage, "Edición cancelada.");
});

refreshButton.addEventListener("click", async () => {
  await loadRecords();
  showMessage(formMessage, "Lista consultada y actualizada correctamente.");
});

logoutButton.addEventListener("click", async () => {
  await database.signOut();
  resetForm();
  showMessage(formMessage, "Sesión administrativa cerrada.");
  await updateAdminView();
});

updateAdminView();

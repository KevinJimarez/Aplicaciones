const money = new Intl.NumberFormat("es-MX", { style: "currency", currency: "MXN", maximumFractionDigits: 0 });

async function readService(path) {
  const response = await fetch(path, { headers: { Accept: "application/json" } });
  if (!response.ok) throw new Error(`Servicio no disponible: ${response.status}`);
  return response.json();
}

function renderCustomers(customers) {
  document.querySelector("#customers-body").innerHTML = customers.map(customer => `
    <tr>
      <td><strong>${customer.name}</strong></td>
      <td>${customer.plan}</td>
      <td><span class="status ${customer.status === "Prueba" ? "trial" : ""}">${customer.status}</span></td>
    </tr>`).join("");
}

function renderPlans(plans) {
  document.querySelector("#plans").innerHTML = plans.map(plan => `
    <article class="plan ${plan.status === "Más elegido" ? "featured" : ""}">
      <div class="plan-top"><h3>${plan.name}</h3><em>${plan.status}</em></div>
      <div class="price">${money.format(plan.price)} <span>MXN / mes</span></div>
      <small>Hasta ${plan.users} usuarios</small>
    </article>`).join("");
}

async function loadDashboard() {
  try {
    const [summary, customers, plans] = await Promise.all([
      readService("/api/summary"),
      readService("/api/customers"),
      readService("/api/plans"),
    ]);
    document.querySelector("#revenue").textContent = money.format(summary.monthlyRevenue);
    document.querySelector("#customers-total").textContent = summary.activeCustomers;
    document.querySelector("#services-total").textContent = summary.servicesOnline;
    document.querySelector("#availability").textContent = `${summary.availability}%`;
    renderCustomers(customers);
    renderPlans(plans);
  } catch (error) {
    document.querySelector("#customers-body").innerHTML = `<tr><td class="error" colspan="3">${error.message}</td></tr>`;
    document.querySelector("#plans").innerHTML = `<p class="error">${error.message}</p>`;
  }
}

document.querySelector("#refresh").addEventListener("click", loadDashboard);
loadDashboard();

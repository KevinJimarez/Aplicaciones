import { SUPABASE_CONFIG } from "./supabase-config.js?v=7";

const SESSION_KEY = "ecoruta-admin-session";
const publicApiKey = SUPABASE_CONFIG.publishableKey || "";

const supabaseReady = () =>
  /^https:\/\/[a-z0-9-]+\.supabase\.co$/i.test(SUPABASE_CONFIG.url) &&
  publicApiKey.startsWith("sb_publishable_");

const readSession = () => {
  try {
    return JSON.parse(sessionStorage.getItem(SESSION_KEY) || "null");
  } catch {
    sessionStorage.removeItem(SESSION_KEY);
    return null;
  }
};

const saveSession = (payload) => {
  const session = {
    accessToken: payload.access_token,
    refreshToken: payload.refresh_token,
    expiresAt: Date.now() + payload.expires_in * 1000,
    user: payload.user
  };
  sessionStorage.setItem(SESSION_KEY, JSON.stringify(session));
  return session;
};

const authHeaders = (accessToken) => ({
  apikey: publicApiKey,
  "Content-Type": "application/json",
  Authorization: `Bearer ${accessToken || publicApiKey}`
});

const authRequest = async (path, options = {}) => {
  if (!supabaseReady()) {
    throw new Error("La conexión con Supabase todavía no está configurada.");
  }

  const response = await fetch(`${SUPABASE_CONFIG.url}/auth/v1/${path}`, {
    ...options,
    headers: authHeaders(options.accessToken)
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => null);
    throw new Error(detail?.msg || detail?.message || `Error de autenticación ${response.status}`);
  }

  if (response.status === 204) return null;
  const responseText = await response.text();
  return responseText ? JSON.parse(responseText) : null;
};

const getValidSession = async () => {
  const session = readSession();
  if (!session) return null;

  if (session.expiresAt > Date.now() + 60_000) {
    return session;
  }

  try {
    const refreshed = await authRequest("token?grant_type=refresh_token", {
      method: "POST",
      body: JSON.stringify({ refresh_token: session.refreshToken })
    });
    return saveSession(refreshed);
  } catch {
    sessionStorage.removeItem(SESSION_KEY);
    return null;
  }
};

const supabaseRequest = async (path, options = {}) => {
  if (!supabaseReady()) {
    throw new Error("La conexión con Supabase todavía no está configurada.");
  }

  const { requireAuth = false, ...requestOptions } = options;
  const session = await getValidSession();

  if (requireAuth && !session) {
    throw new Error("Inicia sesión como administrador para realizar esta operación.");
  }

  const response = await fetch(`${SUPABASE_CONFIG.url}/rest/v1/${path}`, {
    ...requestOptions,
    headers: {
      ...authHeaders(session?.accessToken),
      ...requestOptions.headers
    }
  });

  if (!response.ok) {
    const detail = await response.json().catch(() => null);
    throw new Error(detail?.message || detail?.hint || `Error HTTP ${response.status}`);
  }

  if (response.status === 204) return null;
  const responseText = await response.text();
  return responseText ? JSON.parse(responseText) : null;
};

export const database = {
  mode: supabaseReady() ? "supabase" : "unconfigured",

  async currentUser() {
    return (await getValidSession())?.user || null;
  },

  async signIn(email, password) {
    const payload = await authRequest("token?grant_type=password", {
      method: "POST",
      body: JSON.stringify({ email, password })
    });
    return saveSession(payload).user;
  },

  async signOut() {
    const session = readSession();
    try {
      if (session?.accessToken) {
        await authRequest("logout", { method: "POST", accessToken: session.accessToken });
      }
    } finally {
      sessionStorage.removeItem(SESSION_KEY);
    }
  },

  async list() {
    return supabaseRequest(
      "suscriptores?select=id,nombre,email,interes,recibir_actualizaciones,created_at,updated_at&order=created_at.desc",
      { requireAuth: true }
    );
  },

  async create(data) {
    return supabaseRequest("suscriptores", {
      method: "POST",
      headers: { Prefer: "return=minimal" },
      body: JSON.stringify(data)
    });
  },

  async update(id, data) {
    const records = await supabaseRequest(`suscriptores?id=eq.${encodeURIComponent(id)}`, {
      method: "PATCH",
      requireAuth: true,
      headers: { Prefer: "return=representation" },
      body: JSON.stringify({ ...data, updated_at: new Date().toISOString() })
    });
    return records[0];
  },

  async remove(id) {
    return supabaseRequest(`suscriptores?id=eq.${encodeURIComponent(id)}`, {
      method: "DELETE",
      requireAuth: true
    });
  }
};

import { SUPABASE_CONFIG } from "./supabase-config.js";

const DB_NAME = "ecoruta-db";
const DB_VERSION = 1;
const STORE_NAME = "suscriptores";

const publicApiKey = SUPABASE_CONFIG.publishableKey || SUPABASE_CONFIG.anonKey || "";

const supabaseReady = () =>
  /^https:\/\/[a-z0-9-]+\.supabase\.co$/i.test(SUPABASE_CONFIG.url) && publicApiKey.length > 20;

const supabaseHeaders = (extra = {}) => {
  const headers = {
    apikey: publicApiKey,
    "Content-Type": "application/json",
    ...extra
  };

  // Las claves anon heredadas son JWT y se envían también como Bearer.
  // Las publishable keys modernas solo necesitan el encabezado apikey.
  if (publicApiKey.startsWith("eyJ")) {
    headers.Authorization = `Bearer ${publicApiKey}`;
  }

  return headers;
};

const supabaseRequest = async (path, options = {}) => {
  const response = await fetch(`${SUPABASE_CONFIG.url}/rest/v1/${path}`, {
    ...options,
    headers: supabaseHeaders(options.headers)
  });

  if (!response.ok) {
    const detail = await response.text();
    throw new Error(detail || `Error HTTP ${response.status}`);
  }

  if (response.status === 204) return null;
  return response.json();
};

const openLocalDatabase = () =>
  new Promise((resolve, reject) => {
    const request = indexedDB.open(DB_NAME, DB_VERSION);

    request.onupgradeneeded = () => {
      const database = request.result;
      if (!database.objectStoreNames.contains(STORE_NAME)) {
        const store = database.createObjectStore(STORE_NAME, { keyPath: "id" });
        store.createIndex("email", "email", { unique: true });
      }
    };

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
  });

const localTransaction = async (mode, operation) => {
  const database = await openLocalDatabase();

  return new Promise((resolve, reject) => {
    const transaction = database.transaction(STORE_NAME, mode);
    const store = transaction.objectStore(STORE_NAME);
    const request = operation(store);

    request.onsuccess = () => resolve(request.result);
    request.onerror = () => reject(request.error);
    transaction.oncomplete = () => database.close();
    transaction.onerror = () => reject(transaction.error);
  });
};

const localList = async () => {
  const records = await localTransaction("readonly", (store) => store.getAll());
  return records.sort((a, b) => b.updated_at.localeCompare(a.updated_at));
};

const localCreate = async (data) => {
  const now = new Date().toISOString();
  const record = {
    id: crypto.randomUUID(),
    ...data,
    created_at: now,
    updated_at: now
  };
  await localTransaction("readwrite", (store) => store.add(record));
  return record;
};

const localUpdate = async (id, data) => {
  const current = await localTransaction("readonly", (store) => store.get(id));
  if (!current) throw new Error("El usuario que intentas editar ya no existe.");

  const record = { ...current, ...data, id, updated_at: new Date().toISOString() };
  await localTransaction("readwrite", (store) => store.put(record));
  return record;
};

const localDelete = (id) => localTransaction("readwrite", (store) => store.delete(id));

export const database = {
  mode: supabaseReady() ? "supabase" : "indexeddb",

  async list() {
    if (!supabaseReady()) return localList();
    return supabaseRequest(
      "suscriptores?select=id,nombre,email,interes,recibir_actualizaciones,created_at,updated_at&order=created_at.desc"
    );
  },

  async create(data) {
    if (!supabaseReady()) return localCreate(data);
    const records = await supabaseRequest("suscriptores", {
      method: "POST",
      headers: { Prefer: "return=representation" },
      body: JSON.stringify(data)
    });
    return records[0];
  },

  async update(id, data) {
    if (!supabaseReady()) return localUpdate(id, data);
    const records = await supabaseRequest(`suscriptores?id=eq.${encodeURIComponent(id)}`, {
      method: "PATCH",
      headers: { Prefer: "return=representation" },
      body: JSON.stringify({ ...data, updated_at: new Date().toISOString() })
    });
    return records[0];
  },

  async remove(id) {
    if (!supabaseReady()) return localDelete(id);
    return supabaseRequest(`suscriptores?id=eq.${encodeURIComponent(id)}`, { method: "DELETE" });
  }
};

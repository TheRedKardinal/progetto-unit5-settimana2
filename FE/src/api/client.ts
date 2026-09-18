export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL ?? 'http://localhost:8080';

const AUTH_STORAGE_KEY = 'baldchat_auth';

export interface StoredAuth {
  token: string;
  userId: string;
  username: string;
}

export function leggiAuthSalvata(): StoredAuth | null {
  const raw = localStorage.getItem(AUTH_STORAGE_KEY);
  if (!raw) return null;

  try {
    return JSON.parse(raw) as StoredAuth;
  } catch {
    return null;
  }
}

export function salvaAuth(auth: StoredAuth) {
  localStorage.setItem(AUTH_STORAGE_KEY, JSON.stringify(auth));
}

export function rimuoviAuth() {
  localStorage.removeItem(AUTH_STORAGE_KEY);
}

export class ApiError extends Error {
  status: number;

  constructor(status: number, message: string) {
    super(message);
    this.status = status;
  }
}

export async function apiFetch<T>(path: string, options: RequestInit = {}): Promise<T> {
  const auth = leggiAuthSalvata();
  const headers = new Headers(options.headers);
  headers.set('Content-Type', 'application/json');
  if (auth?.token) {
    headers.set('Authorization', `Bearer ${auth.token}`);
  }

  const response = await fetch(`${API_BASE_URL}${path}`, { ...options, headers });

  if (!response.ok) {
    let message = `Errore ${response.status}`;
    try {
      const body = await response.json();
      if (body?.error) message = body.error;
    } catch {
      // risposta senza corpo JSON, si usa il messaggio di default
    }
    throw new ApiError(response.status, message);
  }

  if (response.status === 204) {
    return undefined as T;
  }

  return (await response.json()) as T;
}

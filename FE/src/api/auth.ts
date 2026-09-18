import { apiFetch } from './client';
import type { AuthResponse } from '../types';

export function login(username: string, password: string) {
  return apiFetch<AuthResponse>('/api/auth/login', {
    method: 'POST',
    body: JSON.stringify({ username, password }),
  });
}

export function registrati(username: string, email: string, password: string) {
  return apiFetch<AuthResponse>('/api/auth/registrati', {
    method: 'POST',
    body: JSON.stringify({ username, email, password }),
  });
}

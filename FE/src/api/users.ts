import { apiFetch } from './client';
import type { UserSummary } from '../types';

export function cercaUtenti(query: string) {
  return apiFetch<UserSummary[]>(`/api/utenti?q=${encodeURIComponent(query)}`);
}

import { apiFetch } from './client';
import type { StatisticheResponse } from '../types';

export function inviaStatistiche() {
  return apiFetch<StatisticheResponse>('/api/utenti/me/statistiche', {
    method: 'POST',
  });
}

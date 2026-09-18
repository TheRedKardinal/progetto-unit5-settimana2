import { apiFetch } from './client';
import type { ChatDetail, ChatSummary, SuggerimentoResponse } from '../types';

export function listaChat() {
  return apiFetch<ChatSummary[]>('/api/chat');
}

export function apriChat(altroUtenteId: string) {
  return apiFetch<ChatDetail>('/api/chat/apri', {
    method: 'POST',
    body: JSON.stringify({ altroUtenteId }),
  });
}

export function chiediSuggerimento(chatId: string) {
  return apiFetch<SuggerimentoResponse>(`/api/chat/${chatId}/suggerimento`, {
    method: 'POST',
  });
}

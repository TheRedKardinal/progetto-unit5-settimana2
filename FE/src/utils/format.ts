export function iniziali(username: string): string {
  return username.trim().charAt(0).toUpperCase() || '?';
}

export function formattaOra(isoDate: string | null): string {
  if (!isoDate) return '';
  return new Intl.DateTimeFormat('it-IT', { hour: '2-digit', minute: '2-digit' }).format(new Date(isoDate));
}

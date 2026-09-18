export interface AuthResponse {
  token: string;
  userId: string;
  username: string;
}

export interface UserSummary {
  id: string;
  username: string;
}

export interface ChatSummary {
  id: string;
  altroUtenteId: string;
  altroUsername: string;
  ultimoMessaggioTesto: string | null;
  ultimoMessaggioData: string | null;
  nonLetti: number;
}

export interface Messaggio {
  id: string;
  chatId: string;
  testo: string;
  createdAt: string;
  mittenteId: string;
  letto: boolean;
}

export interface ChatDetail {
  id: string;
  altroUtenteId: string;
  altroUsername: string;
  createdAt: string;
  messaggi: Messaggio[];
}

export interface SuggerimentoResponse {
  suggerimento: string;
}

export interface StatisticheResponse {
  utenteId: string;
  username: string;
  messaggiInviati: number;
  messaggiRicevuti: number;
  numeroChat: number;
  emailInviata: boolean;
  emailDestinatario: string;
}

export interface LetturaEvent {
  chatId: string;
  lettoreId: string;
  momento: string;
}

import { createContext, useCallback, useContext, useMemo, useState, type ReactNode } from 'react';
import * as authApi from '../api/auth';
import { leggiAuthSalvata, rimuoviAuth, salvaAuth, type StoredAuth } from '../api/client';

interface AuthContextValue {
  utente: StoredAuth | null;
  login: (username: string, password: string) => Promise<void>;
  registrati: (username: string, email: string, password: string) => Promise<void>;
  logout: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [utente, setUtente] = useState<StoredAuth | null>(() => leggiAuthSalvata());

  const login = useCallback(async (username: string, password: string) => {
    const risposta = await authApi.login(username, password);
    const auth: StoredAuth = { token: risposta.token, userId: risposta.userId, username: risposta.username };
    salvaAuth(auth);
    setUtente(auth);
  }, []);

  const registrati = useCallback(async (username: string, email: string, password: string) => {
    const risposta = await authApi.registrati(username, email, password);
    const auth: StoredAuth = { token: risposta.token, userId: risposta.userId, username: risposta.username };
    salvaAuth(auth);
    setUtente(auth);
  }, []);

  const logout = useCallback(() => {
    rimuoviAuth();
    setUtente(null);
  }, []);

  const value = useMemo(() => ({ utente, login, registrati, logout }), [utente, login, registrati, logout]);

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components -- hook accoppiato al provider dello stesso context
export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth deve essere usato dentro AuthProvider');
  return ctx;
}

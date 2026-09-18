import { Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client';
import { createContext, useContext, useEffect, useMemo, useState, type ReactNode } from 'react';
import { API_BASE_URL } from '../api/client';
import { useAuth } from './AuthContext';

interface SocketContextValue {
  client: Client | null;
  connected: boolean;
}

const SocketContext = createContext<SocketContextValue>({ client: null, connected: false });

export function SocketProvider({ children }: { children: ReactNode }) {
  const { utente } = useAuth();
  const [client, setClient] = useState<Client | null>(null);
  const [connected, setConnected] = useState(false);

  useEffect(() => {
    if (!utente) return;

    // guardia contro il doppio mount di React StrictMode in sviluppo: se questo effect
    // viene già ripulito quando la connessione va a buon fine, la si chiude subito invece
    // di promuoverla a connessione "attiva", per non restare con due socket vivi in parallelo
    let ancoraMontato = true;

    const stompClient = new Client({
      webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
      connectHeaders: { Authorization: `Bearer ${utente.token}` },
      reconnectDelay: 4000,
      // rileva connessioni "zombie" (es. tab in background, proxy che chiude socket idle)
      // e forza una riconnessione automatica invece di restare silenziosamente disconnessi
      heartbeatIncoming: 10000,
      heartbeatOutgoing: 10000,
      onConnect: () => {
        if (!ancoraMontato) {
          stompClient.deactivate();
          return;
        }
        setClient(stompClient);
        setConnected(true);
      },
      onWebSocketClose: () => setConnected(false),
      onStompError: () => setConnected(false),
    });

    stompClient.activate();

    return () => {
      ancoraMontato = false;
      setConnected(false);
      setClient(null);
      stompClient.deactivate();
    };
  }, [utente]);

  const value = useMemo(() => ({ client, connected }), [client, connected]);

  return <SocketContext.Provider value={value}>{children}</SocketContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components -- hook accoppiato al provider dello stesso context
export function useSocket() {
  return useContext(SocketContext);
}

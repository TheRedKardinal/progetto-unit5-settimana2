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
  const [connected, setConnected] = useState(false);

  const client = useMemo(() => {
    if (!utente) return null;

    return new Client({
      webSocketFactory: () => new SockJS(`${API_BASE_URL}/ws`),
      connectHeaders: { Authorization: `Bearer ${utente.token}` },
      reconnectDelay: 4000,
      onConnect: () => setConnected(true),
      onWebSocketClose: () => setConnected(false),
      onStompError: () => setConnected(false),
    });
  }, [utente]);

  useEffect(() => {
    if (!client) return;

    client.activate();

    return () => {
      setConnected(false);
      client.deactivate();
    };
  }, [client]);

  const value = useMemo(() => ({ client, connected }), [client, connected]);

  return <SocketContext.Provider value={value}>{children}</SocketContext.Provider>;
}

// eslint-disable-next-line react-refresh/only-export-components -- hook accoppiato al provider dello stesso context
export function useSocket() {
  return useContext(SocketContext);
}

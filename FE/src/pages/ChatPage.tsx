import type { IMessage } from '@stomp/stompjs';
import { useEffect, useRef, useState } from 'react';
import { Toast, ToastContainer } from 'react-bootstrap';
import { apriChat, listaChat } from '../api/chat';
import { ApiError } from '../api/client';
import { inviaStatistiche } from '../api/stats';
import ChatWindow from '../components/ChatWindow';
import NewChatModal from '../components/NewChatModal';
import Sidebar from '../components/Sidebar';
import { useAuth } from '../context/AuthContext';
import { useSocket } from '../context/SocketContext';
import type { ChatDetail, ChatSummary, LetturaEvent, Messaggio, UserSummary } from '../types';

interface Notifica {
  tipo: 'success' | 'danger';
  testo: string;
}

export default function ChatPage() {
  const { utente, logout } = useAuth();
  const { client, connected } = useSocket();

  const [chats, setChats] = useState<ChatSummary[]>([]);
  const [selectedChat, setSelectedChat] = useState<ChatDetail | null>(null);
  const [showNewChatModal, setShowNewChatModal] = useState(false);
  const [statisticheInCorso, setStatisticheInCorso] = useState(false);
  const [notifica, setNotifica] = useState<Notifica | null>(null);

  const selectedChatIdRef = useRef<string | null>(null);

  useEffect(() => {
    selectedChatIdRef.current = selectedChat?.id ?? null;
  }, [selectedChat?.id]);

  useEffect(() => {
    if (!utente) return;
    listaChat()
      .then(setChats)
      .catch((err) => setNotifica({ tipo: 'danger', testo: err instanceof ApiError ? err.message : 'Errore nel caricamento delle chat' }));
  }, [utente]);

  function gestisciMessaggioRicevuto(messaggio: Messaggio) {
    const mio = messaggio.mittenteId === utente?.userId;
    const chatAperta = selectedChatIdRef.current === messaggio.chatId;

    setChats((prev) =>
      prev
        .map((c) =>
          c.id === messaggio.chatId
            ? {
                ...c,
                ultimoMessaggioTesto: messaggio.testo,
                ultimoMessaggioData: messaggio.createdAt,
                nonLetti: mio || chatAperta ? c.nonLetti : c.nonLetti + 1,
              }
            : c,
        )
        .sort((a, b) => new Date(b.ultimoMessaggioData ?? 0).getTime() - new Date(a.ultimoMessaggioData ?? 0).getTime()),
    );

    setSelectedChat((prev) => {
      if (!prev || prev.id !== messaggio.chatId) return prev;
      if (prev.messaggi.some((m) => m.id === messaggio.id)) return prev;
      return { ...prev, messaggi: [...prev.messaggi, messaggio] };
    });

    if (chatAperta && !mio) {
      client?.publish({ destination: '/app/chat.letto', body: JSON.stringify({ chatId: messaggio.chatId }) });
    }
  }

  function gestisciLettura(evento: LetturaEvent) {
    setSelectedChat((prev) => {
      if (!prev || prev.id !== evento.chatId) return prev;
      return {
        ...prev,
        messaggi: prev.messaggi.map((m) => (m.mittenteId === utente?.userId ? { ...m, letto: true } : m)),
      };
    });
  }

  useEffect(() => {
    if (!client || !connected || !utente || chats.length === 0) return;

    const subs = chats.flatMap((chat) => [
      client.subscribe(`/topic/chat.${chat.id}`, (frame: IMessage) => {
        const messaggio = JSON.parse(frame.body) as Messaggio;
        gestisciMessaggioRicevuto(messaggio);
      }),
      client.subscribe(`/topic/chat.${chat.id}.letture`, (frame: IMessage) => {
        const evento = JSON.parse(frame.body) as LetturaEvent;
        gestisciLettura(evento);
      }),
    ]);

    return () => subs.forEach((s) => s.unsubscribe());
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [client, connected, chats.map((c) => c.id).join(','), utente]);

  async function handleSelectChat(chatSummary: ChatSummary) {
    try {
      const dettaglio = await apriChat(chatSummary.altroUtenteId);
      setSelectedChat(dettaglio);
      setChats((prev) => prev.map((c) => (c.id === dettaglio.id ? { ...c, nonLetti: 0 } : c)));
      client?.publish({ destination: '/app/chat.letto', body: JSON.stringify({ chatId: dettaglio.id }) });
    } catch (err) {
      setNotifica({ tipo: 'danger', testo: err instanceof ApiError ? err.message : 'Impossibile aprire la chat' });
    }
  }

  async function handleNuovaChat(utenteSelezionato: UserSummary) {
    setShowNewChatModal(false);
    try {
      const dettaglio = await apriChat(utenteSelezionato.id);
      setSelectedChat(dettaglio);
      setChats((prev) => {
        if (prev.some((c) => c.id === dettaglio.id)) {
          return prev.map((c) => (c.id === dettaglio.id ? { ...c, nonLetti: 0 } : c));
        }
        const ultimo = dettaglio.messaggi.at(-1);
        const nuovaVoce: ChatSummary = {
          id: dettaglio.id,
          altroUtenteId: dettaglio.altroUtenteId,
          altroUsername: dettaglio.altroUsername,
          ultimoMessaggioTesto: ultimo?.testo ?? null,
          ultimoMessaggioData: ultimo?.createdAt ?? dettaglio.createdAt,
          nonLetti: 0,
        };
        return [nuovaVoce, ...prev];
      });
    } catch (err) {
      setNotifica({ tipo: 'danger', testo: err instanceof ApiError ? err.message : 'Impossibile avviare la chat' });
    }
  }

  function handleSendMessage(testo: string) {
    if (!selectedChat || !client || !connected) return;
    client.publish({ destination: '/app/chat.invia', body: JSON.stringify({ chatId: selectedChat.id, testo }) });
  }

  async function handleInviaStatistiche() {
    setStatisticheInCorso(true);
    try {
      const risposta = await inviaStatistiche();
      setNotifica({ tipo: 'success', testo: `Statistiche inviate a ${risposta.emailDestinatario}` });
    } catch (err) {
      setNotifica({ tipo: 'danger', testo: err instanceof ApiError ? err.message : 'Errore nell\'invio delle statistiche' });
    } finally {
      setStatisticheInCorso(false);
    }
  }

  if (!utente) return null;

  return (
    <div className="bc-app-shell">
      <Sidebar
        username={utente.username}
        chats={chats}
        selectedChatId={selectedChat?.id ?? null}
        onSelectChat={handleSelectChat}
        onOpenNewChat={() => setShowNewChatModal(true)}
        onLogout={logout}
        onInviaStatistiche={handleInviaStatistiche}
        statisticheInCorso={statisticheInCorso}
      />

      <ChatWindow
        key={selectedChat?.id ?? 'nessuna-chat'}
        chat={selectedChat}
        currentUserId={utente.userId}
        onSendMessage={handleSendMessage}
      />

      <NewChatModal
        key={showNewChatModal ? 'nuova-chat-aperta' : 'nuova-chat-chiusa'}
        show={showNewChatModal}
        onClose={() => setShowNewChatModal(false)}
        onUserSelected={handleNuovaChat}
      />

      <ToastContainer position="top-end" className="p-3" style={{ position: 'fixed', zIndex: 2000 }}>
        <Toast show={!!notifica} onClose={() => setNotifica(null)} delay={5000} autohide bg={notifica?.tipo}>
          <Toast.Body className={notifica?.tipo === 'danger' ? 'text-white' : ''}>{notifica?.testo}</Toast.Body>
        </Toast>
      </ToastContainer>
    </div>
  );
}

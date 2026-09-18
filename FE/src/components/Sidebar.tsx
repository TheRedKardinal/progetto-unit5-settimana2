import { Badge, Button, Spinner } from 'react-bootstrap';
import type { ChatSummary } from '../types';
import { formattaOra, iniziali } from '../utils/format';

interface SidebarProps {
  username: string;
  chats: ChatSummary[];
  selectedChatId: string | null;
  onSelectChat: (chat: ChatSummary) => void;
  onOpenNewChat: () => void;
  onLogout: () => void;
  onInviaStatistiche: () => void;
  statisticheInCorso: boolean;
  caricamentoChat: boolean;
  erroreCaricamentoChat: string | null;
  onRiprovaCaricamentoChat: () => void;
}

export default function Sidebar({
  username,
  chats,
  selectedChatId,
  onSelectChat,
  onOpenNewChat,
  onLogout,
  onInviaStatistiche,
  statisticheInCorso,
  caricamentoChat,
  erroreCaricamentoChat,
  onRiprovaCaricamentoChat,
}: SidebarProps) {
  return (
    <div className="bc-glass bc-sidebar">
      <div className="bc-sidebar-header">
        <div className="d-flex align-items-center gap-2">
          <span className="bc-avatar">{iniziali(username)}</span>
          <div>
            <div className="fw-semibold" style={{ fontSize: '0.92rem' }}>
              {username}
            </div>
            <div className="bc-gradient-text" style={{ fontSize: '0.78rem' }}>
              BaldChat
            </div>
          </div>
        </div>
        <Button className="bc-gradient-btn" size="sm" onClick={onOpenNewChat} title="Nuova chat">
          + Chat
        </Button>
      </div>

      <div className="px-3 pb-2 d-flex gap-2">
        <Button
          variant="light"
          size="sm"
          className="flex-fill rounded-pill"
          onClick={onInviaStatistiche}
          disabled={statisticheInCorso}
        >
          {statisticheInCorso ? <Spinner animation="border" size="sm" /> : '📊 Le mie statistiche'}
        </Button>
        <Button
          variant="light"
          size="sm"
          className="rounded-pill"
          onClick={onLogout}
          title="Esci dall'account"
        >
          Esci ⎋
        </Button>
      </div>

      <div className="bc-chat-list bc-scrollbar">
        {caricamentoChat && (
          <div className="text-center text-muted mt-4">
            <Spinner animation="border" size="sm" />
          </div>
        )}

        {!caricamentoChat && erroreCaricamentoChat && (
          <div className="text-center px-3 mt-4">
            <p className="text-danger mb-2" style={{ fontSize: '0.85rem' }}>
              Impossibile caricare le chat: {erroreCaricamentoChat}
            </p>
            <Button variant="light" size="sm" className="rounded-pill" onClick={onRiprovaCaricamentoChat}>
              Riprova
            </Button>
          </div>
        )}

        {!caricamentoChat && !erroreCaricamentoChat && chats.length === 0 && (
          <div className="text-center text-muted mt-4 px-3" style={{ fontSize: '0.85rem' }}>
            Nessuna chat ancora. Avviane una con il pulsante "+ Chat".
          </div>
        )}

        {!caricamentoChat && !erroreCaricamentoChat && chats.map((chat) => (
          <div
            key={chat.id}
            className={`bc-chat-item ${chat.id === selectedChatId ? 'active' : ''}`}
            onClick={() => onSelectChat(chat)}
          >
            <span className="bc-avatar">{iniziali(chat.altroUsername)}</span>
            <div className="bc-chat-item-body">
              <div className="d-flex justify-content-between align-items-center">
                <span className="fw-semibold" style={{ fontSize: '0.9rem' }}>
                  {chat.altroUsername}
                </span>
                <span className="text-muted" style={{ fontSize: '0.7rem' }}>
                  {formattaOra(chat.ultimoMessaggioData)}
                </span>
              </div>
              <div className="d-flex justify-content-between align-items-center">
                <span className="bc-chat-item-preview">
                  {chat.ultimoMessaggioTesto ?? 'Nessun messaggio ancora'}
                </span>
                {chat.nonLetti > 0 && (
                  <Badge className="bc-badge-unread" pill>
                    {chat.nonLetti}
                  </Badge>
                )}
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
}

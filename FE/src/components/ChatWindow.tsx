import { useEffect, useRef, useState, type FormEvent, type KeyboardEvent } from 'react';
import { Alert, Spinner } from 'react-bootstrap';
import { chiediSuggerimento } from '../api/chat';
import { ApiError } from '../api/client';
import type { ChatDetail } from '../types';
import { iniziali } from '../utils/format';
import MessageBubble from './MessageBubble';

interface ChatWindowProps {
  chat: ChatDetail | null;
  currentUserId: string;
  onSendMessage: (testo: string) => void;
}

export default function ChatWindow({ chat, currentUserId, onSendMessage }: ChatWindowProps) {
  const [testo, setTesto] = useState('');
  const [aiInCorso, setAiInCorso] = useState(false);
  const [aiErrore, setAiErrore] = useState<string | null>(null);
  const messagesEndRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [chat?.messaggi.length]);

  if (!chat) {
    return (
      <div className="bc-chat-main">
        <div className="bc-empty-state">
          <div style={{ fontSize: '2.4rem' }}>💬</div>
          <h4 className="bc-gradient-text mb-1">Benvenuto su BaldChat</h4>
          <p className="mb-0">Seleziona una chat dalla lista o avviane una nuova per iniziare a scrivere.</p>
        </div>
      </div>
    );
  }

  function inviaMessaggio() {
    const testoPulito = testo.trim();
    if (!testoPulito) return;
    onSendMessage(testoPulito);
    setTesto('');
  }

  function handleSubmit(e: FormEvent) {
    e.preventDefault();
    inviaMessaggio();
  }

  function handleKeyDown(e: KeyboardEvent<HTMLTextAreaElement>) {
    if (e.key === 'Enter' && !e.shiftKey) {
      e.preventDefault();
      inviaMessaggio();
    }
  }

  async function handleSuggerimento() {
    if (!chat) return;
    setAiInCorso(true);
    setAiErrore(null);
    try {
      const risposta = await chiediSuggerimento(chat.id);
      setTesto(risposta.suggerimento);
    } catch (err) {
      setAiErrore(err instanceof ApiError ? err.message : 'Suggerimento AI non disponibile');
    } finally {
      setAiInCorso(false);
    }
  }

  return (
    <div className="bc-chat-main">
      <div className="bc-glass bc-chat-topbar">
        <span className="bc-avatar">{iniziali(chat.altroUsername)}</span>
        <div className="fw-semibold">{chat.altroUsername}</div>
      </div>

      <div className="bc-messages bc-scrollbar">
        {chat.messaggi.length === 0 && (
          <div className="text-center text-muted mt-4">Nessun messaggio ancora, scrivi il primo!</div>
        )}
        {chat.messaggi.map((messaggio) => (
          <MessageBubble key={messaggio.id} messaggio={messaggio} mio={messaggio.mittenteId === currentUserId} />
        ))}
        <div ref={messagesEndRef} />
      </div>

      <div className="bc-composer">
        {aiErrore && (
          <Alert variant="warning" className="py-2 mb-2" onClose={() => setAiErrore(null)} dismissible>
            {aiErrore}
          </Alert>
        )}
        <form onSubmit={handleSubmit} className="bc-glass bc-composer-card">
          <button
            type="button"
            className="bc-icon-btn"
            onClick={handleSuggerimento}
            disabled={aiInCorso}
            title="Suggerimento AI"
          >
            {aiInCorso ? <Spinner animation="border" size="sm" /> : '✨'}
          </button>
          <textarea
            className="form-control bc-composer-input"
            rows={1}
            placeholder="Scrivi un messaggio..."
            value={testo}
            onChange={(e) => setTesto(e.target.value)}
            onKeyDown={handleKeyDown}
          />
          <button type="submit" className="bc-icon-btn bc-gradient-btn" disabled={!testo.trim()} title="Invia">
            ➤
          </button>
        </form>
      </div>
    </div>
  );
}

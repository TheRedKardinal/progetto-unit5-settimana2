import { useEffect, useState } from 'react';
import { Form, ListGroup, Modal, Spinner } from 'react-bootstrap';
import { cercaUtenti } from '../api/users';
import type { UserSummary } from '../types';
import { iniziali } from '../utils/format';

interface NewChatModalProps {
  show: boolean;
  onClose: () => void;
  onUserSelected: (utente: UserSummary) => void;
}

export default function NewChatModal({ show, onClose, onUserSelected }: NewChatModalProps) {
  const [query, setQuery] = useState('');
  const [risultati, setRisultati] = useState<UserSummary[]>([]);
  const [caricamento, setCaricamento] = useState(false);

  useEffect(() => {
    if (!show) return;

    const timeout = setTimeout(() => {
      setCaricamento(true);
      cercaUtenti(query)
        .then(setRisultati)
        .finally(() => setCaricamento(false));
    }, 250);

    return () => clearTimeout(timeout);
  }, [query, show]);

  return (
    <Modal show={show} onHide={onClose} centered>
      <Modal.Header closeButton className="border-0">
        <Modal.Title className="bc-gradient-text h5">Nuova chat</Modal.Title>
      </Modal.Header>
      <Modal.Body>
        <Form.Control
          className="bc-form-control mb-3"
          placeholder="Cerca per username..."
          aria-label="Cerca utenti per username"
          value={query}
          onChange={(e) => setQuery(e.target.value)}
          autoFocus
        />

        {caricamento && (
          <div className="text-center py-3">
            <Spinner animation="border" size="sm" />
          </div>
        )}

        {!caricamento && risultati.length === 0 && (
          <p className="text-muted text-center mb-0">Nessun utente trovato</p>
        )}

        <ListGroup variant="flush">
          {risultati.map((utente) => (
            <ListGroup.Item
              key={utente.id}
              action
              onClick={() => onUserSelected(utente)}
              className="d-flex align-items-center gap-3 border-0 rounded-3 mb-1"
            >
              <span className="bc-avatar bc-avatar-sm">{iniziali(utente.username)}</span>
              <span className="fw-semibold">{utente.username}</span>
            </ListGroup.Item>
          ))}
        </ListGroup>
      </Modal.Body>
    </Modal>
  );
}

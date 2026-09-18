import { useState, type FormEvent } from 'react';
import { Alert, Button, Form } from 'react-bootstrap';
import { Link, useNavigate } from 'react-router-dom';
import { ApiError } from '../api/client';
import { useAuth } from '../context/AuthContext';

export default function RegisterPage() {
  const { registrati } = useAuth();
  const navigate = useNavigate();
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [errore, setErrore] = useState<string | null>(null);
  const [caricamento, setCaricamento] = useState(false);

  async function handleSubmit(e: FormEvent) {
    e.preventDefault();
    setErrore(null);
    setCaricamento(true);
    try {
      await registrati(username, email, password);
      navigate('/', { replace: true });
    } catch (err) {
      setErrore(err instanceof ApiError ? err.message : 'Errore di connessione al server');
    } finally {
      setCaricamento(false);
    }
  }

  return (
    <div className="bc-auth-page">
      <div className="bc-glass bc-auth-card">
        <div className="text-center mb-4">
          <div className="bc-logo-mark mx-auto mb-3">💬</div>
          <h1 className="h3 mb-1 bc-gradient-text">BaldChat</h1>
          <p className="text-muted mb-0">Crea il tuo account per iniziare a chattare</p>
        </div>

        {errore && <Alert variant="danger">{errore}</Alert>}

        <Form onSubmit={handleSubmit}>
          <Form.Group className="mb-3" controlId="register-username">
            <Form.Label>Username</Form.Label>
            <Form.Control
              className="bc-form-control"
              value={username}
              onChange={(e) => setUsername(e.target.value)}
              minLength={3}
              required
              autoFocus
            />
          </Form.Group>
          <Form.Group className="mb-3" controlId="register-email">
            <Form.Label>Email</Form.Label>
            <Form.Control
              className="bc-form-control"
              type="email"
              value={email}
              onChange={(e) => setEmail(e.target.value)}
              required
            />
          </Form.Group>
          <Form.Group className="mb-4" controlId="register-password">
            <Form.Label>Password</Form.Label>
            <Form.Control
              className="bc-form-control"
              type="password"
              value={password}
              onChange={(e) => setPassword(e.target.value)}
              minLength={8}
              required
            />
            <Form.Text className="text-muted">Almeno 8 caratteri</Form.Text>
          </Form.Group>
          <Button type="submit" className="bc-gradient-btn w-100 py-2" disabled={caricamento}>
            {caricamento ? 'Creazione account...' : 'Registrati'}
          </Button>
        </Form>

        <p className="text-center mt-4 mb-0 text-muted">
          Hai già un account?{' '}
          <Link to="/login" className="bc-muted-link fw-semibold">
            Accedi
          </Link>
        </p>
      </div>
    </div>
  );
}

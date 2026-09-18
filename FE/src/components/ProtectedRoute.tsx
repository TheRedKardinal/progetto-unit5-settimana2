import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function ProtectedRoute() {
  const { utente } = useAuth();

  if (!utente) {
    return <Navigate to="/login" replace />;
  }

  return <Outlet />;
}

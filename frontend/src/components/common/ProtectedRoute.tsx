import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuth } from '@context';
import { UserRole } from '@services';
import { Loading } from './Loading';

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles?: UserRole[];
}

export const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children, allowedRoles }) => {
  const { isAuthenticated, user, validate } = useAuth();
  const [isValidating, setIsValidating] = useState(true);
  const [isValid, setIsValid] = useState(false);
  const [denialReason, setDenialReason] = useState<'NOT_AUTHENTICATED' | 'INSUFFICIENT_PERMISSIONS' | null>(null);

  useEffect(() => {
    const validateToken = async () => {
      setIsValidating(true);
      try {
        const valid = await validate();
        if (!valid) {
          setIsValid(false);
          setDenialReason('NOT_AUTHENTICATED');
        } else {
          setIsValid(true);
        }
      } catch (err) {
        setIsValid(false);
        setDenialReason('NOT_AUTHENTICATED');
      } finally {
        setIsValidating(false);
      }
    };

    if (isAuthenticated) {
      validateToken();
    } else {
      setIsValidating(false);
      setIsValid(false);
      setDenialReason('NOT_AUTHENTICATED');
    }
  }, [isAuthenticated, validate]);

  if (isValidating) {
    return <Loading />;
  }

  if (!isValid) {
    localStorage.setItem('access_denial_reason', denialReason || 'NOT_AUTHENTICATED');
    return <Navigate to="/access-denied" replace />;
  }

  if (allowedRoles && user && !allowedRoles.includes(user.rol)) {
    localStorage.setItem('access_denial_reason', 'INSUFFICIENT_PERMISSIONS');
    return <Navigate to="/access-denied" replace />;
  }

  return <>{children}</>;
};

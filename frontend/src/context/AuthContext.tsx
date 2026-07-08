import React, { createContext, ReactNode, useState, useCallback } from 'react';
import { User, userService, authService } from '@services';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<User>;
  logout: () => void;
  updateProfile: (data: Partial<Omit<User, 'id' | 'rol' | 'createdAt'>>) => Promise<void>;
  validate: () => Promise<boolean>;
}

export const AuthContext = createContext<AuthContextType | undefined>(undefined);

function clearBrowserSession(): void {
  localStorage.removeItem('user_token');
  localStorage.removeItem('user_data');
  sessionStorage.clear();

  if (typeof document === 'undefined') {
    return;
  }

  document.cookie.split(';').forEach((cookie) => {
    const trimmedCookie = cookie.trim();
    if (!trimmedCookie) {
      return;
    }

    const separatorIndex = trimmedCookie.indexOf('=');
    const cookieName = separatorIndex >= 0 ? trimmedCookie.slice(0, separatorIndex) : trimmedCookie;
    document.cookie = `${cookieName}=;expires=${new Date(0).toUTCString()};path=/`;
  });
}

export const AuthProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const stored = localStorage.getItem('user_data');
    return stored ? (JSON.parse(stored) as User) : null;
  });
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const login = useCallback(async (email: string, password: string): Promise<User> => {
    setIsLoading(true);
    setError(null);
    try {
      const response = await authService.login({ email, password });
      setUser(response.user);
      localStorage.setItem('user_token', response.token);
      localStorage.setItem('user_data', JSON.stringify(response.user));
      return response.user;
    } catch (err) {
      setError(err instanceof Error ? err.message : 'Error al iniciar sesión');
      throw err;
    } finally {
      setIsLoading(false);
    }
  }, []);

  const logout = useCallback(() => {
    authService.logout();
    clearBrowserSession();
    setUser(null);
    setError(null);
  }, []);

  const updateProfile = useCallback(
    async (data: Partial<Omit<User, 'id' | 'rol' | 'createdAt'>>) => {
      if (!user) return;
      setIsLoading(true);
      try {
        const response = await userService.updateProfile(user.id, data);
        setUser(response.data);
        localStorage.setItem('user_data', JSON.stringify(response.data));
      } finally {
        setIsLoading(false);
      }
    },
    [user],
  );

  const validate = useCallback(async (): Promise<boolean> => {
    const token = localStorage.getItem('user_token');
    if (!token) {
      setUser(null);
      return false;
    }

    try {
      const validatedUser = await authService.validateToken(token);
      setUser(validatedUser);
      localStorage.setItem('user_data', JSON.stringify(validatedUser));
      return true;
    } catch (err) {
      // Token is invalid or expired
      logout();
      return false;
    }
  }, []);

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, error, login, logout, updateProfile, validate }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = (): AuthContextType => {
  const context = React.useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuth debe ser usado dentro de AuthProvider');
  }
  return context;
};

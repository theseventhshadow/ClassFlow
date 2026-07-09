import React, { createContext, ReactNode, useState, useCallback, useEffect } from 'react';

/**
 * Tipo de tema
 */
export type ThemeType = 'light' | 'dark';

/**
 * Interfaz del contexto de tema
 */
interface ThemeContextType {
  theme: ThemeType;
  toggleTheme: () => void;
  setTheme: (theme: ThemeType) => void;
}

/**
 * Contexto de tema
 */
export const ThemeContext = createContext<ThemeContextType | undefined>(undefined);

/**
 * Proveedor de tema
 */
export const ThemeProvider: React.FC<{ children: ReactNode }> = ({ children }) => {
  const [theme, setThemeState] = useState<ThemeType>(
    () => (localStorage.getItem('theme') as ThemeType) || 'light'
  );

  const setTheme = useCallback((newTheme: ThemeType) => {
    setThemeState(newTheme);
    localStorage.setItem('theme', newTheme);
    document.documentElement.setAttribute('data-theme', newTheme);
  }, []);

  const toggleTheme = useCallback(() => {
    setTheme(theme === 'light' ? 'dark' : 'light');
  }, [theme, setTheme]);

  // El HTML fija data-theme="light" de forma estatica; sincroniza el atributo real
  // con el tema persistido apenas monta, para que un tema oscuro guardado se aplique
  // de entrada en vez de quedar desfasado hasta el proximo toggle.
  useEffect(() => {
    document.documentElement.setAttribute('data-theme', theme);
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  return (
    <ThemeContext.Provider value={{ theme, toggleTheme, setTheme }}>
      {children}
    </ThemeContext.Provider>
  );
};

/**
 * Hook personalizado para usar el contexto de tema
 */
export const useTheme = (): ThemeContextType => {
  const context = React.useContext(ThemeContext);
  if (context === undefined) {
    throw new Error('useTheme debe ser usado dentro de ThemeProvider');
  }
  return context;
};

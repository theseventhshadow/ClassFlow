import React from 'react';
import { ThemeProvider, AuthProvider } from '@context';
import AppRouter from '@/router';
import '@styles/index.css';

/**
 * Componente principal de la aplicación
 */
function App(): React.ReactElement {
  return (
    <ThemeProvider>
      <AuthProvider>
        <AppRouter />
      </AuthProvider>
    </ThemeProvider>
  );
}

export default App;

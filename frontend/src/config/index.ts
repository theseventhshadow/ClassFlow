/**
 * Configuración global de la aplicación
 */

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api';
const API_TIMEOUT = 30000; // 30 segundos

export const config = {
  api: {
    baseURL: API_BASE_URL,
    timeout: API_TIMEOUT,
  },
  app: {
    name: 'ClassFlow',
    version: '0.1.0',
  },
} as const;

export default config;

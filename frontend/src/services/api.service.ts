import axios, { AxiosInstance, AxiosError } from 'axios';
import { config } from '@config';
import { ApiError } from '@types';

/**
 * Instancia de Axios configurada
 * Centraliza la lógica de comunicación con la API
 */
class ApiService {
  private instance: AxiosInstance;

  constructor() {
    this.instance = axios.create({
      baseURL: config.api.baseURL,
      timeout: config.api.timeout,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  /**
   * Configura interceptores para manejo global de errores y tokens
   */
  private setupInterceptors(): void {
    // Interceptor de respuesta
    this.instance.interceptors.response.use(
      (response) => response,
      (error: AxiosError) => {
        const apiError: ApiError = {
          message: error.message,
          status: error.response?.status || 500,
          details: error.response?.data,
        };

        // Aquí puedes agregar lógica global de manejo de errores
        console.error('API Error:', apiError);

        return Promise.reject(apiError);
      }
    );

    // Interceptor de solicitud
    this.instance.interceptors.request.use((config) => {
      // Aquí puedes agregar el token de autenticación si existe
      // Excepto en endpoints públicos como login, register, etc.
      const publicEndpoints = ['/auth/login', '/auth/register', '/auth/forgot-password'];
      const isPublicEndpoint = publicEndpoints.some((endpoint) => config.url?.includes(endpoint));

      if (!isPublicEndpoint) {
        const token = localStorage.getItem('user_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
      }
      return config;
    });
  }

  /**
   * Método GET genérico
   */
  async get<T = unknown>(url: string, config?: Record<string, unknown>): Promise<T> {
    const response = await this.instance.get<T>(url, config);
    return response.data;
  }

  /**
   * Método POST genérico
   */
  async post<T = unknown>(url: string, data?: unknown): Promise<T> {
    const response = await this.instance.post<T>(url, data);
    return response.data;
  }

  /**
   * Método PUT genérico
   */
  async put<T = unknown>(url: string, data?: unknown): Promise<T> {
    const response = await this.instance.put<T>(url, data);
    return response.data;
  }

  /**
   * Método PATCH genérico
   */
  async patch<T = unknown>(url: string, data?: unknown): Promise<T> {
    const response = await this.instance.patch<T>(url, data);
    return response.data;
  }

  /**
   * Método DELETE genérico
   */
  async delete<T = unknown>(url: string): Promise<T> {
    const response = await this.instance.delete<T>(url);
    return response.data;
  }
}

// Exportar instancia única
export const apiService = new ApiService();

export default apiService;

import { apiService } from './api.service';
import { ApiResponse } from '@types';
import { User } from './user.service';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  user: User;
}

export interface RegisterRequest {
  firstName: string;
  lastName: string;
  idNumber: string;
  email: string;
  password: string;
  role: string;
  course?: string;
  guardianId?: number;
}

interface BackendUser {
  id: string;
  first_name?: string;
  last_name?: string;
  nombre?: string;
  email: string;
  role?: string;
  rol?: string;
  active?: boolean;
  activo?: boolean;
  createdAt?: string;
  created_at?: string;
  subject?: string;
  course?: string;
  guardian_id?: string | null;
  phone?: string;
  bio?: string;
}

interface BackendLoginResponse {
  token: string;
  // Some backends return a nested `user` object, others return flattened fields.
  user?: BackendUser;
  // Flattened form
  email?: string;
  role?: string;
  fullName?: string;
}

class AuthService {
  async login(data: LoginRequest): Promise<LoginResponse> {
    const response = await apiService.post<LoginResponse | ApiResponse<LoginResponse>>('/auth/login', data);

    if (this.isApiResponse(response)) {
      return this.normalizeLoginResponse(response.data as unknown as BackendLoginResponse);
    }

    return this.normalizeLoginResponse(response as unknown as BackendLoginResponse);
  }

  async register(data: RegisterRequest): Promise<User> {
    const response = await apiService.post<BackendUser>('/auth/register', data);
    return this.normalizeUser(response);
  }

  async validateToken(token: string): Promise<User> {
    const response = await apiService.get<BackendUser>(
      '/auth/validate',
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );

    return this.normalizeUser(response);
  }

  logout(): void {
    localStorage.removeItem('user_token');
    localStorage.removeItem('user_data');
  }

  private isApiResponse(value: LoginResponse | ApiResponse<LoginResponse>): value is ApiResponse<LoginResponse> {
    return typeof value === 'object' && value !== null && 'data' in value && 'status' in value;
  }

  private normalizeLoginResponse(response: BackendLoginResponse): LoginResponse {
    if (response.user) {
      return {
        token: response.token,
        user: this.normalizeUser(response.user),
      };
    }

    // Fallback for flattened response from the backend
    const userFallback = {
      id: '',
      nombre: response.fullName ?? response.email ?? '',
      email: response.email ?? '',
      rol: this.normalizeRole(response.role),
      activo: true,
      createdAt: new Date().toISOString(),
    } as unknown as User;

    return {
      token: response.token,
      user: userFallback,
    };
  }

  private normalizeUser(user: BackendUser): User {
    const firstName = user.first_name ?? '';
    const lastName = user.last_name ?? '';
    const nombre = user.nombre ?? `${firstName} ${lastName}`.trim();

    return {
      id: user.id,
      nombre: nombre || user.email,
      email: user.email,
      rol: this.normalizeRole(user.role ?? user.rol),
      activo: user.active ?? user.activo ?? true,
      createdAt: user.createdAt ?? user.created_at ?? new Date().toISOString(),
      subject: user.subject ?? user.course,
      phone: user.phone,
      bio: user.bio,
    };
  }

  private normalizeRole(role?: string): User['rol'] {
    switch (role) {
      case 'ADMINISTRATOR':
      case 'ADMIN':
      case 'ADMINISTRADOR':
        return 'ADMINISTRATOR';
      case 'TEACHER':
      case 'DOCENTE':
        return 'TEACHER';
      case 'GUARDIAN':
      case 'APODERADO':
        return 'GUARDIAN';
      case 'STUDENT':
      case 'ESTUDIANTE':
      default:
        return 'STUDENT';
    }
  }
}

export const authService = new AuthService();

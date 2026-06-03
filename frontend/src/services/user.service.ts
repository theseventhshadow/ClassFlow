import { apiService } from './api.service';
import { ApiResponse, PaginatedResponse } from '@types';

export type UserRole = 'ADMINISTRADOR' | 'DOCENTE' | 'APODERADO' | 'ESTUDIANTE';

export interface User {
  id: string;
  nombre: string;
  email: string;
  rol: UserRole;
  activo: boolean;
  createdAt: string;
  subject?: string;
  phone?: string;
  bio?: string;
}

export interface ChangePasswordPayload {
  currentPassword: string;
  newPassword: string;
}

class UserService {
  private readonly endpoint = '/users';

  async getUsers(page = 1, limit = 10): Promise<PaginatedResponse<User>> {
    return apiService.get(`${this.endpoint}?page=${page}&limit=${limit}`);
  }

  async getUserById(id: string): Promise<ApiResponse<User>> {
    return apiService.get(`${this.endpoint}/${id}`);
  }

  async createUser(userData: Omit<User, 'id' | 'createdAt'>): Promise<ApiResponse<User>> {
    return apiService.post(this.endpoint, userData);
  }

  async updateUser(id: string, userData: Partial<User>): Promise<ApiResponse<User>> {
    return apiService.put(`${this.endpoint}/${id}`, userData);
  }

  async deleteUser(id: string): Promise<ApiResponse<void>> {
    return apiService.delete(`${this.endpoint}/${id}`);
  }

  async updateProfile(id: string, data: Partial<Omit<User, 'id' | 'rol' | 'createdAt'>>): Promise<ApiResponse<User>> {
    return apiService.put(`${this.endpoint}/${id}/profile`, data);
  }

  async changePassword(id: string, payload: ChangePasswordPayload): Promise<ApiResponse<void>> {
    return apiService.patch(`${this.endpoint}/${id}/password`, payload);
  }
}

export const userService = new UserService();
export default userService;

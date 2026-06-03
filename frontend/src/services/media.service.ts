/**
 * Servicio para manejar recursos de media (imágenes, videos, audios)
 * Obtiene URLs desde el backend o CDN
 */

import { apiService } from './api.service';
import { ApiResponse } from '@types';

export interface MediaResource {
  id: string;
  url: string;
  type: 'image' | 'video' | 'audio';
  mimeType: string;
  size: number;
  createdAt: string;
}

class MediaService {
  private readonly endpoint = '/media';
  private placeholders = {
    image: '/assets/placeholders/image-placeholder.svg',
    video: '/assets/placeholders/video-placeholder.svg',
    audio: '/assets/placeholders/audio-placeholder.svg',
  };

  /**
   * Obtiene todas los recursos de media
   */
  async getMediaResources(type?: string): Promise<ApiResponse<MediaResource[]>> {
    const query = type ? `?type=${type}` : '';
    return apiService.get(`${this.endpoint}${query}`);
  }

  /**
   * Obtiene un recurso por ID
   */
  async getMediaById(id: string): Promise<ApiResponse<MediaResource>> {
    return apiService.get(`${this.endpoint}/${id}`);
  }

  /**
   * Elimina un recurso de media
   */
  async deleteMedia(id: string): Promise<ApiResponse<void>> {
    return apiService.delete(`${this.endpoint}/${id}`);
  }

  /**
   * Obtiene URL de placeholder por tipo de media
   */
  getPlaceholderUrl(type: 'image' | 'video' | 'audio' = 'image'): string {
    return this.placeholders[type];
  }

  /**
   * Genera URL optimizada para imagen
   */
  getOptimizedImageUrl(
    url: string,
    _options?: {
      width?: number;
      height?: number;
      quality?: 'low' | 'medium' | 'high';
      format?: 'webp' | 'jpg' | 'png';
    }
  ): string {
    if (!url) return this.getPlaceholderUrl('image');
    return url;
  }
}

export const mediaService = new MediaService();

export default mediaService;

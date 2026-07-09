import { apiService } from './api.service';

export type AnnotationType = 'POSITIVE' | 'NEGATIVE';

export interface CreateAnnotationRequest {
  studentId: number;
  teacherId: number;
  type: AnnotationType;
  description: string;
}

export interface Annotation {
  id: number;
  studentId: number;
  teacherId: number;
  type: AnnotationType;
  description: string;
  date: string;
  active?: boolean | null;
}

class AnnotationService {
  private readonly endpoint = '/annotations';

  async createAnnotation(payload: CreateAnnotationRequest): Promise<Annotation> {
    return apiService.post<Annotation>(this.endpoint, payload);
  }
}

export const annotationService = new AnnotationService();
export default annotationService;

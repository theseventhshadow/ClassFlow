import { apiService } from './api.service';

export interface CreateGradeRequest {
  studentId: number;
  score: number;
  observations?: string;
  evaluationId: number;
}

export interface Grade {
  id: number;
  studentId: number;
  score: number;
  observations?: string | null;
  evaluationId: number;
}

class GradeService {
  private readonly endpoint = '/grades';

  async createGrade(payload: CreateGradeRequest): Promise<Grade> {
    return apiService.post<Grade>(this.endpoint, payload);
  }
}

export const gradeService = new GradeService();
export default gradeService;

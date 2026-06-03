import { apiService } from './api.service';

export interface CreateCourseRequest {
  name: string;
  description?: string;
  academicYear: number;
}

export interface Course {
  id: number;
  name: string;
  description?: string | null;
  academicYear?: number | null;
  active?: boolean | null;
}

class CourseService {
  // baseURL already contains the API prefix (config.api.baseURL), avoid duplicating '/api'
  private readonly endpoint = '/courses';

  async createCourse(payload: CreateCourseRequest): Promise<Course> {
    return apiService.post<Course>(this.endpoint, payload);
  }

  async getAll(): Promise<Course[]> {
    return apiService.get<Course[]>(this.endpoint);
  }
}

export const courseService = new CourseService();
export default courseService;

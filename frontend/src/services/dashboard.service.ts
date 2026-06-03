import { apiService } from './api.service';
import { User } from './user.service';

export interface DashboardCourse {
  id: number;
  name: string;
  description?: string | null;
  academicYear?: number | null;
  active?: boolean | null;
}

export interface DashboardSubject {
  id: number;
  name: string;
  description?: string | null;
  courseId?: number | null;
  active?: boolean | null;
}

export interface DashboardEvaluation {
  id: number;
  name: string;
  description?: string | null;
  maxScore?: number | null;
  percentage?: number | null;
  date?: string | null;
  subjectId?: number | null;
}

export interface DashboardGrade {
  id: number;
  studentId: number;
  score?: number | null;
  observations?: string | null;
  evaluationId?: number | null;
}

export interface DashboardAttendance {
  id: number;
  studentId: number;
  courseId: number;
  date: string;
  present: boolean;
  justification?: string | null;
}

export interface DashboardAnnotation {
  id: number;
  studentId: number;
  teacherId: number;
  type: string;
  description: string;
  date: string;
  active?: boolean | null;
}

export interface DashboardMessage {
  id: number;
  senderId: number;
  receiverId: number;
  subject: string;
  body: string;
  read?: boolean | null;
  sentAt?: string | null;
}

export interface DashboardAnnouncement {
  id: number;
  title: string;
  content: string;
  courseId?: number | null;
  senderId: number;
  publishedAt?: string | null;
  active?: boolean | null;
}

export interface DashboardNotification {
  id: number;
  userId: number;
  type: string;
  subject: string;
  content: string;
  sent?: boolean | null;
  sentAt?: string | null;
  createdAt?: string | null;
  errorMessage?: string | null;
}

export interface DashboardResponse {
  user: User;
  role: string;
  courses: DashboardCourse[];
  subjects: DashboardSubject[];
  evaluations: DashboardEvaluation[];
  grades: DashboardGrade[];
  attendances: DashboardAttendance[];
  annotations: DashboardAnnotation[];
  messages: DashboardMessage[];
  unreadMessages: DashboardMessage[];
  announcements: DashboardAnnouncement[];
  notifications: DashboardNotification[];
  pendingNotifications: DashboardNotification[];
}

class DashboardService {
  async getDashboard(userId: string): Promise<DashboardResponse> {
    return apiService.get(`/bff/dashboard/${userId}`);
  }
}

export const dashboardService = new DashboardService();

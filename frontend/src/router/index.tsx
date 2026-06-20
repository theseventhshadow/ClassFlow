import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from '@components/layout';
import { HomePage, DashboardPage, NotFoundPage, LoginPage, AdminDashboard, TeacherAccountPage, StudentDashboardPage, GuardianDashboardPage, AccessDeniedPage } from '@pages';
import { ProtectedRoute } from '@components/common/ProtectedRoute';
import { ROUTES } from '@constants';

const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path={ROUTES.LOGIN} element={<LoginPage />} />
        <Route path={ROUTES.ACCESS_DENIED} element={<AccessDeniedPage />} />

        <Route
          path={ROUTES.DASHBOARD_ADMIN}
          element={
            <ProtectedRoute allowedRoles={['ADMINISTRATOR']}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path={ROUTES.DASHBOARD_TEACHER}
          element={
            <ProtectedRoute allowedRoles={['TEACHER']}>
              <TeacherAccountPage />
            </ProtectedRoute>
          }
        />

        <Route
          path={ROUTES.DASHBOARD_STUDENT}
          element={
            <ProtectedRoute allowedRoles={['STUDENT']}>
              <StudentDashboardPage />
            </ProtectedRoute>
          }
        />

        <Route
          path={ROUTES.DASHBOARD_GUARDIAN}
          element={
            <ProtectedRoute allowedRoles={['GUARDIAN']}>
              <GuardianDashboardPage />
            </ProtectedRoute>
          }
        />

        <Route element={<Layout />}>
          <Route path={ROUTES.HOME} element={<HomePage />} />
          <Route path={ROUTES.NOT_FOUND} element={<NotFoundPage />} />
        </Route>
        <Route path={ROUTES.DASHBOARD} element={<DashboardPage />} />
      </Routes>
    </BrowserRouter>
  );
};

export default AppRouter;

import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { Layout } from '@components/layout';
import { HomePage, DashboardPage, NotFoundPage, LoginPage, AdminDashboard, TeacherAccountPage, AccessDeniedPage } from '@pages';
import { ProtectedRoute } from '@components/common/ProtectedRoute';
import { ROUTES } from '@constants';

const AppRouter: React.FC = () => {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" replace />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/access-denied" element={<AccessDeniedPage />} />

        <Route
          path="/admin"
          element={
            <ProtectedRoute allowedRoles={['ADMINISTRADOR']}>
              <AdminDashboard />
            </ProtectedRoute>
          }
        />

        <Route
          path={ROUTES.TEACHER_ACCOUNT}
          element={
            <ProtectedRoute allowedRoles={['DOCENTE']}>
              <TeacherAccountPage />
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

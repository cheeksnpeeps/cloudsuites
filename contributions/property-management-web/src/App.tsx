import React from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { ProtectedRoute } from './components/auth/ProtectedRoute';
import { LoginPage } from './components/auth/LoginPage';
import { RegisterPage } from './components/auth/RegisterPage';
import { AdminDashboard } from './components/dashboards/AdminDashboard';
import { OwnerDashboard } from './components/dashboards/OwnerDashboard';
import { StaffDashboard } from './components/dashboards/StaffDashboard';
import { TenantDashboard } from './components/dashboards/TenantDashboard';

function App() {
  return (
    <AuthProvider>
      <div className="min-h-screen bg-background">
        <Routes>
          {/* Public Routes */}
          <Route path="/login" element={<LoginPage />} />
          <Route path="/register" element={<RegisterPage />} />
          
          {/* Protected Routes */}
          <Route
            path="/admin/*"
            element={
              <ProtectedRoute allowedRoles={['ADMIN']}>
                <Routes>
                  <Route path="dashboard" element={<AdminDashboard />} />
                  <Route path="" element={<Navigate to="dashboard" replace />} />
                </Routes>
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/staff/*"
            element={
              <ProtectedRoute allowedRoles={['STAFF']}>
                <Routes>
                  <Route path="dashboard" element={<StaffDashboard />} />
                  <Route path="" element={<Navigate to="dashboard" replace />} />
                </Routes>
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/tenant/*"
            element={
              <ProtectedRoute allowedRoles={['TENANT']}>
                <Routes>
                  <Route path="dashboard" element={<TenantDashboard />} />
                  <Route path="" element={<Navigate to="dashboard" replace />} />
                </Routes>
              </ProtectedRoute>
            }
          />
          
          <Route
            path="/owner/*"
            element={
              <ProtectedRoute allowedRoles={['OWNER']}>
                <Routes>
                  <Route path="dashboard" element={<OwnerDashboard />} />
                  <Route path="" element={<Navigate to="dashboard" replace />} />
                </Routes>
              </ProtectedRoute>
            }
          />
          
          {/* Default route - redirect to appropriate dashboard or login */}
          <Route path="/" element={<Navigate to="/login" replace />} />
          
          {/* Catch all route */}
          <Route path="*" element={<Navigate to="/login" replace />} />
        </Routes>
      </div>
    </AuthProvider>
  );
}

export default App;
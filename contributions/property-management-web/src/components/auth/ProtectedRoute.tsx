import { ReactNode } from 'react';
import { Navigate } from 'react-router-dom';
import { useAuthContext } from '../../contexts/AuthContext';
import { RoleType } from '../../api/auth';

interface ProtectedRouteProps {
  children: ReactNode;
  allowedRoles?: RoleType[];
  requireAuth?: boolean;
}

export const ProtectedRoute = ({ 
  children, 
  allowedRoles = [], 
  requireAuth = true 
}: ProtectedRouteProps) => {
  const { isAuthenticated, hasAnyRole, isLoading } = useAuthContext();

  // Show loading spinner while checking authentication
  if (isLoading) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-primary mx-auto mb-4"></div>
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  // Check if authentication is required
  if (requireAuth && !isAuthenticated()) {
    return <Navigate to="/login" replace />;
  }

  // Check if user has required role
  if (allowedRoles.length > 0 && !hasAnyRole(allowedRoles)) {
    // Redirect to appropriate dashboard based on user's actual role
    const userRole = useAuthContext().getUserRole();
    const roleBasedRoute = userRole ? `/${userRole.toLowerCase()}/dashboard` : '/login';
    return <Navigate to={roleBasedRoute} replace />;
  }

  return <>{children}</>;
};

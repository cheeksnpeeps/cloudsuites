import { useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import toast from 'react-hot-toast';
import { 
  authService, 
  AuthResponse, 
  User, 
  RoleType, 
  LoginRequest, 
  RegisterRequest
} from '../api/auth';
import { useApiQuery, useApiMutation, queryKeys, useInvalidateQueries } from './useApi';

// Authentication Query Hooks
export const useCurrentUser = (options?: { enabled?: boolean }) => {
  return useApiQuery<User>({
    queryKey: queryKeys.auth.currentUser(),
    queryFn: () => authService.getCurrentUser(),
    enabled: authService.isAuthenticated() && (options?.enabled ?? true),
    retry: false,
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

// Authentication Mutation Hooks
export const useLogin = () => {
  const navigate = useNavigate();
  const { invalidateAll } = useInvalidateQueries();

  return useApiMutation<AuthResponse, { credentials: Omit<LoginRequest, 'userType'>; userType: RoleType }>({
    mutationFn: async ({ credentials, userType }) => {
      switch (userType) {
        case 'ADMIN':
          return authService.adminLogin(credentials);
        case 'STAFF':
          return authService.staffLogin(credentials);
        case 'TENANT':
          return authService.tenantLogin(credentials);
        case 'OWNER':
          return authService.ownerLogin(credentials);
        default:
          throw new Error('Invalid user type');
      }
    },
    onSuccess: (data) => {
      toast.success(`Welcome back, ${data.user.firstName}!`);
      invalidateAll();
      
      // Navigate to appropriate dashboard based on role
      const roleBasedRoute = getRoleBasedRoute(data.user.role.roleType);
      navigate(roleBasedRoute);
    },
    onError: (error) => {
      toast.error(error.message || 'Login failed');
    },
  });
};

export const useRegister = () => {
  const navigate = useNavigate();
  const { invalidateAll } = useInvalidateQueries();

  return useApiMutation<AuthResponse, { data: Omit<RegisterRequest, 'userType'>; userType: RoleType }>({
    mutationFn: async ({ data, userType }) => {
      switch (userType) {
        case 'ADMIN':
          return authService.adminRegister(data);
        case 'STAFF':
          return authService.staffRegister(data as any);
        case 'TENANT':
          return authService.tenantRegister(data as any);
        case 'OWNER':
          return authService.ownerRegister(data as any);
        default:
          throw new Error('Invalid user type');
      }
    },
    onSuccess: (data) => {
      toast.success(`Welcome to CloudSuites, ${data.user.firstName}!`);
      invalidateAll();
      
      // Navigate to appropriate dashboard based on role
      const roleBasedRoute = getRoleBasedRoute(data.user.role.roleType);
      navigate(roleBasedRoute);
    },
    onError: (error) => {
      toast.error(error.message || 'Registration failed');
    },
  });
};

export const useRequestOTP = () => {
  return useApiMutation<{ message: string; otpSent: boolean }, { email: string; userType: RoleType }>({
    mutationFn: async ({ email, userType }) => {
      switch (userType) {
        case 'ADMIN':
          return authService.adminRequestOTP(email);
        case 'STAFF':
          return authService.staffRequestOTP(email);
        case 'TENANT':
          return authService.tenantRequestOTP(email);
        case 'OWNER':
          return authService.ownerRequestOTP(email);
        default:
          throw new Error('Invalid user type');
      }
    },
    onSuccess: (data) => {
      if (data.otpSent) {
        toast.success('OTP sent to your email');
      }
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to send OTP');
    },
  });
};

export const useVerifyOTP = () => {
  const navigate = useNavigate();
  const { invalidateAll } = useInvalidateQueries();

  return useApiMutation<AuthResponse, { email: string; otp: string; userType: RoleType }>({
    mutationFn: async ({ email, otp, userType }) => {
      switch (userType) {
        case 'ADMIN':
          return authService.adminVerifyOTP(email, otp);
        case 'STAFF':
          return authService.staffVerifyOTP(email, otp);
        case 'TENANT':
          return authService.tenantVerifyOTP(email, otp);
        case 'OWNER':
          return authService.ownerVerifyOTP(email, otp);
        default:
          throw new Error('Invalid user type');
      }
    },
    onSuccess: (data) => {
      toast.success(`Welcome, ${data.user.firstName}!`);
      invalidateAll();
      
      // Navigate to appropriate dashboard based on role
      const roleBasedRoute = getRoleBasedRoute(data.user.role.roleType);
      navigate(roleBasedRoute);
    },
    onError: (error) => {
      toast.error(error.message || 'OTP verification failed');
    },
  });
};

export const useLogout = () => {
  const navigate = useNavigate();
  const { invalidateAll } = useInvalidateQueries();

  return useApiMutation<void, void>({
    mutationFn: () => authService.logout(),
    onSuccess: () => {
      toast.success('Logged out successfully');
      invalidateAll();
      navigate('/login');
    },
    onError: (error) => {
      // Still logout locally even if server logout fails
      toast.error(error.message || 'Logout failed');
      invalidateAll();
      navigate('/login');
    },
  });
};

export const useUpdatePassword = () => {
  return useApiMutation<void, { currentPassword: string; newPassword: string }>({
    mutationFn: ({ currentPassword, newPassword }) => 
      authService.updatePassword(currentPassword, newPassword),
    onSuccess: () => {
      toast.success('Password updated successfully');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to update password');
    },
  });
};

export const useRequestPasswordReset = () => {
  return useApiMutation<{ message: string }, { email: string; userType: RoleType }>({
    mutationFn: ({ email, userType }) => authService.requestPasswordReset(email, userType),
    onSuccess: (data) => {
      toast.success(data.message || 'Password reset email sent');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to send password reset email');
    },
  });
};

export const useResetPassword = () => {
  const navigate = useNavigate();

  return useApiMutation<{ message: string }, { token: string; newPassword: string }>({
    mutationFn: ({ token, newPassword }) => authService.resetPassword(token, newPassword),
    onSuccess: (data) => {
      toast.success(data.message || 'Password reset successfully');
      navigate('/login');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to reset password');
    },
  });
};

// Authentication Utility Hooks
export const useAuth = () => {
  const { data: user, isLoading, error } = useCurrentUser();
  
  const isAuthenticated = useCallback(() => {
    return authService.isAuthenticated();
  }, []);

  const getUserRole = useCallback((): RoleType | null => {
    return authService.getCurrentUserRole();
  }, []);

  const getUserId = useCallback((): string | null => {
    return authService.getCurrentUserId();
  }, []);

  const hasRole = useCallback((role: RoleType): boolean => {
    const userRole = getUserRole();
    return userRole === role;
  }, [getUserRole]);

  const hasAnyRole = useCallback((roles: RoleType[]): boolean => {
    const userRole = getUserRole();
    return userRole ? roles.includes(userRole) : false;
  }, [getUserRole]);

  const isAdmin = useCallback((): boolean => {
    return hasRole('ADMIN');
  }, [hasRole]);

  const isStaff = useCallback((): boolean => {
    return hasRole('STAFF');
  }, [hasRole]);

  const isTenant = useCallback((): boolean => {
    return hasRole('TENANT');
  }, [hasRole]);

  const isOwner = useCallback((): boolean => {
    return hasRole('OWNER');
  }, [hasRole]);

  return {
    user,
    isLoading,
    error,
    isAuthenticated,
    getUserRole,
    getUserId,
    hasRole,
    hasAnyRole,
    isAdmin,
    isStaff,
    isTenant,
    isOwner,
  };
};

// Utility function to get role-based routes
const getRoleBasedRoute = (role: RoleType): string => {
  switch (role) {
    case 'ADMIN':
      return '/admin/dashboard';
    case 'STAFF':
      return '/staff/dashboard';
    case 'TENANT':
      return '/tenant/dashboard';
    case 'OWNER':
      return '/owner/dashboard';
    default:
      return '/dashboard';
  }
};

// Export utility functions
export { getRoleBasedRoute };

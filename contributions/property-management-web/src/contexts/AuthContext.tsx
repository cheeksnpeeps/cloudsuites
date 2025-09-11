import React, { createContext, useContext, useEffect, ReactNode } from 'react';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { BrowserRouter } from 'react-router-dom';
import { Toaster } from 'react-hot-toast';
import { useAuth } from '../hooks/useAuth';
import { User, RoleType } from '../api/auth';

// Create Query Client
const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      gcTime: 10 * 60 * 1000, // 10 minutes (formerly cacheTime)
      retry: 2,
      refetchOnWindowFocus: false,
    },
    mutations: {
      retry: 1,
    },
  },
});

// Auth Context Type
interface AuthContextType {
  user: User | undefined;
  isLoading: boolean;
  error: any;
  isAuthenticated: () => boolean;
  getUserRole: () => RoleType | null;
  getUserId: () => string | null;
  hasRole: (role: RoleType) => boolean;
  hasAnyRole: (roles: RoleType[]) => boolean;
  isAdmin: () => boolean;
  isStaff: () => boolean;
  isTenant: () => boolean;
  isOwner: () => boolean;
}

// Create Context
const AuthContext = createContext<AuthContextType | undefined>(undefined);

// Auth Provider Component
interface AuthProviderProps {
  children: ReactNode;
}

const AuthProviderInner: React.FC<{ children: ReactNode }> = ({ children }) => {
  const auth = useAuth();

  // Auto-refresh user data on mount if authenticated
  useEffect(() => {
    if (auth.isAuthenticated() && !auth.user && !auth.isLoading) {
      // The useCurrentUser hook will automatically fetch user data
      console.log('User is authenticated but no user data found, fetching...');
    }
  }, [auth.isAuthenticated, auth.user, auth.isLoading]);

  return (
    <AuthContext.Provider value={auth}>
      {children}
    </AuthContext.Provider>
  );
};

export const AuthProvider: React.FC<AuthProviderProps> = ({ children }) => {
  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <AuthProviderInner>
          {children}
          <Toaster
            position="top-right"
            toastOptions={{
              duration: 4000,
              style: {
                background: '#363636',
                color: '#fff',
              },
              success: {
                duration: 3000,
                iconTheme: {
                  primary: '#4ade80',
                  secondary: '#fff',
                },
              },
              error: {
                duration: 5000,
                iconTheme: {
                  primary: '#ef4444',
                  secondary: '#fff',
                },
              },
            }}
          />
        </AuthProviderInner>
      </BrowserRouter>
    </QueryClientProvider>
  );
};

// Hook to use Auth Context
export const useAuthContext = (): AuthContextType => {
  const context = useContext(AuthContext);
  if (context === undefined) {
    throw new Error('useAuthContext must be used within an AuthProvider');
  }
  return context;
};

// Export query client for external use
export { queryClient };

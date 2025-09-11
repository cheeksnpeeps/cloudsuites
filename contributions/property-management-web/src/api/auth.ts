import { ApiService, TokenManager } from './index';

// User Types
export interface User {
  userId: string;
  email: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  role: UserRole;
  status: UserStatus;
  createdAt: string;
  lastModifiedAt: string;
}

export interface UserRole {
  roleId: string;
  roleName: string;
  roleType: RoleType;
  permissions: string[];
}

export type RoleType = 'ADMIN' | 'STAFF' | 'TENANT' | 'OWNER';
export type UserStatus = 'ACTIVE' | 'INACTIVE' | 'PENDING' | 'SUSPENDED';

// Authentication Request Types
export interface LoginRequest {
  email: string;
  password: string;
  userType: RoleType;
}

export interface RegisterRequest {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  userType: RoleType;
  companyId?: string;
  buildingId?: string;
  unitId?: string;
}

export interface OTPRequest {
  email: string;
  userType: RoleType;
}

export interface OTPVerificationRequest {
  email: string;
  otp: string;
  userType: RoleType;
}

export interface RefreshTokenRequest {
  refreshToken: string;
}

// Authentication Response Types
export interface AuthResponse {
  token: string;
  refreshToken: string;
  user: User;
  expiresIn: number;
}

export interface OTPResponse {
  message: string;
  otpSent: boolean;
  expiresAt: string;
}

// Authentication Service
class AuthenticationService extends ApiService {
  // Admin Authentication
  async adminLogin(credentials: Omit<LoginRequest, 'userType'>): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/admin/login', {
      ...credentials,
      userType: 'ADMIN',
    });
    
    // Store tokens
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async adminRegister(data: Omit<RegisterRequest, 'userType'>): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/admin/register', {
      ...data,
      userType: 'ADMIN',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async adminRequestOTP(email: string): Promise<OTPResponse> {
    return this.post<OTPResponse>('/auth/admin/otp/request', {
      email,
      userType: 'ADMIN',
    });
  }

  async adminVerifyOTP(email: string, otp: string): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/admin/otp/verify', {
      email,
      otp,
      userType: 'ADMIN',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  // Staff Authentication
  async staffLogin(credentials: Omit<LoginRequest, 'userType'>): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/staff/login', {
      ...credentials,
      userType: 'STAFF',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async staffRegister(data: Omit<RegisterRequest, 'userType'> & { companyId: string; buildingId: string }): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/staff/register', {
      ...data,
      userType: 'STAFF',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async staffRequestOTP(email: string): Promise<OTPResponse> {
    return this.post<OTPResponse>('/auth/staff/otp/request', {
      email,
      userType: 'STAFF',
    });
  }

  async staffVerifyOTP(email: string, otp: string): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/staff/otp/verify', {
      email,
      otp,
      userType: 'STAFF',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  // Tenant Authentication
  async tenantLogin(credentials: Omit<LoginRequest, 'userType'>): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/tenant/login', {
      ...credentials,
      userType: 'TENANT',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async tenantRegister(data: Omit<RegisterRequest, 'userType'> & { buildingId: string; unitId: string }): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/tenant/register', {
      ...data,
      userType: 'TENANT',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async tenantRequestOTP(email: string): Promise<OTPResponse> {
    return this.post<OTPResponse>('/auth/tenant/otp/request', {
      email,
      userType: 'TENANT',
    });
  }

  async tenantVerifyOTP(email: string, otp: string): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/tenant/otp/verify', {
      email,
      otp,
      userType: 'TENANT',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  // Owner Authentication
  async ownerLogin(credentials: Omit<LoginRequest, 'userType'>): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/owner/login', {
      ...credentials,
      userType: 'OWNER',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async ownerRegister(data: Omit<RegisterRequest, 'userType'> & { buildingId: string; unitId: string }): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/owner/register', {
      ...data,
      userType: 'OWNER',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async ownerRequestOTP(email: string): Promise<OTPResponse> {
    return this.post<OTPResponse>('/auth/owner/otp/request', {
      email,
      userType: 'OWNER',
    });
  }

  async ownerVerifyOTP(email: string, otp: string): Promise<AuthResponse> {
    const response = await this.post<AuthResponse>('/auth/owner/otp/verify', {
      email,
      otp,
      userType: 'OWNER',
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  // Common Authentication Methods
  async refreshToken(): Promise<AuthResponse> {
    const refreshToken = TokenManager.getRefreshToken();
    if (!refreshToken) {
      throw new Error('No refresh token available');
    }

    const response = await this.post<AuthResponse>('/auth/refresh', {
      refreshToken,
    });
    
    TokenManager.setToken(response.token);
    TokenManager.setRefreshToken(response.refreshToken);
    
    return response;
  }

  async logout(): Promise<void> {
    const token = TokenManager.getToken();
    
    try {
      // Optionally call backend logout endpoint if it exists
      await this.post('/auth/logout', { token });
    } catch (error) {
      // Continue with logout even if backend call fails
      console.warn('Backend logout failed:', error);
    } finally {
      // Always clear local tokens
      TokenManager.clearTokens();
    }
  }

  async getCurrentUser(): Promise<User> {
    return this.get<User>('/auth/me');
  }

  async updatePassword(currentPassword: string, newPassword: string): Promise<void> {
    return this.post('/auth/password/update', {
      currentPassword,
      newPassword,
    });
  }

  async requestPasswordReset(email: string, userType: RoleType): Promise<{ message: string }> {
    return this.post('/auth/password/reset/request', {
      email,
      userType,
    });
  }

  async resetPassword(token: string, newPassword: string): Promise<{ message: string }> {
    return this.post('/auth/password/reset', {
      token,
      newPassword,
    });
  }

  // Utility Methods
  isAuthenticated(): boolean {
    const token = TokenManager.getToken();
    return token !== null && !TokenManager.isTokenExpired(token);
  }

  getCurrentUserRole(): RoleType | null {
    const token = TokenManager.getToken();
    if (!token || TokenManager.isTokenExpired(token)) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.type || payload.role || null;
    } catch {
      return null;
    }
  }

  getCurrentUserId(): string | null {
    const token = TokenManager.getToken();
    if (!token || TokenManager.isTokenExpired(token)) {
      return null;
    }

    try {
      const payload = JSON.parse(atob(token.split('.')[1]));
      return payload.userId || payload.sub || null;
    } catch {
      return null;
    }
  }
}

// Export singleton instance
export const authService = new AuthenticationService();
export default authService;

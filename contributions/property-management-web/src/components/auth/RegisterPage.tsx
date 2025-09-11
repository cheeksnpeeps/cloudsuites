import { Link, Navigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Alert, AlertDescription } from '../ui/alert';
import { useRegister } from '../../hooks/useAuth';
import { useAuthContext } from '../../contexts/AuthContext';
import { RoleType } from '../../api/auth';

interface RegisterFormData {
  email: string;
  password: string;
  confirmPassword: string;
  firstName: string;
  lastName: string;
  phoneNumber?: string;
  userType: RoleType;
  companyId?: string;
  buildingId?: string;
  unitId?: string;
}

export const RegisterPage = () => {
  const { isAuthenticated } = useAuthContext();
  const register = useRegister();
  
  const form = useForm<RegisterFormData>({
    defaultValues: {
      email: '',
      password: '',
      confirmPassword: '',
      firstName: '',
      lastName: '',
      phoneNumber: '',
      userType: 'TENANT',
      companyId: '',
      buildingId: '',
      unitId: '',
    },
  });

  const watchUserType = form.watch('userType');

  // Redirect if already authenticated
  if (isAuthenticated()) {
    return <Navigate to="/" replace />;
  }

  const handleRegister = (data: RegisterFormData) => {
    if (data.password !== data.confirmPassword) {
      form.setError('confirmPassword', {
        type: 'manual',
        message: 'Passwords do not match',
      });
      return;
    }

    const { confirmPassword, ...registerData } = data;
    
    register.mutate({
      data: registerData,
      userType: data.userType,
    });
  };

  const requiresCompanyInfo = watchUserType === 'STAFF';
  const requiresBuildingInfo = ['STAFF', 'TENANT', 'OWNER'].includes(watchUserType);
  const requiresUnitInfo = ['TENANT', 'OWNER'].includes(watchUserType);

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4 py-8">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">Join CloudSuites</CardTitle>
          <CardDescription>
            Create your property management account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <form onSubmit={form.handleSubmit(handleRegister)} className="space-y-4">
            
            {/* Account Type */}
            <div className="space-y-2">
              <Label htmlFor="register-userType">Account Type *</Label>
              <Select 
                value={form.watch('userType')} 
                onValueChange={(value: RoleType) => form.setValue('userType', value)}
              >
                <SelectTrigger id="register-userType" name="userType" aria-label="Account Type">
                  <SelectValue placeholder="Select account type" />
                </SelectTrigger>
                <SelectContent>
                  <SelectItem value="ADMIN">Administrator</SelectItem>
                  <SelectItem value="STAFF">Staff Member</SelectItem>
                  <SelectItem value="OWNER">Property Owner</SelectItem>
                  <SelectItem value="TENANT">Tenant</SelectItem>
                </SelectContent>
              </Select>
            </div>

            {/* Personal Information */}
            <div className="grid grid-cols-2 gap-4">
              <div className="space-y-2">
                <Label htmlFor="firstName">First Name *</Label>
                <Input
                  id="firstName"
                  type="text"
                  autoComplete="given-name"
                  placeholder="John"
                  {...form.register('firstName', { required: 'First name is required' })}
                />
                {form.formState.errors.firstName && (
                  <p className="text-sm text-red-600">{form.formState.errors.firstName.message}</p>
                )}
              </div>
              
              <div className="space-y-2">
                <Label htmlFor="lastName">Last Name *</Label>
                <Input
                  id="lastName"
                  type="text"
                  autoComplete="family-name"
                  placeholder="Doe"
                  {...form.register('lastName', { required: 'Last name is required' })}
                />
                {form.formState.errors.lastName && (
                  <p className="text-sm text-red-600">{form.formState.errors.lastName.message}</p>
                )}
              </div>
            </div>

            {/* Contact Information */}
            <div className="space-y-2">
              <Label htmlFor="register-email">Email *</Label>
              <Input
                id="register-email"
                type="email"
                autoComplete="email"
                placeholder="john.doe@example.com"
                {...form.register('email', { 
                  required: 'Email is required',
                  pattern: {
                    value: /^\S+@\S+$/i,
                    message: 'Invalid email address'
                  }
                })}
              />
              {form.formState.errors.email && (
                <p className="text-sm text-red-600">{form.formState.errors.email.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="phoneNumber">Phone Number</Label>
              <Input
                id="phoneNumber"
                type="tel"
                autoComplete="tel"
                placeholder="+1 (555) 123-4567"
                {...form.register('phoneNumber')}
              />
            </div>

            {/* Password Fields */}
            <div className="space-y-2">
              <Label htmlFor="register-password">Password *</Label>
              <Input
                id="register-password"
                type="password"
                autoComplete="new-password"
                placeholder="Enter a strong password"
                {...form.register('password', { 
                  required: 'Password is required',
                  minLength: {
                    value: 8,
                    message: 'Password must be at least 8 characters'
                  }
                })}
              />
              {form.formState.errors.password && (
                <p className="text-sm text-red-600">{form.formState.errors.password.message}</p>
              )}
            </div>

            <div className="space-y-2">
              <Label htmlFor="confirmPassword">Confirm Password *</Label>
              <Input
                id="confirmPassword"
                type="password"
                placeholder="Confirm your password"
                {...form.register('confirmPassword', { required: 'Please confirm your password' })}
              />
              {form.formState.errors.confirmPassword && (
                <p className="text-sm text-red-600">{form.formState.errors.confirmPassword.message}</p>
              )}
            </div>

            {/* Company Information for Staff */}
            {requiresCompanyInfo && (
              <div className="space-y-2">
                <Label htmlFor="companyId">Company ID *</Label>
                <Input
                  id="companyId"
                  placeholder="Enter company ID"
                  {...form.register('companyId', { 
                    required: requiresCompanyInfo ? 'Company ID is required for staff' : false 
                  })}
                />
                {form.formState.errors.companyId && (
                  <p className="text-sm text-red-600">{form.formState.errors.companyId.message}</p>
                )}
              </div>
            )}

            {/* Building Information */}
            {requiresBuildingInfo && (
              <div className="space-y-2">
                <Label htmlFor="buildingId">Building ID *</Label>
                <Input
                  id="buildingId"
                  placeholder="Enter building ID"
                  {...form.register('buildingId', { 
                    required: requiresBuildingInfo ? 'Building ID is required' : false 
                  })}
                />
                {form.formState.errors.buildingId && (
                  <p className="text-sm text-red-600">{form.formState.errors.buildingId.message}</p>
                )}
              </div>
            )}

            {/* Unit Information for Tenants and Owners */}
            {requiresUnitInfo && (
              <div className="space-y-2">
                <Label htmlFor="unitId">Unit ID *</Label>
                <Input
                  id="unitId"
                  placeholder="Enter unit ID (e.g., 101, 2A)"
                  {...form.register('unitId', { 
                    required: requiresUnitInfo ? 'Unit ID is required' : false 
                  })}
                />
                {form.formState.errors.unitId && (
                  <p className="text-sm text-red-600">{form.formState.errors.unitId.message}</p>
                )}
              </div>
            )}

            {/* Info Alert */}
            {(requiresCompanyInfo || requiresBuildingInfo || requiresUnitInfo) && (
              <Alert>
                <AlertDescription>
                  {watchUserType === 'ADMIN' && 'As an administrator, you will have full system access.'}
                  {watchUserType === 'STAFF' && 'As a staff member, you need company and building IDs from your employer.'}
                  {watchUserType === 'TENANT' && 'As a tenant, you need building and unit IDs from your lease agreement.'}
                  {watchUserType === 'OWNER' && 'As an owner, you need building and unit IDs for your property.'}
                </AlertDescription>
              </Alert>
            )}

            <Button 
              type="submit" 
              className="w-full" 
              disabled={register.isPending}
            >
              {register.isPending ? 'Creating Account...' : 'Create Account'}
            </Button>
          </form>

          <div className="mt-6 text-center">
            <p className="text-sm text-muted-foreground">
              Already have an account?{' '}
              <Link to="/login" className="font-medium text-primary hover:underline">
                Sign in
              </Link>
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

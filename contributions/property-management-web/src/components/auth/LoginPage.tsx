import { useState } from 'react';
import { Link, Navigate } from 'react-router-dom';
import { useForm } from 'react-hook-form';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Card, CardContent, CardDescription, CardHeader, CardTitle } from '../ui/card';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '../ui/select';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../ui/tabs';
import { Alert, AlertDescription } from '../ui/alert';
import { useLogin, useRequestOTP, useVerifyOTP } from '../../hooks/useAuth';
import { useAuthContext } from '../../contexts/AuthContext';
import { RoleType } from '../../api/auth';

interface LoginFormData {
  email: string;
  password: string;
  userType: RoleType;
}

interface OTPFormData {
  email: string;
  userType: RoleType;
  otp: string;
}

export const LoginPage = () => {
  const { isAuthenticated } = useAuthContext();
  const [loginType, setLoginType] = useState<'password' | 'otp'>('password');
  const [showOTPInput, setShowOTPInput] = useState(false);
  const [otpEmail, setOTPEmail] = useState('');


  const login = useLogin();
  const requestOTP = useRequestOTP();
  const verifyOTP = useVerifyOTP();

  const loginForm = useForm<LoginFormData>({
    defaultValues: {
      email: '',
      password: '',
      userType: 'TENANT',
    },
  });

  const otpForm = useForm<OTPFormData>({
    defaultValues: {
      email: '',
      userType: 'TENANT',
      otp: '',
    },
  });

  // Redirect if already authenticated
  if (isAuthenticated()) {
    return <Navigate to="/" replace />;
  }

  const handlePasswordLogin = (data: LoginFormData) => {
    login.mutate({
      credentials: {
        email: data.email,
        password: data.password,
      },
      userType: data.userType,
    });
  };

  const handleRequestOTP = (data: Pick<OTPFormData, 'email' | 'userType'>) => {
    setOTPEmail(data.email);
    
    requestOTP.mutate(
      { email: data.email, userType: data.userType },
      {
        onSuccess: () => {
          setShowOTPInput(true);
          otpForm.setValue('email', data.email);
          otpForm.setValue('userType', data.userType);
        },
      }
    );
  };

  const handleVerifyOTP = (data: OTPFormData) => {
    verifyOTP.mutate({
      email: data.email,
      otp: data.otp,
      userType: data.userType,
    });
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50 px-4">
      <Card className="w-full max-w-md">
        <CardHeader className="text-center">
          <CardTitle className="text-2xl">CloudSuites</CardTitle>
          <CardDescription>
            Sign in to your property management account
          </CardDescription>
        </CardHeader>
        <CardContent>
          <Tabs value={loginType} onValueChange={(v) => setLoginType(v as 'password' | 'otp')}>
            <TabsList className="grid w-full grid-cols-2">
              <TabsTrigger value="password">Password</TabsTrigger>
              <TabsTrigger value="otp">OTP</TabsTrigger>
            </TabsList>

            <TabsContent value="password">
              <form onSubmit={loginForm.handleSubmit(handlePasswordLogin)} className="space-y-4">
                <div className="space-y-2">
                  <Label htmlFor="login-userType">Account Type</Label>
                  <Select 
                    value={loginForm.watch('userType')} 
                    onValueChange={(value: RoleType) => loginForm.setValue('userType', value)}
                  >
                    <SelectTrigger id="login-userType" name="userType" aria-label="Account Type">
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

                <div className="space-y-2">
                  <Label htmlFor="login-email">Email</Label>
                  <Input
                    id="login-email"
                    type="email"
                    autoComplete="email"
                    placeholder="Enter your email"
                    {...loginForm.register('email', { required: 'Email is required' })}
                  />
                  {loginForm.formState.errors.email && (
                    <p className="text-sm text-red-600">{loginForm.formState.errors.email.message}</p>
                  )}
                </div>

                <div className="space-y-2">
                  <Label htmlFor="login-password">Password</Label>
                  <Input
                    id="login-password"
                    type="password"
                    autoComplete="current-password"
                    placeholder="Enter your password"
                    {...loginForm.register('password', { required: 'Password is required' })}
                  />
                  {loginForm.formState.errors.password && (
                    <p className="text-sm text-red-600">{loginForm.formState.errors.password.message}</p>
                  )}
                </div>

                <Button 
                  type="submit" 
                  className="w-full" 
                  disabled={login.isPending}
                >
                  {login.isPending ? 'Signing in...' : 'Sign In'}
                </Button>
              </form>
            </TabsContent>

            <TabsContent value="otp">
              {!showOTPInput ? (
                <form onSubmit={otpForm.handleSubmit(handleRequestOTP)} className="space-y-4">
                  <div className="space-y-2">
                    <Label htmlFor="otp-userType">Account Type</Label>
                    <Select 
                      value={otpForm.watch('userType')} 
                      onValueChange={(value: RoleType) => otpForm.setValue('userType', value)}
                    >
                      <SelectTrigger id="otp-userType" name="userType" aria-label="Account Type for OTP">
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

                  <div className="space-y-2">
                    <Label htmlFor="otp-email">Email</Label>
                    <Input
                      id="otp-email"
                      type="email"
                      autoComplete="email"
                      placeholder="Enter your email"
                      {...otpForm.register('email', { required: 'Email is required' })}
                    />
                    {otpForm.formState.errors.email && (
                      <p className="text-sm text-red-600">{otpForm.formState.errors.email.message}</p>
                    )}
                  </div>

                  <Button 
                    type="submit" 
                    className="w-full" 
                    disabled={requestOTP.isPending}
                  >
                    {requestOTP.isPending ? 'Sending OTP...' : 'Send OTP'}
                  </Button>
                </form>
              ) : (
                <form onSubmit={otpForm.handleSubmit(handleVerifyOTP)} className="space-y-4">
                  <Alert>
                    <AlertDescription>
                      We've sent a verification code to {otpEmail}
                    </AlertDescription>
                  </Alert>

                  <div className="space-y-2">
                    <Label htmlFor="verification-code">Verification Code</Label>
                    <Input
                      id="verification-code"
                      type="text"
                      autoComplete="one-time-code"
                      placeholder="Enter 6-digit code"
                      maxLength={6}
                      {...otpForm.register('otp', { 
                        required: 'OTP is required',
                        pattern: {
                          value: /^\d{6}$/,
                          message: 'OTP must be 6 digits'
                        }
                      })}
                    />
                    {otpForm.formState.errors.otp && (
                      <p className="text-sm text-red-600">{otpForm.formState.errors.otp.message}</p>
                    )}
                  </div>

                  <div className="space-y-2">
                    <Button 
                      type="submit" 
                      className="w-full" 
                      disabled={verifyOTP.isPending}
                    >
                      {verifyOTP.isPending ? 'Verifying...' : 'Verify & Sign In'}
                    </Button>
                    
                    <Button 
                      type="button" 
                      variant="outline" 
                      className="w-full"
                      onClick={() => setShowOTPInput(false)}
                    >
                      Back
                    </Button>
                  </div>
                </form>
              )}
            </TabsContent>
          </Tabs>

          <div className="mt-6 text-center">
            <p className="text-sm text-muted-foreground">
              Don't have an account?{' '}
              <Link to="/register" className="font-medium text-primary hover:underline">
                Sign up
              </Link>
            </p>
          </div>
        </CardContent>
      </Card>
    </div>
  );
};

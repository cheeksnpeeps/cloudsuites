import { Bell, LogOut, Settings, User } from 'lucide-react';
import { Button } from '../ui/button';
import { Avatar, AvatarFallback } from '../ui/avatar';
import { Badge } from '../ui/badge';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from '../ui/dropdown-menu';
import { useAuthContext } from '../../contexts/AuthContext';
import { useLogout } from '../../hooks/useAuth';
import { RoleType } from '../../api/auth';

// Define tabs for each persona
const personaTabs: Record<RoleType, string[]> = {
  ADMIN: ['Overview', 'Companies', 'Buildings', 'Users & Roles', 'Analytics', 'System Settings'],
  STAFF: ['Overview', 'Residents', 'Units', 'Tickets', 'Amenities', 'Calendar', 'Reports'],
  OWNER: ['Dashboard', 'Units', 'Payments', 'Documents', 'Requests', 'Messages'],
  TENANT: ['Home', 'Bookings', 'Requests', 'Payments', 'Deliveries', 'Documents', 'Messages']
};

export function TopNav() {
  const { user, getUserRole } = useAuthContext();
  const logout = useLogout();

  if (!user) return null;

  const userRole = getUserRole();
  const currentTabs = userRole ? personaTabs[userRole] : [];
  const activeTab = 'Overview'; // Default active tab

  const handleLogout = () => {
    logout.mutate();
  };

  const getUserInitials = (firstName: string, lastName: string) => {
    return `${firstName.charAt(0)}${lastName.charAt(0)}`.toUpperCase();
  };

  const getRoleDisplayName = (role: RoleType) => {
    const roleNames = {
      ADMIN: 'Administrator',
      STAFF: 'Staff Member',
      OWNER: 'Property Owner',
      TENANT: 'Tenant'
    };
    return roleNames[role] || role;
  };

  return (
    <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="flex h-16 items-center justify-between px-6">
        {/* Left: Logo */}
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
            <span className="text-primary-foreground font-bold text-sm">CS</span>
          </div>
          <span className="font-semibold">CloudSuites</span>
        </div>

        {/* Center: Navigation Tabs */}
        <div className="flex items-center gap-6">
          {currentTabs.map((tab) => (
            <Button
              key={tab}
              variant="ghost"
              className={`h-8 px-4 text-sm ${
                activeTab === tab
                  ? 'text-primary border-b-2 border-primary rounded-none'
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              {tab}
            </Button>
          ))}
        </div>

        {/* Right: Actions and user */}
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" className="relative">
            <Bell className="w-4 h-4" />
            <Badge 
              variant="destructive" 
              className="absolute -top-1 -right-1 w-5 h-5 p-0 flex items-center justify-center text-xs"
            >
              3
            </Badge>
          </Button>

          <DropdownMenu>
            <DropdownMenuTrigger asChild>
              <Button variant="ghost" className="flex items-center gap-2 px-2">
                <Avatar className="w-8 h-8">
                  <AvatarFallback>
                    {getUserInitials(user.firstName, user.lastName)}
                  </AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-64">
              <div className="px-3 py-2">
                <div className="font-medium">{user.firstName} {user.lastName}</div>
                <div className="text-sm text-muted-foreground">{user.email}</div>
                <div className="text-xs text-muted-foreground mt-1">
                  {userRole && getRoleDisplayName(userRole)}
                </div>
              </div>
              <DropdownMenuSeparator />
              <DropdownMenuItem>
                <User className="w-4 h-4 mr-2" />
                Profile
              </DropdownMenuItem>
              <DropdownMenuItem>
                <Settings className="w-4 h-4 mr-2" />
                Settings
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem onClick={handleLogout} className="text-red-600">
                <LogOut className="w-4 h-4 mr-2" />
                Logout
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
}

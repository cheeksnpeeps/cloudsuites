import { Bell, Search, Moon, Sun, Settings } from 'lucide-react';
import { Button } from '../ui/button';
import { Input } from '../ui/input';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';
import { Badge } from '../ui/badge';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuSeparator, 
  DropdownMenuTrigger 
} from '../ui/dropdown-menu';
import { PersonaSwitcher } from './PersonaSwitcher';
import { useUser } from '../UserContext';
import { PersonaType } from '../types';

// Define tabs for each persona
const personaTabs = {
  admin: ['Overview', 'Buildings', 'Users & Roles', 'Integrations', 'Billing', 'Reports', 'Audit'],
  owner: ['Dashboard', 'Units', 'Payments', 'Documents', 'Requests', 'Messages'],
  staff: ['Overview', 'Residents', 'Units', 'Tickets', 'Amenities', 'Calendar', 'Incidents', 'Inventory', 'Reports', 'Settings'],
  tenant: ['Home', 'Bookings', 'Requests', 'Payments', 'Deliveries & Visitors', 'Documents', 'Messages']
};

export function TopNav() {
  const { user, darkMode, toggleDarkMode } = useUser();

  if (!user) return null;

  const currentTabs = personaTabs[user.persona as PersonaType] || [];
  const activeTab = 'Overview'; // Default active tab

  return (
    <header className="border-b bg-background/95 backdrop-blur supports-[backdrop-filter]:bg-background/60">
      <div className="flex h-16 items-center justify-between px-6">
        {/* Left: Logo */}
        <div className="flex items-center gap-2">
          <div className="w-8 h-8 bg-primary rounded-lg flex items-center justify-center">
            <span className="text-primary-foreground font-bold text-sm">PM</span>
          </div>
          <span className="font-semibold">PropertyManager</span>
        </div>

        {/* Center: Navigation Tabs */}
        <div className="flex items-center gap-6">
          {currentTabs.map((tab) => (
            <Button
              key={tab}
              variant="ghost"
              className={`h-8 px-4 text-sm ${
                tab === activeTab 
                  ? 'text-foreground border-b-2 border-primary rounded-none bg-transparent hover:bg-transparent' 
                  : 'text-muted-foreground hover:text-foreground'
              }`}
            >
              {tab}
            </Button>
          ))}
        </div>

        {/* Right: Actions and user */}
        <div className="flex items-center gap-4">
          <Button variant="ghost" size="sm" onClick={toggleDarkMode}>
            {darkMode ? <Sun className="w-4 h-4" /> : <Moon className="w-4 h-4" />}
          </Button>

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
                  <AvatarImage src={user.avatar} alt={user.name} />
                  <AvatarFallback>{user.name.split(' ').map(n => n[0]).join('')}</AvatarFallback>
                </Avatar>
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent align="end" className="w-64">
              <div className="px-3 py-2">
                <div className="font-medium">{user.name}</div>
                <div className="text-sm text-muted-foreground">{user.email}</div>
              </div>
              <DropdownMenuSeparator />
              <DropdownMenuItem>
                <Settings className="w-4 h-4 mr-2" />
                Settings
              </DropdownMenuItem>
              <DropdownMenuItem>
                Profile
              </DropdownMenuItem>
              <DropdownMenuSeparator />
              <DropdownMenuItem>
                Sign out
              </DropdownMenuItem>
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
      </div>
    </header>
  );
}
import { 
  Home, Building, Users, Wrench, Calendar, FileText, 
  DollarSign, MessageSquare, Shield, Settings, BarChart3,
  UserCheck, CreditCard, Package, Bell, MapPin
} from 'lucide-react';
import { Button } from '../ui/button';
import { ScrollArea } from '../ui/scroll-area';
import { Badge } from '../ui/badge';
import { useUser } from '../UserContext';
import { PersonaType } from '../types';

interface NavItem {
  label: string;
  icon: any;
  badge?: string;
  personas: PersonaType[];
  roles?: string[];
}

const navigationItems: NavItem[] = [
  // Admin navigation
  { label: 'Overview', icon: Home, personas: ['admin'] },
  { label: 'Buildings', icon: Building, personas: ['admin'] },
  { label: 'Users & Roles', icon: Users, personas: ['admin'] },
  { label: 'Integrations', icon: Settings, personas: ['admin'] },
  { label: 'Billing', icon: CreditCard, personas: ['admin'] },
  { label: 'Reports', icon: BarChart3, personas: ['admin'] },
  { label: 'Audit', icon: Shield, personas: ['admin'] },

  // Owner navigation  
  { label: 'Dashboard', icon: Home, personas: ['owner'] },
  { label: 'Units', icon: Building, personas: ['owner'] },
  { label: 'Payments', icon: DollarSign, personas: ['owner'] },
  { label: 'Documents', icon: FileText, personas: ['owner'] },
  { label: 'Requests', icon: Wrench, personas: ['owner'] },
  { label: 'Messages', icon: MessageSquare, personas: ['owner'] },

  // Staff navigation
  { label: 'Overview', icon: Home, personas: ['staff'] },
  { label: 'Residents', icon: Users, personas: ['staff'] },
  { label: 'Units', icon: Building, personas: ['staff'] },
  { label: 'Tickets', icon: Wrench, badge: '12', personas: ['staff'] },
  { label: 'Amenities', icon: Calendar, personas: ['staff'] },
  { label: 'Calendar', icon: Calendar, personas: ['staff'] },
  { label: 'Incidents', icon: Shield, personas: ['staff'] },
  { label: 'Inventory', icon: Package, personas: ['staff'] },
  { label: 'Reports', icon: BarChart3, personas: ['staff'] },
  { label: 'Settings', icon: Settings, personas: ['staff'] },

  // Tenant navigation
  { label: 'Home', icon: Home, personas: ['tenant'] },
  { label: 'Bookings', icon: Calendar, personas: ['tenant'] },
  { label: 'Requests', icon: Wrench, badge: '2', personas: ['tenant'] },
  { label: 'Payments', icon: DollarSign, personas: ['tenant'] },
  { label: 'Deliveries & Visitors', icon: MapPin, personas: ['tenant'] },
  { label: 'Documents', icon: FileText, personas: ['tenant'] },
  { label: 'Messages', icon: MessageSquare, badge: '5', personas: ['tenant'] },
];

const personaLabels = {
  admin: 'Admin',
  owner: 'Owner', 
  staff: 'Staff',
  tenant: 'Tenant'
};

export function Navigation() {
  const { user, setUser } = useUser();

  if (!user) return null;

  const visibleItems = navigationItems.filter(item => 
    item.personas.includes(user.persona)
  );

  const handlePersonaSwitch = (persona: PersonaType) => {
    // Create a mock user for the new persona
    const mockUsers = {
      admin: { 
        id: '1', 
        name: 'John Admin', 
        email: 'admin@example.com', 
        role: 'system_admin', 
        persona: 'admin' as PersonaType,
        avatar: '/avatars/admin.jpg'
      },
      owner: { 
        id: '2', 
        name: 'Jane Owner', 
        email: 'owner@example.com', 
        role: 'property_owner', 
        persona: 'owner' as PersonaType,
        avatar: '/avatars/owner.jpg'
      },
      staff: { 
        id: '3', 
        name: 'Mike Staff', 
        email: 'staff@example.com', 
        role: 'building_manager', 
        persona: 'staff' as PersonaType,
        avatar: '/avatars/staff.jpg'
      },
      tenant: { 
        id: '4', 
        name: 'Sarah Tenant', 
        email: 'tenant@example.com', 
        role: 'resident', 
        persona: 'tenant' as PersonaType,
        avatar: '/avatars/tenant.jpg'
      }
    };
    
    setUser(mockUsers[persona]);
  };

  return (
    <div className="w-64 bg-stone-800 text-white border-r flex flex-col">
      {/* Role Switcher */}
      <div className="p-4 border-b border-stone-700">
        <div className="space-y-1">
          {Object.entries(personaLabels).map(([persona, label]) => (
            <Button
              key={persona}
              variant={user.persona === persona ? "default" : "ghost"}
              className={`w-full justify-start h-10 ${
                user.persona === persona 
                  ? "bg-stone-700 text-white hover:bg-stone-600" 
                  : "text-stone-300 hover:text-white hover:bg-stone-700"
              }`}
              onClick={() => handlePersonaSwitch(persona as PersonaType)}
            >
              {label}
            </Button>
          ))}
        </div>
      </div>

      {/* Navigation Items */}
      <ScrollArea className="flex-1">
        <div className="p-4 space-y-2">
          {visibleItems.map((item) => {
            const Icon = item.icon;
            
            return (
              <Button
                key={item.label}
                variant="ghost"
                className="w-full justify-start gap-3 h-10 text-stone-300 hover:text-white hover:bg-stone-700"
              >
                <Icon className="w-4 h-4" />
                <span className="flex-1 text-left">{item.label}</span>
                {item.badge && (
                  <Badge variant="secondary" className="h-5 px-2 text-xs">
                    {item.badge}
                  </Badge>
                )}
              </Button>
            );
          })}
        </div>
      </ScrollArea>
    </div>
  );
}
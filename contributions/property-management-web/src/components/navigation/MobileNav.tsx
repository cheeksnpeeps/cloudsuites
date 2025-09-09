import { 
  Home, Building, Users, Wrench, Calendar, FileText, 
  DollarSign, MessageSquare, Shield, Settings, BarChart3,
  UserCheck, CreditCard, Package, Bell, MapPin
} from 'lucide-react';
import { Button } from '../ui/button';
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

const mobileNavigationItems: NavItem[] = [
  // Admin navigation (top 4)
  { label: 'Overview', icon: Home, personas: ['admin'] },
  { label: 'Buildings', icon: Building, personas: ['admin'] },
  { label: 'Users', icon: Users, personas: ['admin'] },
  { label: 'Reports', icon: BarChart3, personas: ['admin'] },

  // Owner navigation (top 4)
  { label: 'Dashboard', icon: Home, personas: ['owner'] },
  { label: 'Units', icon: Building, personas: ['owner'] },
  { label: 'Payments', icon: DollarSign, personas: ['owner'] },
  { label: 'Documents', icon: FileText, personas: ['owner'] },

  // Staff navigation (top 4)
  { label: 'Overview', icon: Home, personas: ['staff'] },
  { label: 'Residents', icon: Users, personas: ['staff'] },
  { label: 'Tickets', icon: Wrench, badge: '12', personas: ['staff'] },
  { label: 'Amenities', icon: Calendar, personas: ['staff'] },

  // Tenant navigation (top 4)
  { label: 'Home', icon: Home, personas: ['tenant'] },
  { label: 'Bookings', icon: Calendar, personas: ['tenant'] },
  { label: 'Requests', icon: Wrench, badge: '2', personas: ['tenant'] },
  { label: 'Payments', icon: DollarSign, personas: ['tenant'] },
];

export function MobileNav() {
  const { user } = useUser();

  if (!user) return null;

  const visibleItems = mobileNavigationItems
    .filter(item => item.personas.includes(user.persona))
    .slice(0, 4); // Show only top 4 items on mobile

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-background border-t md:hidden">
      <div className="grid grid-cols-4 h-16">
        {visibleItems.map((item) => {
          const Icon = item.icon;
          
          return (
            <Button
              key={item.label}
              variant="ghost"
              className="h-full rounded-none flex-col gap-1 relative p-2"
            >
              <div className="relative">
                <Icon className="w-5 h-5" />
                {item.badge && (
                  <Badge 
                    variant="destructive" 
                    className="absolute -top-2 -right-2 w-4 h-4 p-0 flex items-center justify-center text-xs"
                  >
                    {item.badge}
                  </Badge>
                )}
              </div>
              <span className="text-xs truncate w-full text-center">{item.label}</span>
            </Button>
          );
        })}
      </div>
    </div>
  );
}
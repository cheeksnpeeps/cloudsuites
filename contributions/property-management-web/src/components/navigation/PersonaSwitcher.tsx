import { useState } from 'react';
import { Check, ChevronDown, User, Users, UserCog, Building } from 'lucide-react';
import { Button } from '../ui/button';
import { Popover, PopoverContent, PopoverTrigger } from '../ui/popover';
import { useUser } from '../UserContext';
import { PersonaType } from '../types';

const personas = [
  { 
    value: 'admin' as PersonaType, 
    label: 'Admin', 
    icon: UserCog, 
    description: 'System administration' 
  },
  { 
    value: 'owner' as PersonaType, 
    label: 'Owner', 
    icon: Building, 
    description: 'Property ownership' 
  },
  { 
    value: 'staff' as PersonaType, 
    label: 'Staff', 
    icon: Users, 
    description: 'Property management' 
  },
  { 
    value: 'tenant' as PersonaType, 
    label: 'Tenant', 
    icon: User, 
    description: 'Resident portal' 
  },
];

export function PersonaSwitcher() {
  const { user, switchPersona } = useUser();
  const [open, setOpen] = useState(false);

  if (!user) return null;

  const currentPersona = personas.find(p => p.value === user.persona);

  return (
    <Popover open={open} onOpenChange={setOpen}>
      <PopoverTrigger asChild>
        <Button 
          variant="outline" 
          size="sm" 
          className="justify-between w-32"
        >
          <div className="flex items-center gap-2">
            {currentPersona && <currentPersona.icon className="w-4 h-4" />}
            {currentPersona?.label}
          </div>
          <ChevronDown className="w-4 h-4 opacity-50" />
        </Button>
      </PopoverTrigger>
      <PopoverContent className="w-64 p-2">
        <div className="space-y-1">
          {personas.map((persona) => {
            const Icon = persona.icon;
            const isSelected = user.persona === persona.value;
            
            return (
              <button
                key={persona.value}
                onClick={() => {
                  switchPersona(persona.value);
                  setOpen(false);
                }}
                className="flex items-center justify-between w-full px-3 py-2 text-left rounded-md hover:bg-muted transition-colors"
              >
                <div className="flex items-center gap-3">
                  <Icon className="w-4 h-4" />
                  <div>
                    <div className="font-medium">{persona.label}</div>
                    <div className="text-xs text-muted-foreground">
                      {persona.description}
                    </div>
                  </div>
                </div>
                {isSelected && <Check className="w-4 h-4 text-primary" />}
              </button>
            );
          })}
        </div>
      </PopoverContent>
    </Popover>
  );
}
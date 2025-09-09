import React, { createContext, useContext, useState, ReactNode } from 'react';
import { User, PersonaType } from './types';

interface UserContextType {
  user: User | null;
  setUser: (user: User | null) => void;
  switchPersona: (persona: PersonaType) => void;
  darkMode: boolean;
  toggleDarkMode: () => void;
}

const UserContext = createContext<UserContextType | undefined>(undefined);

export function UserProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>({
    id: '1',
    name: 'John Admin',
    email: 'admin@example.com',
    role: 'system_admin',
    persona: 'admin',
    avatar: 'https://images.unsplash.com/photo-1472099645785-5658abf4ff4e?w=32&h=32&fit=crop&crop=face'
  });
  
  const [darkMode, setDarkMode] = useState(false);

  const switchPersona = (persona: PersonaType) => {
    if (user) {
      // Mock role switching for demo
      let newRole = user.role;
      switch (persona) {
        case 'admin':
          newRole = 'SUPER_ADMIN';
          break;
        case 'owner':
          newRole = 'OWNER';
          break;
        case 'staff':
          newRole = 'PROPERTY_MANAGER';
          break;
        case 'tenant':
          newRole = 'TENANT';
          break;
      }
      setUser({ ...user, persona, role: newRole });
    }
  };

  const toggleDarkMode = () => {
    setDarkMode(!darkMode);
    document.documentElement.classList.toggle('dark');
  };

  return (
    <UserContext.Provider value={{ 
      user, 
      setUser, 
      switchPersona, 
      darkMode, 
      toggleDarkMode 
    }}>
      {children}
    </UserContext.Provider>
  );
}

export function useUser() {
  const context = useContext(UserContext);
  if (context === undefined) {
    throw new Error('useUser must be used within a UserProvider');
  }
  return context;
}
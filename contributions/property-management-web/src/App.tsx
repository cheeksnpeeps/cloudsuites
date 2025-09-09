import React from 'react';
import { UserProvider, useUser } from './components/UserContext';
import { TopNav } from './components/navigation/TopNav';
import { Navigation } from './components/navigation/Navigation';
import { MobileNav } from './components/navigation/MobileNav';
import { AdminDashboard } from './components/dashboards/AdminDashboard';
import { OwnerDashboard } from './components/dashboards/OwnerDashboard';
import { StaffDashboard } from './components/dashboards/StaffDashboard';
import { TenantDashboard } from './components/dashboards/TenantDashboard';
import { Demo } from './components/Demo';
import { useMobile } from './components/ui/use-mobile';

function AppContent() {
  const { user } = useUser();
  const isMobile = useMobile();
  const [showDemo, setShowDemo] = React.useState(false);

  if (!user) {
    return (
      <div className="min-h-screen flex items-center justify-center">
        <div className="text-center">
          <h1 className="text-2xl font-bold mb-4">PropertyManager</h1>
          <p className="text-muted-foreground">Loading...</p>
        </div>
      </div>
    );
  }

  const renderDashboard = () => {
    switch (user.persona) {
      case 'admin':
        return <AdminDashboard />;
      case 'owner':
        return <OwnerDashboard />;
      case 'staff':
        return <StaffDashboard />;
      case 'tenant':
        return <TenantDashboard />;
      default:
        return <TenantDashboard />;
    }
  };

  // Show demo overview first, then allow navigation to actual dashboards
  if (showDemo) {
    return (
      <div className="min-h-screen bg-background">
        <Demo />
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background">
      <TopNav />
      <div className="flex">
        {!isMobile && <Navigation />}
        <main className={`flex-1 overflow-auto ${isMobile ? 'pb-16' : ''}`}>
          {renderDashboard()}
        </main>
      </div>
      {isMobile && <MobileNav />}
    </div>
  );
}

export default function App() {
  return (
    <UserProvider>
      <AppContent />
    </UserProvider>
  );
}
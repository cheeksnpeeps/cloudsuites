import { useState } from 'react';
import { Calendar, Wrench, Users, Settings, Eye, ChevronRight } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from './ui/card';
import { Button } from './ui/button';
import { Badge } from './ui/badge';
import { Tabs, TabsContent, TabsList, TabsTrigger } from './ui/tabs';
import { AmenityBooking } from './flows/AmenityBooking';
import { ServiceRequest } from './flows/ServiceRequest';
import { PersonaSwitcher } from './navigation/PersonaSwitcher';
import { useUser } from './UserContext';

const demoFlows = [
  {
    id: 'amenity-booking',
    title: 'Amenity Booking Flow',
    description: 'Book amenities like pool, gym, rooftop terrace',
    icon: Calendar,
    personas: ['tenant'],
    steps: ['Choose amenity', 'Select date/time', 'Review booking', 'Confirmation']
  },
  {
    id: 'service-request',
    title: 'Service Request Flow',
    description: 'Submit maintenance requests with photos and scheduling',
    icon: Wrench,
    personas: ['tenant'],
    steps: ['Select category', 'Describe issue', 'Schedule access', 'Track progress']
  },
  {
    id: 'approval-queue',
    title: 'Approval Queue Management',
    description: 'Staff workflow for approving amenity bookings',
    icon: Users,
    personas: ['staff'],
    steps: ['Review request', 'Check availability', 'Approve/deny', 'Notify resident']
  },
  {
    id: 'work-orders',
    title: 'Work Order Management',
    description: 'Maintenance technician workflow',
    icon: Settings,
    personas: ['staff'],
    steps: ['Accept ticket', 'Document work', 'Update status', 'Complete request']
  }
];

const designSystemFeatures = [
  {
    title: 'Teal Accent System',
    description: 'Primary teal (#0f766e) with hover/active states',
    example: 'Buttons, links, progress indicators'
  },
  {
    title: 'Neutral Base Colors',
    description: 'Stone/gray palette for backgrounds and text',
    example: 'Cards, navigation, typography'
  },
  {
    title: 'State Colors',
    description: 'Success, warning, error colors for feedback',
    example: 'Alerts, badges, status indicators'
  },
  {
    title: 'Elevation System',
    description: 'Consistent shadow tokens for depth',
    example: 'Cards, modals, dropdowns'
  },
  {
    title: 'Responsive Design',
    description: 'Mobile-first with bottom navigation',
    example: 'Adaptive layouts, touch-friendly controls'
  },
  {
    title: 'Dark Mode Support',
    description: 'Complete dark theme implementation',
    example: 'Toggle in top navigation'
  }
];

const roleMatrix = [
  { feature: 'Amenity Booking', tenant: 'Create', staff: 'Approve', owner: 'View', admin: 'Configure' },
  { feature: 'Service Requests', tenant: 'Create/Track', staff: 'Manage', owner: 'View', admin: 'Reports' },
  { feature: 'Payments/Billing', tenant: 'Pay/View', staff: 'Process', owner: 'Statements', admin: 'Audit' },
  { feature: 'Unit Management', tenant: 'View Own', staff: 'Manage All', owner: 'View Owned', admin: 'Configure' },
  { feature: 'User Management', tenant: '-', staff: 'Residents', owner: '-', admin: 'All Users' },
  { feature: 'Reports', tenant: 'Personal', staff: 'Operational', owner: 'Financial', admin: 'System-wide' }
];

export function Demo() {
  const [activeFlow, setActiveFlow] = useState('overview');
  const { user, darkMode, toggleDarkMode } = useUser();

  if (activeFlow === 'amenity-booking') {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between p-6 border-b">
          <Button variant="outline" onClick={() => setActiveFlow('overview')}>
            ← Back to Overview
          </Button>
          <Badge variant="secondary">Demo Flow</Badge>
        </div>
        <div className="px-6">
          <AmenityBooking />
        </div>
      </div>
    );
  }

  if (activeFlow === 'service-request') {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between p-6 border-b">
          <Button variant="outline" onClick={() => setActiveFlow('overview')}>
            ← Back to Overview
          </Button>
          <Badge variant="secondary">Demo Flow</Badge>
        </div>
        <div className="px-6">
          <ServiceRequest />
        </div>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="p-6 border-b">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-2xl font-bold">PropertyManager Design System</h1>
            <p className="text-muted-foreground">
              Modern property management with role-based experiences
            </p>
          </div>
          <div className="flex items-center gap-4">
            <PersonaSwitcher />
            <Button variant="outline" onClick={toggleDarkMode}>
              {darkMode ? '☀️' : '🌙'} Theme
            </Button>
          </div>
        </div>
        
        <div className="flex items-center gap-2 text-sm text-muted-foreground">
          <Eye className="w-4 h-4" />
          Current view: {user?.persona} dashboard ({user?.role?.replace('_', ' ').toLowerCase()})
        </div>
      </div>

      <div className="p-6">
        <Tabs defaultValue="flows" className="space-y-6">
          <TabsList className="grid w-full grid-cols-3">
            <TabsTrigger value="flows">Key Flows</TabsTrigger>
            <TabsTrigger value="design-system">Design System</TabsTrigger>
            <TabsTrigger value="role-matrix">Role Matrix</TabsTrigger>
          </TabsList>

          <TabsContent value="flows" className="space-y-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Critical User Flows</h2>
              <p className="text-muted-foreground mb-6">
                Experience the core workflows for each persona type. Switch personas using the dropdown above to see different dashboards.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
              {demoFlows.map((flow) => {
                const Icon = flow.icon;
                const isAvailable = flow.personas.includes(user?.persona || 'tenant');
                
                return (
                  <Card 
                    key={flow.id} 
                    className={`${isAvailable ? 'cursor-pointer hover:shadow-md' : 'opacity-50'} transition-shadow`}
                    onClick={() => isAvailable && setActiveFlow(flow.id)}
                  >
                    <CardHeader>
                      <div className="flex items-start justify-between">
                        <div className="flex items-center gap-3">
                          <div className="w-10 h-10 bg-primary rounded-lg flex items-center justify-center">
                            <Icon className="w-5 h-5 text-primary-foreground" />
                          </div>
                          <div>
                            <CardTitle className="text-lg">{flow.title}</CardTitle>
                            <p className="text-sm text-muted-foreground mt-1">
                              {flow.description}
                            </p>
                          </div>
                        </div>
                        {isAvailable && <ChevronRight className="w-5 h-5 text-muted-foreground" />}
                      </div>
                    </CardHeader>
                    <CardContent>
                      <div className="space-y-3">
                        <div className="flex flex-wrap gap-1">
                          {flow.personas.map((persona) => (
                            <Badge 
                              key={persona} 
                              variant={persona === user?.persona ? 'default' : 'secondary'}
                              className="text-xs"
                            >
                              {persona}
                            </Badge>
                          ))}
                        </div>
                        
                        <div className="text-sm text-muted-foreground">
                          <span className="font-medium">Steps:</span> {flow.steps.join(' → ')}
                        </div>
                        
                        {!isAvailable && (
                          <p className="text-xs text-muted-foreground">
                            Switch to {flow.personas.join(' or ')} persona to try this flow
                          </p>
                        )}
                      </div>
                    </CardContent>
                  </Card>
                );
              })}
            </div>
          </TabsContent>

          <TabsContent value="design-system" className="space-y-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Design System Overview</h2>
              <p className="text-muted-foreground mb-6">
                Built with neutral base colors + teal accent, supporting both light and dark modes with comprehensive component library.
              </p>
            </div>

            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              {designSystemFeatures.map((feature, index) => (
                <Card key={index}>
                  <CardContent className="p-4">
                    <h3 className="font-medium mb-2">{feature.title}</h3>
                    <p className="text-sm text-muted-foreground mb-2">{feature.description}</p>
                    <p className="text-xs text-muted-foreground italic">{feature.example}</p>
                  </CardContent>
                </Card>
              ))}
            </div>

            <Card>
              <CardHeader>
                <CardTitle>Component Examples</CardTitle>
              </CardHeader>
              <CardContent className="space-y-4">
                <div className="flex flex-wrap gap-2">
                  <Button>Primary</Button>
                  <Button variant="secondary">Secondary</Button>
                  <Button variant="outline">Outline</Button>
                  <Button variant="ghost">Ghost</Button>
                </div>
                
                <div className="flex flex-wrap gap-2">
                  <Badge>Default</Badge>
                  <Badge variant="secondary">Secondary</Badge>
                  <Badge variant="destructive">Destructive</Badge>
                  <Badge variant="outline">Outline</Badge>
                </div>
                
                <div className="space-y-2">
                  <div className="w-full bg-primary h-2 rounded-full"></div>
                  <div className="w-3/4 bg-success h-2 rounded-full"></div>
                  <div className="w-1/2 bg-warning h-2 rounded-full"></div>
                  <div className="w-1/4 bg-destructive h-2 rounded-full"></div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="role-matrix" className="space-y-6">
            <div>
              <h2 className="text-xl font-semibold mb-4">Role-Based Permissions</h2>
              <p className="text-muted-foreground mb-6">
                Each persona has different access levels and capabilities within the system.
              </p>
            </div>

            <Card>
              <CardContent className="p-0">
                <div className="overflow-x-auto">
                  <table className="w-full">
                    <thead className="border-b">
                      <tr>
                        <th className="text-left p-4 font-medium">Feature</th>
                        <th className="text-left p-4 font-medium">Tenant</th>
                        <th className="text-left p-4 font-medium">Staff</th>
                        <th className="text-left p-4 font-medium">Owner</th>
                        <th className="text-left p-4 font-medium">Admin</th>
                      </tr>
                    </thead>
                    <tbody>
                      {roleMatrix.map((row, index) => (
                        <tr key={index} className="border-b last:border-b-0">
                          <td className="p-4 font-medium">{row.feature}</td>
                          <td className="p-4 text-sm">{row.tenant}</td>
                          <td className="p-4 text-sm">{row.staff}</td>
                          <td className="p-4 text-sm">{row.owner}</td>
                          <td className="p-4 text-sm">{row.admin}</td>
                        </tr>
                      ))}
                    </tbody>
                  </table>
                </div>
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
}
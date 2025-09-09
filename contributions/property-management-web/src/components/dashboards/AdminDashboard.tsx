import { Plus, CheckCircle, Ticket, Calendar, DollarSign, Building2, UserPlus, BarChart3, BarChart2 } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Progress } from '../ui/progress';

// KPI Cards Data
const kpiStats = [
  {
    title: 'Occupancy rate',
    value: '85%',
    icon: CheckCircle,
    color: 'text-primary'
  },
  {
    title: 'Open tickets',
    value: '5',
    icon: Ticket,
    color: 'text-muted-foreground'
  },
  {
    title: 'Upcoming bookings',
    value: '45',
    icon: Calendar,
    color: 'text-muted-foreground'
  },
  {
    title: 'Delinquent payments',
    value: '$12,345',
    icon: DollarSign,
    color: 'text-muted-foreground'
  }
];

// Alerts Data
const alerts = [
  {
    id: '1',
    message: 'Amenity booking conflict for tomorrow',
    time: '12:30 PM'
  },
  {
    id: '2',
    message: 'Lease expiring on May 1st',
    time: '9:15 AM'
  }
];

// Quick Actions Data
const quickActions = [
  {
    label: 'Add Building',
    icon: Building2
  },
  {
    label: 'Invite Staff',
    icon: UserPlus
  },
  {
    label: 'Run Report',
    icon: BarChart3
  }
];

// Recent Activity Data
const recentActivity = [
  {
    id: '1',
    message: 'Amenity booking approved',
    time: '15 min ago'
  },
  {
    id: '2',
    message: 'Staff role assigned',
    time: '1 hour ago'
  },
  {
    id: '3',
    message: 'New lease signed',
    time: 'Today'
  }
];

export function AdminDashboard() {
  return (
    <div className="p-6 space-y-6">
      <div>
        <h1>Dashboard</h1>
      </div>

      {/* KPI Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
        {kpiStats.map((stat) => {
          const Icon = stat.icon;
          return (
            <Card key={stat.title}>
              <CardContent className="p-6">
                <div className="flex items-center gap-2">
                  <Icon className={`w-5 h-5 ${stat.color}`} />
                  <div className="text-2xl font-bold">{stat.value}</div>
                </div>
                <p className="text-sm text-muted-foreground mt-2">
                  {stat.title}
                </p>
              </CardContent>
            </Card>
          );
        })}
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Alerts */}
        <Card>
          <CardHeader>
            <CardTitle>Alerts</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {alerts.map((alert) => (
                <div key={alert.id} className="space-y-1">
                  <p className="text-sm">{alert.message}</p>
                  <p className="text-xs text-muted-foreground">{alert.time}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>

        {/* Quick Actions */}
        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-3">
              {quickActions.map((action) => {
                const Icon = action.icon;
                return (
                  <Button
                    key={action.label}
                    variant="outline"
                    className="w-full justify-start gap-3"
                  >
                    <Icon className="w-4 h-4" />
                    {action.label}
                  </Button>
                );
              })}
            </div>
          </CardContent>
        </Card>
      </div>

      <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
        {/* Building Stats */}
        <Card>
          <CardHeader>
            <CardTitle>Building Stats</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-6">
              <div>
                <p className="font-medium mb-4">Building A</p>
                
                <div className="space-y-4">
                  {/* SLA Progress */}
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <BarChart2 className="w-4 h-4 text-primary" />
                      <span className="text-sm">SLA</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Progress value={92} className="w-16 h-2" />
                      <span className="text-sm font-medium">92 %</span>
                    </div>
                  </div>

                  {/* Repairs Progress */}
                  <div className="flex items-center justify-between">
                    <div className="flex items-center gap-3">
                      <div className="w-4 h-4 bg-primary rounded-sm" />
                      <span className="text-sm">Repairs</span>
                    </div>
                    <div className="flex items-center gap-2">
                      <Progress value={92} className="w-16 h-2" />
                      <span className="text-sm font-medium">92 %</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Recent Activity */}
        <Card>
          <CardHeader>
            <CardTitle>Recent Activity</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentActivity.map((activity) => (
                <div key={activity.id} className="flex items-center justify-between">
                  <p className="text-sm">{activity.message}</p>
                  <p className="text-xs text-muted-foreground">{activity.time}</p>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
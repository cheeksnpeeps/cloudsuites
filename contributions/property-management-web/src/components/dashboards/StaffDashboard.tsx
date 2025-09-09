import { Plus, Calendar, Users, Wrench, TrendingUp, AlertTriangle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageHeader } from '../layout/PageHeader';
import { Progress } from '../ui/progress';
import { useUser } from '../UserContext';

const getStaffStats = (role: string) => {
  const baseStats = [
    { title: 'Active Residents', value: '342', change: '+5 this week', icon: Users },
    { title: 'Occupancy Rate', value: '94%', change: '+2% vs last month', icon: TrendingUp },
  ];

  if (role === 'PROPERTY_MANAGER') {
    return [
      ...baseStats,
      { title: 'Open Tickets', value: '23', change: '-3 from yesterday', icon: Wrench },
      { title: 'Pending Approvals', value: '7', change: '2 urgent', icon: AlertTriangle },
    ];
  }

  if (role === 'MAINTENANCE_TECHNICIAN') {
    return [
      { title: 'My Work Orders', value: '8', change: '3 urgent', icon: Wrench },
      { title: 'Completed Today', value: '5', change: '+2 vs yesterday', icon: TrendingUp },
      { title: 'Parts Inventory', value: '89%', change: 'Well stocked', icon: Users },
      { title: 'Avg Response Time', value: '2.3h', change: '-15min improvement', icon: AlertTriangle },
    ];
  }

  return baseStats;
};

const upcomingTasks = [
  { id: '1', title: 'Amenity booking approval', type: 'approval', priority: 'medium', time: '30 min' },
  { id: '2', title: 'Inspect Unit 15A before move-in', type: 'inspection', priority: 'high', time: '2 hours' },
  { id: '3', title: 'Resident complaint follow-up', type: 'communication', priority: 'medium', time: '4 hours' },
  { id: '4', title: 'Monthly building report', type: 'report', priority: 'low', time: 'Tomorrow' },
];

const recentTickets = [
  { id: '1', unit: '12B', category: 'Plumbing', title: 'Kitchen sink leak', priority: 'high', status: 'assigned', assignee: 'Mike Chen' },
  { id: '2', unit: '8A', category: 'Electrical', title: 'Outlet not working', priority: 'medium', status: 'in_progress', assignee: 'Sarah Johnson' },
  { id: '3', unit: '5C', category: 'HVAC', title: 'AC not cooling', priority: 'urgent', status: 'open', assignee: null },
  { id: '4', unit: '22F', category: 'General', title: 'Lightbulb replacement', priority: 'low', status: 'completed', assignee: 'Mike Chen' },
];

const amenityRequests = [
  { id: '1', resident: 'John Smith', amenity: 'Rooftop Terrace', date: 'March 15', time: '6:00 PM - 9:00 PM', guests: 8, status: 'pending' },
  { id: '2', resident: 'Emily Johnson', amenity: 'Conference Room', date: 'March 16', time: '2:00 PM - 4:00 PM', guests: 6, status: 'pending' },
  { id: '3', resident: 'David Chen', amenity: 'Pool Area', date: 'March 17', time: '1:00 PM - 3:00 PM', guests: 4, status: 'approved' },
];

export function StaffDashboard() {
  const { user } = useUser();
  
  if (!user) return null;

  const stats = getStaffStats(user.role);
  const isPropertyManager = user.role === 'PROPERTY_MANAGER';
  const isMaintenance = user.role === 'MAINTENANCE_TECHNICIAN';

  return (
    <div className="space-y-6">
      <PageHeader
        title={isPropertyManager ? "Property Overview" : isMaintenance ? "Work Orders" : "Staff Dashboard"}
        subtitle="Riverside Towers • Downtown Plaza"
        actions={
          <div className="flex gap-2">
            <Button variant="outline">
              <Calendar className="w-4 h-4 mr-2" />
              Schedule
            </Button>
            <Button>
              <Plus className="w-4 h-4 mr-2" />
              {isMaintenance ? "Create Work Order" : "Quick Create"}
            </Button>
          </div>
        }
      />

      <div className="p-6 space-y-6">
        {/* Stats Cards */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          {stats.map((stat) => {
            const Icon = stat.icon;
            return (
              <Card key={stat.title}>
                <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
                  <CardTitle className="text-sm font-medium text-muted-foreground">
                    {stat.title}
                  </CardTitle>
                  <Icon className="w-4 h-4 text-muted-foreground" />
                </CardHeader>
                <CardContent>
                  <div className="text-2xl font-bold">{stat.value}</div>
                  <p className="text-xs text-muted-foreground mt-1">
                    {stat.change}
                  </p>
                </CardContent>
              </Card>
            );
          })}
        </div>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Upcoming Tasks */}
          <Card>
            <CardHeader>
              <CardTitle>Upcoming Tasks</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {upcomingTasks.map((task) => (
                  <div key={task.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{task.title}</span>
                        <Badge 
                          variant={
                            task.priority === 'high' ? 'destructive' :
                            task.priority === 'medium' ? 'secondary' : 'outline'
                          }
                        >
                          {task.priority}
                        </Badge>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {task.type} • Due in {task.time}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Recent Tickets */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Recent Service Requests</CardTitle>
              <Button variant="outline" size="sm">View All</Button>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {recentTickets.map((ticket) => (
                  <div key={ticket.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">Unit {ticket.unit}</span>
                        <Badge 
                          variant={
                            ticket.priority === 'urgent' ? 'destructive' :
                            ticket.priority === 'high' ? 'destructive' :
                            ticket.priority === 'medium' ? 'secondary' : 'outline'
                          }
                        >
                          {ticket.priority}
                        </Badge>
                      </div>
                      <div className="text-sm">{ticket.title}</div>
                      <div className="text-xs text-muted-foreground">
                        {ticket.category} • {ticket.assignee || 'Unassigned'}
                      </div>
                    </div>
                    <Badge 
                      variant={
                        ticket.status === 'completed' ? 'default' :
                        ticket.status === 'in_progress' ? 'secondary' : 'outline'
                      }
                    >
                      {ticket.status.replace('_', ' ')}
                    </Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Amenity Approval Queue - Property Manager only */}
        {isPropertyManager && (
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Amenity Booking Approvals</CardTitle>
              <Badge variant="secondary">{amenityRequests.filter(r => r.status === 'pending').length} pending</Badge>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {amenityRequests.map((request) => (
                  <div key={request.id} className="flex items-center justify-between p-4 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{request.resident}</span>
                        <span className="text-sm text-muted-foreground">•</span>
                        <span className="text-sm">{request.amenity}</span>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {request.date} • {request.time} • {request.guests} guests
                      </div>
                    </div>
                    <div className="flex items-center gap-2">
                      <Badge 
                        variant={request.status === 'approved' ? 'default' : 'secondary'}
                      >
                        {request.status}
                      </Badge>
                      {request.status === 'pending' && (
                        <div className="flex gap-1">
                          <Button size="sm" variant="outline">Deny</Button>
                          <Button size="sm">Approve</Button>
                        </div>
                      )}
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        )}
      </div>
    </div>
  );
}
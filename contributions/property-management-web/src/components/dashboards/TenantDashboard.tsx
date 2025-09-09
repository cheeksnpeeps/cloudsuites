import { Plus, Calendar, Wrench, CreditCard, MapPin, MessageSquare } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageHeader } from '../layout/PageHeader';
import { Avatar, AvatarFallback, AvatarImage } from '../ui/avatar';

const quickActions = [
  { label: 'Book Amenity', icon: Calendar, color: 'bg-primary' },
  { label: 'Pay Rent', icon: CreditCard, color: 'bg-success' },
  { label: 'Service Request', icon: Wrench, color: 'bg-warning' },
  { label: 'Invite Visitor', icon: MapPin, color: 'bg-chart-2' },
];

const upcomingBookings = [
  { id: '1', amenity: 'Pool', date: 'Today', time: '3:00 PM - 4:00 PM', status: 'confirmed' },
  { id: '2', amenity: 'Gym', date: 'Tomorrow', time: '7:00 AM - 8:00 AM', status: 'confirmed' },
  { id: '3', amenity: 'Rooftop Terrace', date: 'Friday', time: '6:00 PM - 9:00 PM', status: 'pending' },
];

const openRequests = [
  { id: '1', category: 'Plumbing', title: 'Kitchen sink leak', status: 'in_progress', priority: 'medium', date: '2 days ago' },
  { id: '2', category: 'HVAC', title: 'AC not cooling properly', status: 'open', priority: 'high', date: '1 day ago' },
];

const announcements = [
  { 
    id: '1', 
    title: 'Pool Maintenance Scheduled', 
    content: 'The pool will be closed for routine maintenance on Friday, March 15th.',
    author: 'Property Management',
    date: '2 hours ago',
    important: true
  },
  { 
    id: '2', 
    title: 'New Gym Equipment Installed', 
    content: 'We\'ve added new cardio equipment to the fitness center. Enjoy!',
    author: 'Property Management', 
    date: '1 day ago',
    important: false
  },
];

export function TenantDashboard() {
  return (
    <div className="space-y-6">
      <PageHeader
        title="Welcome back, John!"
        subtitle="Unit 12B • Riverside Towers"
        badge="Rent Due: March 1st"
        badgeVariant="outline"
      />

      <div className="p-6 space-y-6">
        {/* Unit Summary Card */}
        <Card>
          <CardContent className="p-6">
            <div className="flex items-center justify-between">
              <div className="space-y-2">
                <h3 className="font-semibold">Your Unit</h3>
                <div className="space-y-1">
                  <p className="text-sm text-muted-foreground">Unit 12B • 2 Bed, 2 Bath • 1,200 sq ft</p>
                  <p className="text-sm text-muted-foreground">Lease expires: December 31, 2024</p>
                </div>
              </div>
              <div className="text-right">
                <div className="text-2xl font-bold">$2,800</div>
                <p className="text-sm text-muted-foreground">Monthly rent</p>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Quick Actions */}
        <Card>
          <CardHeader>
            <CardTitle>Quick Actions</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="grid grid-cols-2 md:grid-cols-4 gap-4">
              {quickActions.map((action) => {
                const Icon = action.icon;
                return (
                  <Button
                    key={action.label}
                    variant="outline"
                    className="h-20 flex-col gap-2"
                  >
                    <div className={`w-8 h-8 ${action.color} rounded-lg flex items-center justify-center`}>
                      <Icon className="w-4 h-4 text-white" />
                    </div>
                    <span className="text-xs">{action.label}</span>
                  </Button>
                );
              })}
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Upcoming Bookings */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Upcoming Bookings</CardTitle>
              <Button variant="outline" size="sm">
                <Plus className="w-4 h-4 mr-2" />
                Book
              </Button>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {upcomingBookings.map((booking) => (
                  <div key={booking.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{booking.amenity}</span>
                        <Badge 
                          variant={booking.status === 'confirmed' ? 'default' : 'secondary'}
                        >
                          {booking.status}
                        </Badge>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {booking.date} • {booking.time}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Open Requests */}
          <Card>
            <CardHeader className="flex flex-row items-center justify-between">
              <CardTitle>Service Requests</CardTitle>
              <Button variant="outline" size="sm">
                <Plus className="w-4 h-4 mr-2" />
                New Request
              </Button>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {openRequests.map((request) => (
                  <div key={request.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{request.title}</span>
                        <Badge 
                          variant={request.priority === 'high' ? 'destructive' : 'secondary'}
                        >
                          {request.priority}
                        </Badge>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {request.category} • {request.date}
                      </div>
                    </div>
                    <Badge variant="outline">
                      {request.status.replace('_', ' ')}
                    </Badge>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Announcements */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Announcements</CardTitle>
            <Button variant="ghost" size="sm">
              <MessageSquare className="w-4 h-4 mr-2" />
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {announcements.map((announcement) => (
                <div key={announcement.id} className="space-y-3 p-4 border rounded-lg">
                  <div className="flex items-start justify-between">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <h4 className="font-medium">{announcement.title}</h4>
                        {announcement.important && (
                          <Badge variant="destructive" className="text-xs">Important</Badge>
                        )}
                      </div>
                      <p className="text-sm text-muted-foreground">{announcement.content}</p>
                    </div>
                  </div>
                  <div className="flex items-center gap-2 text-xs text-muted-foreground">
                    <Avatar className="w-4 h-4">
                      <AvatarFallback className="text-xs">PM</AvatarFallback>
                    </Avatar>
                    <span>{announcement.author}</span>
                    <span>•</span>
                    <span>{announcement.date}</span>
                  </div>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
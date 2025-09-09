import { Download, FileText, DollarSign, TrendingUp, Home } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { PageHeader } from '../layout/PageHeader';
import { Progress } from '../ui/progress';
import { LineChart, Line, XAxis, YAxis, CartesianGrid, ResponsiveContainer } from 'recharts';

const ownershipSummary = {
  totalUnits: 8,
  occupiedUnits: 7,
  monthlyRent: 22400,
  yearToDateIncome: 201600,
  expenses: 45600,
  netIncome: 156000
};

const units = [
  { id: '1', building: 'Riverside Towers', unit: '12B', tenant: 'John Smith', rent: 2800, lease: 'Dec 2024', status: 'occupied' },
  { id: '2', building: 'Riverside Towers', unit: '15A', tenant: 'Sarah Johnson', rent: 2900, lease: 'Nov 2024', status: 'occupied' },
  { id: '3', building: 'Downtown Plaza', unit: '8C', tenant: 'Mike Chen', rent: 2600, lease: 'Jan 2025', status: 'occupied' },
  { id: '4', building: 'Downtown Plaza', unit: '22F', tenant: 'Emily Davis', rent: 2700, lease: 'Mar 2025', status: 'occupied' },
  { id: '5', building: 'Garden Heights', unit: '5B', tenant: 'David Wilson', rent: 2400, lease: 'Aug 2024', status: 'occupied' },
  { id: '6', building: 'Garden Heights', unit: '10A', tenant: 'Lisa Brown', rent: 2500, lease: 'Sep 2024', status: 'occupied' },
  { id: '7', building: 'Metro Square', unit: '18D', tenant: 'Alex Kim', rent: 3200, lease: 'Feb 2025', status: 'occupied' },
  { id: '8', building: 'Metro Square', unit: '25B', tenant: null, rent: 3000, lease: null, status: 'vacant' },
];

const incomeData = [
  { month: 'Jan', income: 22400, expenses: 3800 },
  { month: 'Feb', income: 22400, expenses: 4200 },
  { month: 'Mar', income: 22400, expenses: 3600 },
  { month: 'Apr', income: 22400, expenses: 4000 },
  { month: 'May', income: 22400, expenses: 3900 },
  { month: 'Jun', income: 22400, expenses: 4100 },
  { month: 'Jul', income: 22400, expenses: 3700 },
  { month: 'Aug', income: 22400, expenses: 3800 },
  { month: 'Sep', income: 19400, expenses: 4500 }, // Vacancy
];

const upcomingPayments = [
  { id: '1', type: 'Property Tax', amount: 12500, due: 'March 31', status: 'pending' },
  { id: '2', type: 'Insurance Premium', amount: 4200, due: 'April 15', status: 'pending' },
  { id: '3', type: 'Maintenance Reserve', amount: 2800, due: 'April 1', status: 'paid' },
];

const recentDocuments = [
  { id: '1', name: 'Q1 2024 Financial Statement', type: 'Financial', date: 'March 1', size: '2.4 MB' },
  { id: '2', name: 'Lease Agreement - Unit 12B Renewal', type: 'Legal', date: 'February 28', size: '1.8 MB' },
  { id: '3', name: 'Property Insurance Policy', type: 'Insurance', date: 'February 25', size: '3.2 MB' },
  { id: '4', name: 'Annual Inspection Report', type: 'Maintenance', date: 'February 20', size: '5.1 MB' },
];

export function OwnerDashboard() {
  const occupancyRate = (ownershipSummary.occupiedUnits / ownershipSummary.totalUnits) * 100;

  return (
    <div className="space-y-6">
      <PageHeader
        title="Portfolio Overview"
        subtitle="8 units across 4 buildings"
        actions={
          <div className="flex gap-2">
            <Button variant="outline">
              <Download className="w-4 h-4 mr-2" />
              Export Report
            </Button>
            <Button variant="outline">
              <FileText className="w-4 h-4 mr-2" />
              View Statements
            </Button>
          </div>
        }
      />

      <div className="p-6 space-y-6">
        {/* Financial Summary */}
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-6">
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Monthly Rent Income
              </CardTitle>
              <DollarSign className="w-4 h-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${ownershipSummary.monthlyRent.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground mt-1">
                From {ownershipSummary.occupiedUnits} occupied units
              </p>
            </CardContent>
          </Card>
          
          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                YTD Net Income
              </CardTitle>
              <TrendingUp className="w-4 h-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${ownershipSummary.netIncome.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground mt-1">
                +12% vs last year
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                Occupancy Rate
              </CardTitle>
              <Home className="w-4 h-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">{occupancyRate.toFixed(0)}%</div>
              <p className="text-xs text-muted-foreground mt-1">
                {ownershipSummary.occupiedUnits} of {ownershipSummary.totalUnits} units
              </p>
            </CardContent>
          </Card>

          <Card>
            <CardHeader className="flex flex-row items-center justify-between space-y-0 pb-2">
              <CardTitle className="text-sm font-medium text-muted-foreground">
                YTD Expenses
              </CardTitle>
              <FileText className="w-4 h-4 text-muted-foreground" />
            </CardHeader>
            <CardContent>
              <div className="text-2xl font-bold">${ownershipSummary.expenses.toLocaleString()}</div>
              <p className="text-xs text-muted-foreground mt-1">
                18% of gross income
              </p>
            </CardContent>
          </Card>
        </div>

        {/* Income Chart */}
        <Card>
          <CardHeader>
            <CardTitle>Income vs Expenses</CardTitle>
          </CardHeader>
          <CardContent>
            <div className="h-64">
              <ResponsiveContainer width="100%" height="100%">
                <LineChart data={incomeData}>
                  <CartesianGrid strokeDasharray="3 3" />
                  <XAxis dataKey="month" />
                  <YAxis />
                  <Line 
                    type="monotone" 
                    dataKey="income" 
                    stroke="hsl(var(--primary))" 
                    strokeWidth={2}
                    name="Income"
                  />
                  <Line 
                    type="monotone" 
                    dataKey="expenses" 
                    stroke="hsl(var(--destructive))" 
                    strokeWidth={2}
                    name="Expenses"
                  />
                </LineChart>
              </ResponsiveContainer>
            </div>
          </CardContent>
        </Card>

        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {/* Unit Portfolio */}
          <Card>
            <CardHeader>
              <CardTitle>Unit Portfolio</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {units.map((unit) => (
                  <div key={unit.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{unit.building}</span>
                        <span className="text-sm text-muted-foreground">Unit {unit.unit}</span>
                        <Badge 
                          variant={unit.status === 'occupied' ? 'default' : 'secondary'}
                        >
                          {unit.status}
                        </Badge>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        {unit.tenant || 'Vacant'} • ${unit.rent.toLocaleString()}/month
                        {unit.lease && ` • Lease until ${unit.lease}`}
                      </div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>

          {/* Upcoming Payments */}
          <Card>
            <CardHeader>
              <CardTitle>Upcoming Payments</CardTitle>
            </CardHeader>
            <CardContent>
              <div className="space-y-4">
                {upcomingPayments.map((payment) => (
                  <div key={payment.id} className="flex items-center justify-between p-3 border rounded-lg">
                    <div className="space-y-1">
                      <div className="flex items-center gap-2">
                        <span className="font-medium">{payment.type}</span>
                        <Badge 
                          variant={payment.status === 'paid' ? 'default' : 'secondary'}
                        >
                          {payment.status}
                        </Badge>
                      </div>
                      <div className="text-sm text-muted-foreground">
                        Due {payment.due}
                      </div>
                    </div>
                    <div className="text-right">
                      <div className="font-medium">${payment.amount.toLocaleString()}</div>
                    </div>
                  </div>
                ))}
              </div>
            </CardContent>
          </Card>
        </div>

        {/* Recent Documents */}
        <Card>
          <CardHeader className="flex flex-row items-center justify-between">
            <CardTitle>Recent Documents</CardTitle>
            <Button variant="outline" size="sm">
              <FileText className="w-4 h-4 mr-2" />
              View All
            </Button>
          </CardHeader>
          <CardContent>
            <div className="space-y-4">
              {recentDocuments.map((doc) => (
                <div key={doc.id} className="flex items-center justify-between p-3 border rounded-lg">
                  <div className="flex items-center gap-3">
                    <FileText className="w-8 h-8 text-muted-foreground" />
                    <div className="space-y-1">
                      <div className="font-medium">{doc.name}</div>
                      <div className="text-sm text-muted-foreground">
                        {doc.type} • {doc.date} • {doc.size}
                      </div>
                    </div>
                  </div>
                  <Button variant="ghost" size="sm">
                    <Download className="w-4 h-4" />
                  </Button>
                </div>
              ))}
            </div>
          </CardContent>
        </Card>
      </div>
    </div>
  );
}
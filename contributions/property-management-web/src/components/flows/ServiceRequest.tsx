import { useState } from 'react';
import { Camera, Upload, CheckCircle, Clock, AlertCircle } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Textarea } from '../ui/textarea';
import { RadioGroup, RadioGroupItem } from '../ui/radio-group';
import { Checkbox } from '../ui/checkbox';

const categories = [
  { id: 'plumbing', label: 'Plumbing', description: 'Leaks, clogs, water pressure issues' },
  { id: 'electrical', label: 'Electrical', description: 'Outlets, lighting, circuit breakers' },
  { id: 'hvac', label: 'HVAC', description: 'Heating, cooling, ventilation' },
  { id: 'appliances', label: 'Appliances', description: 'Refrigerator, dishwasher, washer/dryer' },
  { id: 'maintenance', label: 'General Maintenance', description: 'Painting, locks, fixtures' },
  { id: 'pest', label: 'Pest Control', description: 'Insects, rodents, prevention' },
  { id: 'safety', label: 'Safety/Security', description: 'Smoke detectors, locks, alarms' },
  { id: 'other', label: 'Other', description: 'Anything not listed above' }
];

const timePreferences = [
  { id: 'morning', label: 'Morning (8 AM - 12 PM)' },
  { id: 'afternoon', label: 'Afternoon (12 PM - 5 PM)' },
  { id: 'evening', label: 'Evening (5 PM - 8 PM)' },
  { id: 'anytime', label: 'Anytime during business hours' }
];

export function ServiceRequest() {
  const [step, setStep] = useState(1);
  const [category, setCategory] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [priority, setPriority] = useState('medium');
  const [preferredTimes, setPreferredTimes] = useState([]);
  const [allowEntry, setAllowEntry] = useState(false);
  const [photos, setPhotos] = useState([]);
  const [requestId, setRequestId] = useState('');

  const handleCategorySelect = (categoryId) => {
    setCategory(categoryId);
    setStep(2);
  };

  const handleTimePreferenceChange = (timeId, checked) => {
    if (checked) {
      setPreferredTimes([...preferredTimes, timeId]);
    } else {
      setPreferredTimes(preferredTimes.filter(id => id !== timeId));
    }
  };

  const handleSubmit = () => {
    // Mock request creation
    const newRequestId = `SR-${Date.now()}`;
    setRequestId(newRequestId);
    console.log('Service request created:', {
      id: newRequestId,
      category,
      title,
      description,
      priority,
      preferredTimes,
      allowEntry,
      photos
    });
    setStep(4);
  };

  const resetForm = () => {
    setStep(1);
    setCategory('');
    setTitle('');
    setDescription('');
    setPriority('medium');
    setPreferredTimes([]);
    setAllowEntry(false);
    setPhotos([]);
    setRequestId('');
  };

  if (step === 1) {
    return (
      <div className="space-y-6">
        <div>
          <h2 className="text-xl font-semibold mb-2">Create Service Request</h2>
          <p className="text-muted-foreground">What type of issue are you experiencing?</p>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          {categories.map((cat) => (
            <Card 
              key={cat.id} 
              className="cursor-pointer hover:shadow-md transition-shadow"
              onClick={() => handleCategorySelect(cat.id)}
            >
              <CardContent className="p-4">
                <h3 className="font-medium mb-1">{cat.label}</h3>
                <p className="text-sm text-muted-foreground">{cat.description}</p>
              </CardContent>
            </Card>
          ))}
        </div>
      </div>
    );
  }

  if (step === 2) {
    const selectedCategory = categories.find(c => c.id === category);
    
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold">Request Details</h2>
            <p className="text-muted-foreground">
              Category: {selectedCategory?.label}
            </p>
          </div>
          <Button variant="outline" onClick={() => setStep(1)}>
            Back
          </Button>
        </div>

        <Card>
          <CardContent className="p-6 space-y-6">
            <div>
              <Label htmlFor="title">Issue Title *</Label>
              <Input
                id="title"
                placeholder="Brief description of the issue"
                value={title}
                onChange={(e) => setTitle(e.target.value)}
              />
            </div>

            <div>
              <Label htmlFor="description">Detailed Description *</Label>
              <Textarea
                id="description"
                placeholder="Please provide as much detail as possible about the issue, including when it started and any relevant circumstances..."
                value={description}
                onChange={(e) => setDescription(e.target.value)}
                rows={4}
              />
            </div>

            <div>
              <Label>Priority Level</Label>
              <RadioGroup value={priority} onValueChange={setPriority}>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="low" id="low" />
                  <Label htmlFor="low">Low - Can wait a few days</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="medium" id="medium" />
                  <Label htmlFor="medium">Medium - Within 1-2 days</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="high" id="high" />
                  <Label htmlFor="high">High - Today if possible</Label>
                </div>
                <div className="flex items-center space-x-2">
                  <RadioGroupItem value="urgent" id="urgent" />
                  <Label htmlFor="urgent">Urgent - Immediate attention required</Label>
                </div>
              </RadioGroup>
            </div>

            <div>
              <Label>Photos/Videos (Optional)</Label>
              <div className="border-2 border-dashed border-border rounded-lg p-6 text-center">
                <Camera className="w-8 h-8 text-muted-foreground mx-auto mb-2" />
                <p className="text-sm text-muted-foreground mb-2">
                  Take photos or upload files to help describe the issue
                </p>
                <Button variant="outline" size="sm">
                  <Upload className="w-4 h-4 mr-2" />
                  Upload Files
                </Button>
              </div>
            </div>

            <Button 
              onClick={() => setStep(3)} 
              className="w-full"
              disabled={!title || !description}
            >
              Continue
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (step === 3) {
    return (
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <div>
            <h2 className="text-xl font-semibold">Access & Scheduling</h2>
            <p className="text-muted-foreground">When would you prefer maintenance to visit?</p>
          </div>
          <Button variant="outline" onClick={() => setStep(2)}>
            Back
          </Button>
        </div>

        <Card>
          <CardContent className="p-6 space-y-6">
            <div>
              <Label>Preferred Time (Select all that work for you)</Label>
              <div className="space-y-3 mt-3">
                {timePreferences.map((time) => (
                  <div key={time.id} className="flex items-center space-x-2">
                    <Checkbox
                      id={time.id}
                      checked={preferredTimes.includes(time.id)}
                      onCheckedChange={(checked) => handleTimePreferenceChange(time.id, checked)}
                    />
                    <Label htmlFor={time.id}>{time.label}</Label>
                  </div>
                ))}
              </div>
            </div>

            <div className="flex items-center space-x-2">
              <Checkbox
                id="allowEntry"
                checked={allowEntry}
                onCheckedChange={setAllowEntry}
              />
              <Label htmlFor="allowEntry">
                Allow maintenance to enter if I'm not home (spare key/lockbox)
              </Label>
            </div>

            <div className="bg-muted p-4 rounded-lg">
              <h4 className="font-medium mb-2">Request Summary</h4>
              <div className="space-y-1 text-sm">
                <div><span className="text-muted-foreground">Category:</span> {categories.find(c => c.id === category)?.label}</div>
                <div><span className="text-muted-foreground">Issue:</span> {title}</div>
                <div><span className="text-muted-foreground">Priority:</span> 
                  <Badge variant={priority === 'urgent' ? 'destructive' : priority === 'high' ? 'destructive' : 'secondary'} className="ml-2">
                    {priority}
                  </Badge>
                </div>
              </div>
            </div>

            <Button onClick={handleSubmit} className="w-full">
              Submit Request
            </Button>
          </CardContent>
        </Card>
      </div>
    );
  }

  if (step === 4) {
    return (
      <div className="space-y-6">
        <Card>
          <CardContent className="p-6 text-center">
            <CheckCircle className="w-16 h-16 text-success mx-auto mb-4" />
            <h2 className="text-xl font-semibold mb-2">Request Submitted!</h2>
            <p className="text-muted-foreground mb-6">
              Your service request has been received and will be reviewed by our maintenance team.
            </p>
            
            <div className="bg-muted p-4 rounded-lg mb-6">
              <div className="space-y-2 text-sm">
                <div className="flex justify-between">
                  <span>Request ID:</span>
                  <span className="font-mono">{requestId}</span>
                </div>
                <div className="flex justify-between">
                  <span>Status:</span>
                  <Badge variant="secondary">Open</Badge>
                </div>
                <div className="flex justify-between">
                  <span>Priority:</span>
                  <Badge variant={priority === 'urgent' ? 'destructive' : priority === 'high' ? 'destructive' : 'secondary'}>
                    {priority}
                  </Badge>
                </div>
                <div className="flex justify-between">
                  <span>Expected Response:</span>
                  <span>
                    {priority === 'urgent' ? 'Within 2 hours' :
                     priority === 'high' ? 'Within 4 hours' :
                     priority === 'medium' ? 'Within 24 hours' : 'Within 48 hours'}
                  </span>
                </div>
              </div>
            </div>

            <div className="space-y-3">
              <p className="text-sm text-muted-foreground">
                You'll receive updates via email and in-app notifications. You can also track the progress in your dashboard.
              </p>
              
              <div className="flex gap-3">
                <Button variant="outline" onClick={resetForm} className="flex-1">
                  Submit Another Request
                </Button>
                <Button className="flex-1">
                  View My Requests
                </Button>
              </div>
            </div>
          </CardContent>
        </Card>
      </div>
    );
  }

  return null;
}
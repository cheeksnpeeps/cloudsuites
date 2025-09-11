import { useState } from 'react';
import { Calendar, Clock, Users, DollarSign, CheckCircle, Info } from 'lucide-react';
import { Card, CardContent, CardHeader, CardTitle } from '../ui/card';
import { Button } from '../ui/button';
import { Badge } from '../ui/badge';
import { Calendar as CalendarComponent } from '../ui/calendar';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Input } from '../ui/input';
import { Label } from '../ui/label';
import { Textarea } from '../ui/textarea';
import { Alert, AlertDescription } from '../ui/alert';

const amenities = [
  {
    id: '1',
    name: 'Pool Area',
    description: 'Outdoor pool with lounge chairs and cabanas',
    capacity: 20,
    fee: 0,
    bookingWindow: 7,
    maxBookingsPerWeek: 2,
    rules: ['No glass containers', 'Children must be supervised', 'Pool closes at 10 PM'],
    image: 'https://images.unsplash.com/photo-1544551763-46a013bb70d5?w=400&h=200&fit=crop'
  },
  {
    id: '2',
    name: 'Rooftop Terrace',
    description: 'Beautiful rooftop space with city views, perfect for events',
    capacity: 50,
    fee: 150,
    bookingWindow: 14,
    maxBookingsPerWeek: 1,
    rules: ['Events must end by 11 PM', 'No amplified music after 9 PM', 'Cleanup required'],
    image: 'https://images.unsplash.com/photo-1522708323590-d24dbb6b0267?w=400&h=200&fit=crop'
  },
  {
    id: '3',
    name: 'Fitness Center',
    description: 'Fully equipped gym with cardio and strength training equipment',
    capacity: 12,
    fee: 0,
    bookingWindow: 3,
    maxBookingsPerWeek: 7,
    rules: ['Wipe down equipment after use', 'Proper athletic attire required', '2-hour time limit'],
    image: 'https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400&h=200&fit=crop'
  },
  {
    id: '4',
    name: 'Conference Room',
    description: 'Professional meeting space with AV equipment',
    capacity: 8,
    fee: 25,
    bookingWindow: 30,
    maxBookingsPerWeek: 3,
    rules: ['Business use only', 'Clean up after meetings', 'No food or drinks'],
    image: 'https://images.unsplash.com/photo-1497366216548-37526070297c?w=400&h=200&fit=crop'
  }
];

const timeSlots = [
  '9:00 AM', '10:00 AM', '11:00 AM', '12:00 PM',
  '1:00 PM', '2:00 PM', '3:00 PM', '4:00 PM',
  '5:00 PM', '6:00 PM', '7:00 PM', '8:00 PM', '9:00 PM'
];

// Mock existing bookings
const existingBookings = [
  { amenityId: '1', date: '2024-03-15', startTime: '2:00 PM', endTime: '4:00 PM' },
  { amenityId: '2', date: '2024-03-16', startTime: '6:00 PM', endTime: '10:00 PM' },
];

export function AmenityBooking() {
  const [selectedAmenity, setSelectedAmenity] = useState(null);
  const [selectedDate, setSelectedDate] = useState(null);
  const [startTime, setStartTime] = useState('');
  const [endTime, setEndTime] = useState('');
  const [guests, setGuests] = useState(1);
  const [notes, setNotes] = useState('');
  const [step, setStep] = useState(1);
  const [isDialogOpen, setIsDialogOpen] = useState(false);

  const isTimeSlotAvailable = (amenityId, date, time) => {
    if (!date) return true;
    
    const dateStr = date.toISOString().split('T')[0];
    return !existingBookings.some(booking => 
      booking.amenityId === amenityId && 
      booking.date === dateStr &&
      time >= booking.startTime && 
      time <= booking.endTime
    );
  };

  const handleAmenitySelect = (amenity) => {
    setSelectedAmenity(amenity);
    setStep(2);
  };

  const handleDateSelect = (date) => {
    setSelectedDate(date);
    setStartTime('');
    setEndTime('');
  };

  const handleBooking = () => {
    // Mock booking creation
    console.log('Booking created:', {
      amenityId: selectedAmenity.id,
      date: selectedDate,
      startTime,
      endTime,
      guests,
      notes
    });
    setStep(4);
  };

  const resetBooking = () => {
    setSelectedAmenity(null);
    setSelectedDate(null);
    setStartTime('');
    setEndTime('');
    setGuests(1);
    setNotes('');
    setStep(1);
    setIsDialogOpen(false);
  };

  return (
    <div className="space-y-6">
      {step === 1 && (
        <div className="space-y-6">
          <div>
            <h2 className="text-xl font-semibold mb-2">Book an Amenity</h2>
            <p className="text-muted-foreground">Choose from our available building amenities</p>
          </div>

          <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
            {amenities.map((amenity) => (
              <Card key={amenity.id} className="cursor-pointer hover:shadow-md transition-shadow">
                <div 
                  onClick={() => handleAmenitySelect(amenity)}
                  className="h-full"
                >
                  <div className="h-48 bg-cover bg-center rounded-t-lg" 
                       style={{ backgroundImage: `url(${amenity.image})` }} />
                  <CardContent className="p-4">
                    <div className="space-y-3">
                      <div className="flex items-start justify-between">
                        <h3 className="font-medium">{amenity.name}</h3>
                        {amenity.fee > 0 && (
                          <Badge variant="secondary">${amenity.fee}</Badge>
                        )}
                      </div>
                      
                      <p className="text-sm text-muted-foreground">{amenity.description}</p>
                      
                      <div className="flex items-center gap-4 text-sm text-muted-foreground">
                        <div className="flex items-center gap-1">
                          <Users className="w-4 h-4" />
                          <span>Up to {amenity.capacity}</span>
                        </div>
                        <div className="flex items-center gap-1">
                          <Calendar className="w-4 h-4" />
                          <span>{amenity.bookingWindow} days ahead</span>
                        </div>
                      </div>
                    </div>
                  </CardContent>
                </div>
              </Card>
            ))}
          </div>
        </div>
      )}

      {step === 2 && selectedAmenity && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold">{selectedAmenity.name}</h2>
              <p className="text-muted-foreground">Select your preferred date and time</p>
            </div>
            <Button variant="outline" onClick={() => setStep(1)}>
              Back
            </Button>
          </div>

          <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
            <Card>
              <CardHeader>
                <CardTitle>Select Date</CardTitle>
              </CardHeader>
              <CardContent>
                <CalendarComponent
                  mode="single"
                  selected={selectedDate}
                  onSelect={handleDateSelect}
                  disabled={(date) => {
                    const today = new Date();
                    const maxDate = new Date();
                    maxDate.setDate(today.getDate() + selectedAmenity.bookingWindow);
                    return date < today || date > maxDate;
                  }}
                />
              </CardContent>
            </Card>

            {selectedDate && (
              <Card>
                <CardHeader>
                  <CardTitle>Select Time</CardTitle>
                </CardHeader>
                <CardContent className="space-y-4">
                  <div className="grid grid-cols-2 gap-3">
                    <div>
                      <Label>Start Time</Label>
                      <select 
                        className="w-full p-2 border rounded-md"
                        value={startTime}
                        onChange={(e) => setStartTime(e.target.value)}
                      >
                        <option value="">Select start time</option>
                        {timeSlots.map((time) => (
                          <option 
                            key={time} 
                            value={time}
                            disabled={!isTimeSlotAvailable(selectedAmenity.id, selectedDate, time)}
                          >
                            {time}
                          </option>
                        ))}
                      </select>
                    </div>
                    
                    <div>
                      <Label>End Time</Label>
                      <select 
                        className="w-full p-2 border rounded-md"
                        value={endTime}
                        onChange={(e) => setEndTime(e.target.value)}
                        disabled={!startTime}
                      >
                        <option value="">Select end time</option>
                        {timeSlots.filter(time => time > startTime).map((time) => (
                          <option 
                            key={time} 
                            value={time}
                            disabled={!isTimeSlotAvailable(selectedAmenity.id, selectedDate, time)}
                          >
                            {time}
                          </option>
                        ))}
                      </select>
                    </div>
                  </div>

                  <div>
                    <Label htmlFor="guest-count">Number of Guests</Label>
                    <Input
                      id="guest-count"
                      name="guests"
                      type="number"
                      autoComplete="off"
                      min="1"
                      max={selectedAmenity.capacity}
                      value={guests}
                      onChange={(e) => setGuests(parseInt(e.target.value))}
                    />
                  </div>

                  <div>
                    <Label htmlFor="booking-notes">Notes (Optional)</Label>
                    <Textarea
                      id="booking-notes"
                      name="notes"
                      autoComplete="off"
                      placeholder="Any special requests or notes..."
                      value={notes}
                      onChange={(e) => setNotes(e.target.value)}
                    />
                  </div>

                  {startTime && endTime && (
                    <Button 
                      onClick={() => setStep(3)} 
                      className="w-full"
                    >
                      Review Booking
                    </Button>
                  )}
                </CardContent>
              </Card>
            )}
          </div>

          {selectedAmenity.rules.length > 0 && (
            <Alert>
              <Info className="w-4 h-4" />
              <AlertDescription>
                <div className="font-medium mb-1">Amenity Rules:</div>
                <ul className="list-disc list-inside space-y-1">
                  {selectedAmenity.rules.map((rule, index) => (
                    <li key={index} className="text-sm">{rule}</li>
                  ))}
                </ul>
              </AlertDescription>
            </Alert>
          )}
        </div>
      )}

      {step === 3 && (
        <div className="space-y-6">
          <div className="flex items-center justify-between">
            <div>
              <h2 className="text-xl font-semibold">Review Booking</h2>
              <p className="text-muted-foreground">Please confirm your booking details</p>
            </div>
            <Button variant="outline" onClick={() => setStep(2)}>
              Back
            </Button>
          </div>

          <Card>
            <CardContent className="p-6">
              <div className="space-y-4">
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Amenity:</span>
                  <span className="font-medium">{selectedAmenity.name}</span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Date:</span>
                  <span className="font-medium">{selectedDate?.toLocaleDateString()}</span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Time:</span>
                  <span className="font-medium">{startTime} - {endTime}</span>
                </div>
                
                <div className="flex justify-between">
                  <span className="text-muted-foreground">Guests:</span>
                  <span className="font-medium">{guests}</span>
                </div>
                
                {selectedAmenity.fee > 0 && (
                  <div className="flex justify-between">
                    <span className="text-muted-foreground">Fee:</span>
                    <span className="font-medium">${selectedAmenity.fee}</span>
                  </div>
                )}
                
                {notes && (
                  <div className="space-y-1">
                    <span className="text-muted-foreground">Notes:</span>
                    <p className="text-sm">{notes}</p>
                  </div>
                )}
              </div>
              
              <div className="flex gap-3 mt-6">
                <Button variant="outline" onClick={() => setStep(2)} className="flex-1">
                  Back to Edit
                </Button>
                <Button onClick={handleBooking} className="flex-1">
                  {selectedAmenity.fee > 0 ? 'Confirm & Pay' : 'Confirm Booking'}
                </Button>
              </div>
            </CardContent>
          </Card>
        </div>
      )}

      {step === 4 && (
        <div className="space-y-6">
          <Card>
            <CardContent className="p-6 text-center">
              <CheckCircle className="w-16 h-16 text-success mx-auto mb-4" />
              <h2 className="text-xl font-semibold mb-2">Booking Confirmed!</h2>
              <p className="text-muted-foreground mb-6">
                Your booking for {selectedAmenity.name} has been submitted and is pending approval.
                You'll receive a confirmation email shortly.
              </p>
              
              <div className="space-y-2 text-sm text-left max-w-md mx-auto">
                <div className="flex justify-between">
                  <span>Booking ID:</span>
                  <span className="font-mono">BK-{Date.now()}</span>
                </div>
                <div className="flex justify-between">
                  <span>Status:</span>
                  <Badge variant="secondary">Pending Approval</Badge>
                </div>
              </div>
              
              <Button onClick={resetBooking} className="mt-6">
                Book Another Amenity
              </Button>
            </CardContent>
          </Card>
        </div>
      )}
    </div>
  );
}
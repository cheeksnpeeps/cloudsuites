// User roles and permissions
export type UserRole = 
  | 'SUPER_ADMIN'
  | 'BUILDINGS_ADMIN' 
  | 'BUSINESS_ADMIN'
  | 'THIRD_PARTY_ADMIN'
  | 'PROPERTY_MANAGER'
  | 'LEASING_AGENT'
  | 'MAINTENANCE_TECHNICIAN'
  | 'ACCOUNTING_FINANCE_MANAGER'
  | 'CUSTOMER_SERVICE_REPRESENTATIVE'
  | 'BUILDING_SUPERVISOR'
  | 'BUILDING_SECURITY'
  | 'OWNER'
  | 'TENANT';

export type PersonaType = 'admin' | 'owner' | 'staff' | 'tenant';

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  persona: PersonaType;
  avatar?: string;
  buildingIds?: string[];
  unitIds?: string[];
}

export interface Building {
  id: string;
  name: string;
  address: string;
  units: number;
  occupancy: number;
}

export interface Unit {
  id: string;
  buildingId: string;
  number: string;
  type: string;
  bedrooms: number;
  bathrooms: number;
  sqft: number;
  rent: number;
  status: 'occupied' | 'vacant' | 'maintenance';
  tenantId?: string;
}

export interface ServiceRequest {
  id: string;
  unitId: string;
  tenantId: string;
  category: string;
  title: string;
  description: string;
  priority: 'low' | 'medium' | 'high' | 'urgent';
  status: 'open' | 'in_progress' | 'completed' | 'cancelled';
  assignedTo?: string;
  createdAt: string;
  updatedAt: string;
  photos?: string[];
}

export interface AmenityBooking {
  id: string;
  amenityId: string;
  unitId: string;
  tenantId: string;
  date: string;
  startTime: string;
  endTime: string;
  status: 'pending' | 'approved' | 'denied' | 'cancelled';
  guests?: number;
  fee?: number;
  createdAt: string;
}

export interface Amenity {
  id: string;
  buildingId: string;
  name: string;
  description: string;
  capacity: number;
  bookingWindow: number; // days in advance
  maxBookingsPerWeek: number;
  fee?: number;
  rules?: string[];
  image?: string;
}
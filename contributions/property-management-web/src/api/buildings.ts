import { ApiService, PaginatedResponse, buildQueryString } from './index';

// Building Types
export interface Building {
  buildingId: string;
  buildingName: string;
  address: string;
  description?: string;
  companyId: string;
  totalFloors: number;
  totalUnits: number;
  createdAt: string;
  lastModifiedAt: string;
  createdBy: string;
  lastModifiedBy: string;
  status: BuildingStatus;
}

export type BuildingStatus = 'ACTIVE' | 'INACTIVE' | 'UNDER_CONSTRUCTION' | 'MAINTENANCE';

export interface CreateBuildingRequest {
  buildingName: string;
  address: string;
  description?: string;
  companyId: string;
  totalFloors: number;
  totalUnits: number;
}

export interface UpdateBuildingRequest extends Partial<CreateBuildingRequest> {
  buildingId: string;
}

export interface BuildingFilters {
  companyId?: string;
  status?: BuildingStatus;
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
}

// Building Floor
export interface BuildingFloor {
  floorId: string;
  floorNumber: number;
  buildingId: string;
  totalUnits: number;
  description?: string;
  status: string;
}

// Building Unit
export interface BuildingUnit {
  unitId: string;
  unitNumber: string;
  buildingId: string;
  floorId: string;
  unitType: string;
  squareFootage?: number;
  bedrooms?: number;
  bathrooms?: number;
  rentAmount?: number;
  status: string;
}

// Building Service
class BuildingService extends ApiService {
  // Get all buildings with filtering
  async getBuildings(filters?: BuildingFilters): Promise<PaginatedResponse<Building>> {
    const queryString = filters ? `?${buildQueryString(filters)}` : '';
    return this.get<PaginatedResponse<Building>>(`/buildings${queryString}`);
  }

  // Get building by ID
  async getBuildingById(buildingId: string): Promise<Building> {
    return this.get<Building>(`/buildings/${buildingId}`);
  }

  // Create new building
  async createBuilding(building: CreateBuildingRequest): Promise<Building> {
    return this.post<Building>('/buildings', building);
  }

  // Update building
  async updateBuilding(building: UpdateBuildingRequest): Promise<Building> {
    const { buildingId, ...updateData } = building;
    return this.put<Building>(`/buildings/${buildingId}`, updateData);
  }

  // Delete building
  async deleteBuilding(buildingId: string): Promise<void> {
    return this.delete<void>(`/buildings/${buildingId}`);
  }

  // Get building floors
  async getBuildingFloors(buildingId: string): Promise<BuildingFloor[]> {
    return this.get<BuildingFloor[]>(`/buildings/${buildingId}/floors`);
  }

  // Get building units
  async getBuildingUnits(buildingId: string, floorId?: string): Promise<BuildingUnit[]> {
    const endpoint = floorId 
      ? `/buildings/${buildingId}/floors/${floorId}/units`
      : `/buildings/${buildingId}/units`;
    return this.get<BuildingUnit[]>(endpoint);
  }

  // Get building amenities
  async getBuildingAmenities(buildingId: string): Promise<any[]> {
    return this.get<any[]>(`/buildings/${buildingId}/amenities`);
  }

  // Add amenity to building
  async addAmenityToBuilding(buildingId: string, amenityId: string): Promise<void> {
    return this.post<void>(`/buildings/${buildingId}/amenities/${amenityId}`);
  }

  // Remove amenity from building
  async removeAmenityFromBuilding(buildingId: string, amenityId: string): Promise<void> {
    return this.delete<void>(`/buildings/${buildingId}/amenities/${amenityId}`);
  }

  // Get buildings by company
  async getBuildingsByCompany(companyId: string): Promise<Building[]> {
    return this.get<Building[]>(`/companies/${companyId}/buildings`);
  }

  // Get building statistics
  async getBuildingStats(buildingId: string): Promise<{
    totalFloors: number;
    totalUnits: number;
    occupiedUnits: number;
    vacantUnits: number;
    totalTenants: number;
    totalAmenities: number;
  }> {
    return this.get(`/buildings/${buildingId}/stats`);
  }
}

// Export singleton instance
export const buildingService = new BuildingService();
export default buildingService;

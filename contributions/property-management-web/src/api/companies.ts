import { ApiService, PaginatedResponse, buildQueryString } from './index';

// Company Types
export interface Company {
  companyId: string;
  companyName: string;
  address: string;
  phoneNumber: string;
  email: string;
  website?: string;
  createdAt: string;
  lastModifiedAt: string;
  createdBy: string;
  lastModifiedBy: string;
  status: CompanyStatus;
}

export type CompanyStatus = 'ACTIVE' | 'INACTIVE' | 'SUSPENDED';

export interface CreateCompanyRequest {
  companyName: string;
  address: string;
  phoneNumber: string;
  email: string;
  website?: string;
}

export interface UpdateCompanyRequest extends Partial<CreateCompanyRequest> {
  companyId: string;
}

export interface CompanyFilters {
  status?: CompanyStatus;
  search?: string;
  page?: number;
  size?: number;
  sort?: string;
}

// Company Building Association
export interface CompanyBuilding {
  buildingId: string;
  buildingName: string;
  address: string;
  status: string;
  createdAt: string;
}

// Company Service
class CompanyService extends ApiService {
  // Get all companies with filtering
  async getCompanies(filters?: CompanyFilters): Promise<PaginatedResponse<Company>> {
    const queryString = filters ? `?${buildQueryString(filters)}` : '';
    return this.get<PaginatedResponse<Company>>(`/companies${queryString}`);
  }

  // Get company by ID
  async getCompanyById(companyId: string): Promise<Company> {
    return this.get<Company>(`/companies/${companyId}`);
  }

  // Create new company
  async createCompany(company: CreateCompanyRequest): Promise<Company> {
    return this.post<Company>('/companies', company);
  }

  // Update company
  async updateCompany(company: UpdateCompanyRequest): Promise<Company> {
    const { companyId, ...updateData } = company;
    return this.put<Company>(`/companies/${companyId}`, updateData);
  }

  // Delete company
  async deleteCompany(companyId: string): Promise<void> {
    return this.delete<void>(`/companies/${companyId}`);
  }

  // Get buildings associated with company
  async getCompanyBuildings(companyId: string): Promise<CompanyBuilding[]> {
    return this.get<CompanyBuilding[]>(`/companies/${companyId}/buildings`);
  }

  // Add building to company
  async addBuildingToCompany(companyId: string, buildingId: string): Promise<void> {
    return this.post<void>(`/companies/${companyId}/buildings/${buildingId}`);
  }

  // Remove building from company
  async removeBuildingFromCompany(companyId: string, buildingId: string): Promise<void> {
    return this.delete<void>(`/companies/${companyId}/buildings/${buildingId}`);
  }

  // Get company statistics (if available)
  async getCompanyStats(companyId: string): Promise<{
    totalBuildings: number;
    totalUnits: number;
    totalTenants: number;
    totalStaff: number;
  }> {
    return this.get(`/companies/${companyId}/stats`);
  }
}

// Export singleton instance
export const companyService = new CompanyService();
export default companyService;

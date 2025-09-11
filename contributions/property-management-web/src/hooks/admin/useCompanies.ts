import toast from 'react-hot-toast';
import { 
  companyService, 
  Company, 
  CreateCompanyRequest, 
  UpdateCompanyRequest, 
  CompanyFilters
} from '../../api/companies';
import { useApiQuery, useApiMutation, queryKeys, useInvalidateQueries } from '../useApi';

// Company Query Hooks
export const useCompanies = (filters?: CompanyFilters) => {
  return useApiQuery({
    queryKey: queryKeys.companies.list(filters),
    queryFn: () => companyService.getCompanies(filters),
    staleTime: 5 * 60 * 1000, // 5 minutes
  });
};

export const useCompany = (companyId: string, options?: { enabled?: boolean }) => {
  return useApiQuery({
    queryKey: queryKeys.companies.byId(companyId),
    queryFn: () => companyService.getCompanyById(companyId),
    enabled: !!companyId && (options?.enabled ?? true),
    staleTime: 5 * 60 * 1000,
  });
};

export const useCompanyBuildings = (companyId: string, options?: { enabled?: boolean }) => {
  return useApiQuery({
    queryKey: [...queryKeys.companies.byId(companyId), 'buildings'],
    queryFn: () => companyService.getCompanyBuildings(companyId),
    enabled: !!companyId && (options?.enabled ?? true),
    staleTime: 5 * 60 * 1000,
  });
};

export const useCompanyStats = (companyId: string, options?: { enabled?: boolean }) => {
  return useApiQuery({
    queryKey: [...queryKeys.companies.byId(companyId), 'stats'],
    queryFn: () => companyService.getCompanyStats(companyId),
    enabled: !!companyId && (options?.enabled ?? true),
    staleTime: 2 * 60 * 1000, // 2 minutes for stats
  });
};

// Company Mutation Hooks
export const useCreateCompany = () => {
  const { invalidateByPrefix } = useInvalidateQueries();

  return useApiMutation<Company, CreateCompanyRequest>({
    mutationFn: (company) => companyService.createCompany(company),
    onSuccess: () => {
      toast.success('Company created successfully');
      invalidateByPrefix('companies');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to create company');
    },
  });
};

export const useUpdateCompany = () => {
  const { invalidateByPrefix } = useInvalidateQueries();

  return useApiMutation<Company, UpdateCompanyRequest>({
    mutationFn: (company) => companyService.updateCompany(company),
    onSuccess: (updatedCompany) => {
      toast.success('Company updated successfully');
      invalidateByPrefix('companies');
      // Also invalidate specific company query
      invalidateByPrefix(updatedCompany.companyId);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to update company');
    },
  });
};

export const useDeleteCompany = () => {
  const { invalidateByPrefix } = useInvalidateQueries();

  return useApiMutation<void, string>({
    mutationFn: (companyId) => companyService.deleteCompany(companyId),
    onSuccess: () => {
      toast.success('Company deleted successfully');
      invalidateByPrefix('companies');
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to delete company');
    },
  });
};

export const useAddBuildingToCompany = () => {
  const { invalidateByPrefix } = useInvalidateQueries();

  return useApiMutation<void, { companyId: string; buildingId: string }>({
    mutationFn: ({ companyId, buildingId }) => 
      companyService.addBuildingToCompany(companyId, buildingId),
    onSuccess: (_, variables) => {
      toast.success('Building added to company successfully');
      invalidateByPrefix('companies');
      invalidateByPrefix('buildings');
      // Invalidate specific company buildings
      invalidateByPrefix(variables.companyId);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to add building to company');
    },
  });
};

export const useRemoveBuildingFromCompany = () => {
  const { invalidateByPrefix } = useInvalidateQueries();

  return useApiMutation<void, { companyId: string; buildingId: string }>({
    mutationFn: ({ companyId, buildingId }) => 
      companyService.removeBuildingFromCompany(companyId, buildingId),
    onSuccess: (_, variables) => {
      toast.success('Building removed from company successfully');
      invalidateByPrefix('companies');
      invalidateByPrefix('buildings');
      // Invalidate specific company buildings
      invalidateByPrefix(variables.companyId);
    },
    onError: (error) => {
      toast.error(error.message || 'Failed to remove building from company');
    },
  });
};

// Combined hook for company management operations
export const useCompanyManagement = () => {
  const createCompany = useCreateCompany();
  const updateCompany = useUpdateCompany();
  const deleteCompany = useDeleteCompany();
  const addBuilding = useAddBuildingToCompany();
  const removeBuilding = useRemoveBuildingFromCompany();

  return {
    // Mutations
    createCompany,
    updateCompany,
    deleteCompany,
    addBuilding,
    removeBuilding,
    
    // Loading states
    isLoading: createCompany.isPending || updateCompany.isPending || deleteCompany.isPending,
    isCreating: createCompany.isPending,
    isUpdating: updateCompany.isPending,
    isDeleting: deleteCompany.isPending,
    
    // Error states
    createError: createCompany.error,
    updateError: updateCompany.error,
    deleteError: deleteCompany.error,
  };
};

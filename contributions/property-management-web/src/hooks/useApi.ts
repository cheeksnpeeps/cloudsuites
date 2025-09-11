import { useMutation, useQuery, useQueryClient, UseQueryOptions, UseMutationOptions } from '@tanstack/react-query';
import { ApiError } from '../api';

// Generic API hook types
export interface UseApiQueryOptions<T> extends Omit<UseQueryOptions<T, ApiError>, 'queryKey' | 'queryFn'> {
  queryKey: readonly (string | number | boolean | object)[];
  queryFn: () => Promise<T>;
}

export interface UseApiMutationOptions<T, V = any> extends Omit<UseMutationOptions<T, ApiError, V>, 'mutationFn'> {
  mutationFn: (variables: V) => Promise<T>;
}

// Generic API hooks
export const useApiQuery = <T>(options: UseApiQueryOptions<T>) => {
  return useQuery<T, ApiError>({
    ...options,
    queryKey: options.queryKey,
    queryFn: options.queryFn,
  });
};

export const useApiMutation = <T, V = any>(options: UseApiMutationOptions<T, V>) => {
  return useMutation<T, ApiError, V>({
    ...options,
    mutationFn: options.mutationFn,
  });
};

// Custom hook for invalidating queries
export const useInvalidateQueries = () => {
  const queryClient = useQueryClient();
  
  return {
    invalidateAll: () => queryClient.invalidateQueries(),
    invalidateByKey: (queryKey: readonly (string | number | boolean | object)[]) => 
      queryClient.invalidateQueries({ queryKey }),
    invalidateByPrefix: (prefix: string) => 
      queryClient.invalidateQueries({ queryKey: [prefix] }),
  };
};

// Custom hook for optimistic updates
export const useOptimisticUpdate = () => {
  const queryClient = useQueryClient();
  
  return {
    updateQueryData: <T>(
      queryKey: readonly (string | number | boolean | object)[],
      updater: (oldData: T | undefined) => T
    ) => {
      queryClient.setQueryData(queryKey, updater);
    },
    
    cancelQueries: (queryKey: readonly (string | number | boolean | object)[]) => 
      queryClient.cancelQueries({ queryKey }),
    
    getQueryData: <T>(queryKey: readonly (string | number | boolean | object)[]): T | undefined => 
      queryClient.getQueryData(queryKey),
  };
};

// Query key factories for consistent query key management
export const queryKeys = {
  // Authentication
  auth: {
    all: ['auth'] as const,
    currentUser: () => [...queryKeys.auth.all, 'current-user'] as const,
  },
  
  // Users
  users: {
    all: ['users'] as const,
    admins: () => [...queryKeys.users.all, 'admins'] as const,
    staff: () => [...queryKeys.users.all, 'staff'] as const,
    tenants: () => [...queryKeys.users.all, 'tenants'] as const,
    owners: () => [...queryKeys.users.all, 'owners'] as const,
    byId: (id: string) => [...queryKeys.users.all, 'detail', id] as const,
  },
  
  // Companies
  companies: {
    all: ['companies'] as const,
    list: (filters?: object) => [...queryKeys.companies.all, 'list', filters || {}] as const,
    byId: (id: string) => [...queryKeys.companies.all, 'detail', id] as const,
  },
  
  // Buildings
  buildings: {
    all: ['buildings'] as const,
    list: (filters?: object) => [...queryKeys.buildings.all, 'list', filters] as const,
    byId: (id: string) => [...queryKeys.buildings.all, 'detail', id] as const,
    byCompany: (companyId: string) => [...queryKeys.buildings.all, 'company', companyId] as const,
  },
  
  // Units
  units: {
    all: ['units'] as const,
    list: (filters?: object) => [...queryKeys.units.all, 'list', filters] as const,
    byId: (id: string) => [...queryKeys.units.all, 'detail', id] as const,
    byBuilding: (buildingId: string) => [...queryKeys.units.all, 'building', buildingId] as const,
  },
  
  // Amenities
  amenities: {
    all: ['amenities'] as const,
    list: (filters?: object) => [...queryKeys.amenities.all, 'list', filters] as const,
    byId: (id: string) => [...queryKeys.amenities.all, 'detail', id] as const,
    byBuilding: (buildingId: string) => [...queryKeys.amenities.all, 'building', buildingId] as const,
  },
  
  // Bookings
  bookings: {
    all: ['bookings'] as const,
    list: (filters?: object) => [...queryKeys.bookings.all, 'list', filters] as const,
    byId: (id: string) => [...queryKeys.bookings.all, 'detail', id] as const,
    byUser: (userId: string) => [...queryKeys.bookings.all, 'user', userId] as const,
    byAmenity: (amenityId: string) => [...queryKeys.bookings.all, 'amenity', amenityId] as const,
  },
  
  // Roles
  roles: {
    all: ['roles'] as const,
    byType: (type: string) => [...queryKeys.roles.all, 'type', type] as const,
    byId: (id: string) => [...queryKeys.roles.all, 'detail', id] as const,
  },
};

// Common query options
export const defaultQueryOptions = {
  staleTime: 5 * 60 * 1000, // 5 minutes
  cacheTime: 10 * 60 * 1000, // 10 minutes
  retry: 2,
  refetchOnWindowFocus: false,
} as const;

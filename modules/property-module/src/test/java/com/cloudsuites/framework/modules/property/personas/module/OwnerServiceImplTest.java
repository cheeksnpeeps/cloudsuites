package com.cloudsuites.framework.modules.property.personas.module;

import com.cloudsuites.framework.modules.property.personas.repository.OwnerRepository;
import com.cloudsuites.framework.modules.property.personas.repository.TenantRepository;
import com.cloudsuites.framework.modules.user.repository.UserRoleRepository;
import com.cloudsuites.framework.services.common.exception.InvalidOperationException;
import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.common.exception.UserAlreadyExistsException;
import com.cloudsuites.framework.services.property.features.entities.Building;
import com.cloudsuites.framework.services.property.features.entities.Unit;
import com.cloudsuites.framework.services.property.features.service.UnitService;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.entities.OwnerStatus;
import com.cloudsuites.framework.services.user.UserService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.services.user.entities.UserRole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import org.mockito.ArgumentCaptor;

/**
 * Comprehensive unit tests for OwnerServiceImpl covering decision table scenarios and concurrency.
 * 
 * Test Coverage:
 * - Decision Table: All 5 scenarios from ADR-001
 * - Email normalization 
 * - Concurrency handling with database constraints
 * - User role management
 * - Building/unit associations
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("OwnerServiceImpl - Owner Creation Business Logic Tests")
class OwnerServiceImplTest {

    private static final String TEST_EMAIL = "test@example.com";
    private static final String NORMALIZED_EMAIL = "test@example.com";
    private static final String UNNORMALIZED_EMAIL = "  TEST@EXAMPLE.COM  ";
    
    // Test data constants
    private static final String EXISTING_USER_ID_789 = "existing-user-789";
    private static final String USER_ID_123 = "user-123";
    private static final String OWNER_ID_123 = "owner-123";
    private static final String BUILDING_ID_123 = "building-123";
    private static final String UNIT_ID_123 = "unit-123";
    private static final String DIFFERENT_BUILDING_ID = "different-building-456";

    @Mock
    private OwnerRepository ownerRepository;
    
    @Mock
    private UnitService unitService;
    
    @Mock
    private UserService userService;
    
    @Mock
    private TenantRepository tenantRepository;
    
    @Mock
    private UserRoleRepository userRoleRepository;

    @InjectMocks
    private OwnerServiceImpl ownerService;

    private Owner testOwner;
    private Identity testIdentity;

    @BeforeEach
    void setUp() {
        testIdentity = anIdentity(TEST_EMAIL, "test-user");
        testOwner = anOwner(testIdentity, "test-owner");
    }

    // Static factory methods for test data creation
    static Identity anIdentity(String email, String userId) {
        Identity identity = new Identity();
        identity.setUserId(userId);
        identity.setEmail(email);
        identity.setFirstName("John");
        identity.setLastName("Doe");
        return identity;
    }

    static Owner anOwner(Identity identity, String ownerId) {
        Owner owner = new Owner();
        owner.setOwnerId(ownerId);
        owner.setIdentity(identity);
        owner.setStatus(OwnerStatus.ACTIVE);
        return owner;
    }

    static Building aBuilding(String buildingId) {
        Building building = new Building();
        building.setBuildingId(buildingId);
        return building;
    }

    static Unit aUnit(String unitId, Building building) {
        Unit unit = new Unit();
        unit.setUnitId(unitId);
        unit.setBuilding(building);
        return unit;
    }

    @Nested
    @DisplayName("Decision Table Scenarios (ADR-001)")
    class DecisionTableScenarios {

        @Test
        @DisplayName("Scenario 1: No identity exists → creates identity + owner")
        void scenario1_noIdentityExists_createsIdentityAndOwner() throws Exception {
            // Given: No existing identity
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, "new-user-456");
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Owner savedOwner = anOwner(savedIdentity, "new-owner-456");
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            // When: Creating owner
            Owner result = ownerService.createOwner(testOwner);

            // Then: New identity and owner created with correct associations
            assertThat(result)
                .extracting(Owner::getOwnerId, o -> o.getIdentity().getUserId())
                .containsExactly("new-owner-456", "new-user-456");
            
            verify(userService).findByEmail(NORMALIZED_EMAIL);
            verify(userService).createUser(any(Identity.class));
            verify(ownerRepository).save(any(Owner.class));
        }

        @Test
        @DisplayName("Scenario 2: Identity exists, no owner → creates owner with existing identity")
        void scenario2_identityExistsNoOwner_createsOwnerWithExistingIdentity() throws Exception {
            // Given: Existing identity but no owner
            Identity existingIdentity = anIdentity(NORMALIZED_EMAIL, EXISTING_USER_ID_789);
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(existingIdentity);
            when(ownerRepository.findByIdentity_UserId(EXISTING_USER_ID_789)).thenReturn(Optional.empty());
            
            Owner savedOwner = anOwner(existingIdentity, "new-owner-789");
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            // When: Creating owner
            Owner result = ownerService.createOwner(testOwner);

            // Then: Owner created with existing identity, no new identity created
            assertThat(result)
                .extracting(Owner::getOwnerId, o -> o.getIdentity().getUserId())
                .containsExactly("new-owner-789", EXISTING_USER_ID_789);
            
            verify(userService).findByEmail(NORMALIZED_EMAIL);
            verify(userService, never()).createUser(any(Identity.class));
            verify(ownerRepository).findByIdentity_UserId(EXISTING_USER_ID_789);
        }

        @Test
        @DisplayName("Scenario 3: Identity + owner exist → throws UserAlreadyExistsException")
        void scenario3_identityAndOwnerExist_throwsUserAlreadyExistsException() {
            // Given: Both identity and owner exist
            Identity existingIdentity = anIdentity(NORMALIZED_EMAIL, "existing-user-999");
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(existingIdentity);
            
            Owner existingOwner = anOwner(existingIdentity, "existing-owner-999");
            when(ownerRepository.findByIdentity_UserId("existing-user-999"))
                .thenReturn(Optional.of(existingOwner));

            // When & Then: Should throw UserAlreadyExistsException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner))
                .isInstanceOf(UserAlreadyExistsException.class)
                .hasMessageContaining("Owner already exists with email: " + NORMALIZED_EMAIL);
            
            verify(userService).findByEmail(NORMALIZED_EMAIL);
            verify(ownerRepository).findByIdentity_UserId("existing-user-999");
        }

        @Test
        @DisplayName("Scenario 4: Invalid email → throws InvalidOperationException")
        void invalidEmail_empty() {
            // Given: Owner with invalid email
            testIdentity.setEmail("");
            
            // When & Then: Should throw InvalidOperationException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Email is required");
        }

        @ParameterizedTest
        @ValueSource(strings = {"   ", "\t", "\n"})
        @DisplayName("Scenario 4b: Various invalid emails → throws InvalidOperationException")
        void invalidEmail_whitespace(String invalidEmail) {
            // Given: Owner with various invalid emails
            testIdentity.setEmail(invalidEmail);
            
            // When & Then: Should throw InvalidOperationException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Email is required");
        }

        @Test
        @DisplayName("Scenario 5: Missing identity → throws InvalidOperationException")
        void scenario5MissingIdentityThrowsInvalidOperationException() {
            // Given: Owner with null identity
            testOwner.setIdentity(null);
            
            // When & Then: Should throw InvalidOperationException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Identity not found for owner");
            
            verify(userService, never()).findByEmail(anyString());
            verify(ownerRepository, never()).save(any(Owner.class));
        }
    }

    @Nested
    @DisplayName("Email Normalization Tests")
    class EmailNormalizationTests {

        @Test
        @DisplayName("Email normalization: trim and lowercase")
        void emailNormalizationTrimsAndConvertsToLowercase() throws Exception {
            // Given: Email with spaces and uppercase
            testIdentity.setEmail(UNNORMALIZED_EMAIL);
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, "new-user-123");
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Owner savedOwner = anOwner(savedIdentity, "new-owner-123");
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            // When: Creating owner
            ownerService.createOwner(testOwner);

            // Then: Email should be normalized before lookup
            verify(userService).findByEmail(NORMALIZED_EMAIL);
            assertThat(testIdentity.getEmail()).isEqualTo(NORMALIZED_EMAIL);
        }
    }

    @Nested
    @DisplayName("Concurrency and Database Constraint Tests")
    class ConcurrencyTests {

        @Test
        @DisplayName("Concurrent owner creation with same email - database constraint violation")
        void concurrentCreationHandlesConstraintViolation() throws Exception {
            // Given: Two threads trying to create owner with same email
            CountDownLatch startLatch = new CountDownLatch(1);
            CountDownLatch completeLatch = new CountDownLatch(2);
            
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class)))
                .thenReturn(savedIdentity)
                .thenThrow(new DataIntegrityViolationException("Duplicate email"));
            
            Owner savedOwner = anOwner(savedIdentity, "owner-123");
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            ExecutorService executor = Executors.newFixedThreadPool(2);

            CompletableFuture<Owner> future1 = CompletableFuture.supplyAsync(() -> {
                try {
                    boolean awaitResult = startLatch.await(1, TimeUnit.SECONDS);
                    if (!awaitResult) {
                        throw new TimeoutException("Timeout waiting for start signal");
                    }
                    return ownerService.createOwner(createConcurrentTestOwner());
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    throw new ConcurrentOwnerCreationException("Owner creation failed", e);
                } finally {
                    completeLatch.countDown();
                }
            }, executor);

            CompletableFuture<Owner> future2 = CompletableFuture.supplyAsync(() -> {
                try {
                    boolean awaitResult = startLatch.await(1, TimeUnit.SECONDS);
                    if (!awaitResult) {
                        throw new TimeoutException("Timeout waiting for start signal");
                    }
                    return ownerService.createOwner(createConcurrentTestOwner());
                } catch (Exception e) {
                    if (e instanceof InterruptedException) {
                        Thread.currentThread().interrupt();
                    }
                    throw new ConcurrentOwnerCreationException("Owner creation failed", e);
                } finally {
                    completeLatch.countDown();
                }
            }, executor);

            // Start both threads
            startLatch.countDown();
            
            // Wait for completion
            boolean awaitResult = completeLatch.await(5, TimeUnit.SECONDS);
            assertThat(awaitResult).isTrue();

            // Then: One should succeed, one should fail
            try {
                Owner result1 = future1.get();
                assertThat(result1).isNotNull();
                
                assertThatThrownBy(future2::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(ConcurrentOwnerCreationException.class)
                    .hasRootCauseInstanceOf(DataIntegrityViolationException.class);
                    
            } catch (Exception e) {
                assertThat(future2.get()).isNotNull();
                assertThatThrownBy(future1::get)
                    .isInstanceOf(ExecutionException.class)
                    .hasCauseInstanceOf(ConcurrentOwnerCreationException.class)
                    .hasRootCauseInstanceOf(DataIntegrityViolationException.class);
            }

            executor.shutdown();
        }

        @Test
        @DisplayName("Repository constraint violation during save → throws DataIntegrityViolationException")
        void repositoryConstraintViolationThrowsDataIntegrityViolationException() throws UserAlreadyExistsException {
            // Given: No existing identity found, but constraint violation during save
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            when(ownerRepository.save(any(Owner.class)))
                .thenThrow(new DataIntegrityViolationException("Unique constraint violation"));

            // When & Then: Should propagate constraint violation
            assertThatThrownBy(() -> ownerService.createOwner(testOwner))
                .isInstanceOf(DataIntegrityViolationException.class)
                .hasMessageContaining("Unique constraint violation");
        }

        /**
         * Helper method for concurrent tests to create test owner instances
         */
        private Owner createConcurrentTestOwner() {
            Identity identity = anIdentity(NORMALIZED_EMAIL, "test-user");
            return anOwner(identity, "test-owner");
        }
    }

    @Nested
    @DisplayName("User Role Management Tests")
    class UserRoleManagementTests {

        @Test
        @DisplayName("Owner with user role → saves user role after owner creation")
        void ownerWithUserRole_savesUserRoleAfterOwnerCreation() throws Exception {
            // Given: Owner with role enum set
            testOwner.setRole(com.cloudsuites.framework.services.property.personas.entities.OwnerRole.DEFAULT);
            
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Owner savedOwner = anOwner(savedIdentity, OWNER_ID_123);
            savedOwner.setRole(com.cloudsuites.framework.services.property.personas.entities.OwnerRole.DEFAULT);
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            ArgumentCaptor<UserRole> userRoleCaptor = ArgumentCaptor.forClass(UserRole.class);

            // When: Creating owner
            Owner result = ownerService.createOwner(testOwner);

            // Then: Verify exact user role persisted
            verify(userRoleRepository).save(userRoleCaptor.capture());
            UserRole capturedUserRole = userRoleCaptor.getValue();
            
            assertThat(capturedUserRole.getRole()).isEqualTo("DEFAULT");
            assertThat(result.getUserRole()).isNotNull();
            assertThat(result.getUserRole().getRole()).isEqualTo("DEFAULT");
        }

        @Test
        @DisplayName("Owner without user role → saves default role")
        void ownerWithoutUserRole_savesDefaultRole() throws Exception {
            // Given: Owner without explicit role
            testOwner.setRole(null);
            
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Owner savedOwner = anOwner(savedIdentity, OWNER_ID_123);
            savedOwner.setRole(null);
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            ArgumentCaptor<UserRole> userRoleCaptor = ArgumentCaptor.forClass(UserRole.class);

            // When: Creating owner
            Owner result = ownerService.createOwner(testOwner);

            // Then: Verify default role is assigned and persisted
            verify(userRoleRepository).save(userRoleCaptor.capture());
            UserRole capturedUserRole = userRoleCaptor.getValue();
            
            assertThat(capturedUserRole.getRole()).isEqualTo("DEFAULT");
            assertThat(result.getUserRole()).isNotNull();
            assertThat(result.getUserRole().getRole()).isEqualTo("DEFAULT");
        }
    }

    @Nested
    @DisplayName("Building and Unit Association Tests")
    class BuildingUnitAssociationTests {

        @Test
        @DisplayName("Create owner with building and unit → associates correctly")
        void createOwnerWithBuildingAndUnit_associatesCorrectly() throws Exception {
            // Given: Valid building and unit
            Building testBuilding = aBuilding(BUILDING_ID_123);
            Unit testUnit = aUnit(UNIT_ID_123, testBuilding);
            
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Unit savedUnit = aUnit(UNIT_ID_123, testBuilding);
            when(unitService.saveUnit(any(Unit.class))).thenReturn(savedUnit);
            
            Owner savedOwner = anOwner(savedIdentity, "owner-123");
            when(ownerRepository.save(any(Owner.class))).thenReturn(savedOwner);

            // When: Creating owner with building and unit
            Owner result = ownerService.createOwner(testOwner, testBuilding, testUnit);

            // Then: Owner created and unit associations saved
            assertThat(result).isNotNull();
            verify(unitService, times(2)).saveUnit(any(Unit.class));
        }

        @Test
        @DisplayName("Create owner with null building → throws NotFoundResponseException")
        void createOwnerWithNullBuilding_throwsNotFoundResponseException() throws UserAlreadyExistsException {
            // Given: Basic identity setup (required before building validation)
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Unit testUnit = aUnit(UNIT_ID_123, null);
            
            // When & Then: Should throw NotFoundResponseException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner, null, testUnit))
                .isInstanceOf(NotFoundResponseException.class)
                .hasMessageContaining("Building not found for owner");
        }

        @Test
        @DisplayName("Create owner with null unit → throws NotFoundResponseException")
        void createOwnerWithNullUnit_throwsNotFoundResponseException() throws UserAlreadyExistsException {
            // Given: Basic identity setup (required before unit validation)
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Building testBuilding = aBuilding(BUILDING_ID_123);
            
            // When & Then: Should throw NotFoundResponseException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner, testBuilding, null))
                .isInstanceOf(NotFoundResponseException.class)
                .hasMessageContaining("Unit not found for owner");
        }

        @Test
        @DisplayName("Create owner with unit not belonging to building → throws InvalidOperationException")
        void createOwnerWithUnitNotBelongingToBuilding_throwsInvalidOperationException() throws UserAlreadyExistsException {
            // Given: Basic identity setup (required before unit/building validation)
            when(userService.findByEmail(NORMALIZED_EMAIL)).thenReturn(null);
            
            Identity savedIdentity = anIdentity(NORMALIZED_EMAIL, USER_ID_123);
            when(userService.createUser(any(Identity.class))).thenReturn(savedIdentity);
            
            Building testBuilding = aBuilding(BUILDING_ID_123);
            Building differentBuilding = aBuilding(DIFFERENT_BUILDING_ID);
            Unit testUnit = aUnit(UNIT_ID_123, differentBuilding);

            // When & Then: Should throw InvalidOperationException
            assertThatThrownBy(() -> ownerService.createOwner(testOwner, testBuilding, testUnit))
                .isInstanceOf(InvalidOperationException.class)
                .hasMessageContaining("Unit does not belong to the building");
        }
    }

    /**
     * Custom exception for concurrent test scenarios
     */
    private static class ConcurrentOwnerCreationException extends RuntimeException {
        public ConcurrentOwnerCreationException(String message, Throwable cause) {
            super(message, cause);
        }
    }

}

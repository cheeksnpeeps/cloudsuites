package com.cloudsuites.framework.webapp.rest.user;

import com.cloudsuites.framework.services.common.exception.NotFoundResponseException;
import com.cloudsuites.framework.services.property.personas.entities.Owner;
import com.cloudsuites.framework.services.property.personas.service.OwnerService;
import com.cloudsuites.framework.services.user.entities.Identity;
import com.cloudsuites.framework.webapp.CloudsuitesCoreApplication;
import com.cloudsuites.framework.webapp.GlobalExceptionHandler;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.cloudsuites.framework.webapp.rest.user.dto.OwnerDto;
import com.cloudsuites.framework.webapp.rest.user.mapper.OwnerMapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = CloudsuitesCoreApplication.class)
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.yml")  // Specify your properties file
class OwnerRestControllerTest {

    @Value("${web.datasource.url}")
    private String datasourceUrl;
    private MockMvc mockMvc;
    @Mock
    private OwnerService ownerService;
    @Mock
    private OwnerMapper mapper;
    @InjectMocks
    private OwnerRestController ownerRestController;
    private ObjectMapper objectMapper;
    @MockBean
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void contextLoads() {
        // Just loading the context to see if it picks up the local profile
    }

    @Test
    void testDatasourceUrl() {
        System.out.println("Datasource URL: " + datasourceUrl);
        // You can add assertions here if needed
    }

    @BeforeEach
    void setUp() {
        openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(ownerRestController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void testGetAllOwners_Success() throws Exception {
        String ownerId = "OW-01J3M43433R0TR0ZXR8M4ZDC9T";
        String ownerId2 = "OW-01J3M43433R0TR0ZXR8M4ZDC9T";
        Owner owner = new Owner(); // create a sample owner object
        owner.setOwnerId("OW-01J3M43433R0TR0ZXR8M4ZDC9T");
        Owner owner2 = new Owner(); // create a sample owner object
        owner2.setOwnerId("OW-01J3M43433R0TR0ZXR8M4ZDC9U");
        List<Owner> owners = List.of(owner, owner2);
        OwnerDto ownerDto = new OwnerDto(); // create a sample owner DTO
        ownerDto.setOwnerId(ownerId);
        OwnerDto ownerDto2 = new OwnerDto(); // create a sample owner DTO
        ownerDto2.setOwnerId(ownerId2);
        when(ownerService.getAllOwners()).thenReturn(owners);
        when(mapper.convertToDTOList(owners)).thenReturn(List.of(ownerDto, ownerDto2));

        mockMvc.perform(get("/api/v1/owners"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$[0].ownerId").value(ownerId))
                .andExpect(jsonPath("$[1].ownerId").value(ownerId2))
                .toString();
        verify(ownerService).getAllOwners();
        verify(mapper).convertToDTOList(owners);
    }

    @Test
    void testGetOwnerById_Success() throws Exception {
        String ownerId = "OW-01J3M43433R0TR0ZXR8M4ZDC9T";
        Owner owner = new Owner(); // create a sample owner object
        owner.setOwnerId(ownerId);
        OwnerDto ownerDto = new OwnerDto(); // create a sample owner DTO
        ownerDto.setOwnerId(ownerId);
        when(ownerService.getOwnerById(ownerId)).thenReturn(owner);
        when(mapper.convertToDTO(owner)).thenReturn(ownerDto);

        mockMvc.perform(get("/api/v1/owners/{ownerId}", ownerId))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId));

        verify(ownerService).getOwnerById(ownerId);
        verify(mapper).convertToDTO(owner);
    }

    @Test
    void testGetOwnerById_NotFound() throws Exception {
        String ownerId = "1";
        when(ownerService.getOwnerById(ownerId)).thenThrow(new NotFoundResponseException("Owner not found"));
        mockMvc.perform(get("/api/v1/owners/{ownerId}", ownerId))
                .andExpect(status().isNotFound());
        verify(ownerService).getOwnerById(ownerId);
    }

    @Test
    void testUpdateOwner_Success() throws Exception {
        String ownerId = "OW-01J3M43433R0TR0ZXR8M4ZDC9T";
        OwnerDto ownerDto = new OwnerDto(); // create a sample owner DTO
        ownerDto.setOwnerId(ownerId);
        IdentityDto identityDto = new IdentityDto();
        identityDto.setUsername("chmomar");
        ownerDto.setIdentity(identityDto);
        Owner owner = new Owner(); // create a sample owner object
        owner.setOwnerId(ownerId);
        Identity identity = new Identity();
        identity.setUsername("chmomar");
        owner.setIdentity(identity);
        when(mapper.convertToEntity(ownerDto)).thenReturn(owner);
        when(ownerService.updateOwner(eq(ownerId), any(Owner.class))).thenReturn(owner);
        when(mapper.convertToDTO(owner)).thenReturn(ownerDto);

        mockMvc.perform(put("/api/v1/owners/{ownerId}", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDto)))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.ownerId").value(ownerId))
                .andExpect(jsonPath("$.identity.username").value("chmomar"));
        verify(ownerService).updateOwner(eq(ownerId), any(Owner.class));
        verify(mapper).convertToDTO(owner);
    }

    @Test
    void testUpdateOwner_NotFound() throws Exception {
        String ownerId = "1";
        OwnerDto ownerDto = new OwnerDto(); // create a sample owner DTO
        when(mapper.convertToEntity(ownerDto)).thenReturn(new Owner());
        when(ownerService.updateOwner(eq(ownerId), any(Owner.class))).thenThrow(new NotFoundResponseException("Owner not found"));

        mockMvc.perform(put("/api/v1/owners/{id}", ownerId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ownerDto)))
                .andExpect(status().isNotFound());

        verify(ownerService).updateOwner(eq(ownerId), any(Owner.class));
    }
}
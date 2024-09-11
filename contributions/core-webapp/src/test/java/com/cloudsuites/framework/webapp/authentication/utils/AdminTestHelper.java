package com.cloudsuites.framework.webapp.authentication.utils;

import com.cloudsuites.framework.services.user.entities.AdminRole;
import com.cloudsuites.framework.services.user.entities.AdminStatus;
import com.cloudsuites.framework.webapp.rest.user.dto.AdminDto;
import com.cloudsuites.framework.webapp.rest.user.dto.IdentityDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
public class AdminTestHelper {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private final String validBuildingId;
    private final String validUnitId;

    public AdminTestHelper(MockMvc mockMvc, ObjectMapper objectMapper, String validBuildingId, String validUnitId) {
        this.mockMvc = mockMvc;
        this.objectMapper = objectMapper;
        this.validBuildingId = validBuildingId;
        this.validUnitId = validUnitId;
    }

    public String registerAdminAndGetToken(String username, String phoneNumber) throws Exception {
        // Register the admin
        AdminDto admin = new AdminDto();
        admin.setRole(AdminRole.SUPER_ADMIN);
        admin.setStatus(AdminStatus.ACTIVE);
        IdentityDto identity = new IdentityDto();
        identity.setEmail(username + "@gmail.com");
        identity.setPhoneNumber(phoneNumber);
        admin.setIdentity(identity);

        MockHttpServletResponse response;

        response = mockMvc.perform(post("/api/v1/auth/admins/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(admin)))
                .andExpect(status().isOk())
                .andReturn().getResponse();

        // Assuming the response contains the admin ID for OTP verification
        String adminId = extractAdminIdFromResponse(response);

        // Verify OTP
        String otp = "123456"; // Assume this OTP is valid
        response = mockMvc.perform(post("/api/v1/auth/admins/{adminId}/verify-otp", adminId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("otp", otp))
                .andExpect(status().isOk()).andReturn().getResponse();

        ObjectMapper objectMapper = new ObjectMapper();
        Map<String, String> data = objectMapper.readValue(response.getContentAsString(), Map.class);
        System.out.println(response.getContentAsString());
        System.out.println(data.get("token"));
        return data.get("token");
    }

    private String extractAdminIdFromResponse(MockHttpServletResponse response) throws Exception {
        // Parse the response to extract the adminId, depending on your response structure
        // Example:
        AdminDto adminDto = objectMapper.readValue(response.getContentAsString(), AdminDto.class);
        return adminDto.getAdminId(); // Adjust according to your DTO structure
    }
}

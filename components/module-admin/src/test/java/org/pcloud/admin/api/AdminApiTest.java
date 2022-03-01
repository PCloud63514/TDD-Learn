package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.domain.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AdminApiTest {
    private MockMvc mockMvc;
    private SpyAdminService spyAdminService;
    private ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        spyAdminService = new SpyAdminService();
        mockMvc = MockMvcBuilders.standaloneSetup(new AdminApi(spyAdminService)).build();
    }

    @Test
    void joinAdmin_returnCreatedHttpStatus() throws Exception {
        mockMvc.perform(post("/admin/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isCreated());
    }

    @Test
    void joinAdmin_returnAdmin() throws Exception {
        spyAdminService.joinAdmin_returnValue = new Admin("id", "password", "ADMIN", "Default", true,
                LocalDateTime.of(2022, 2, 22, 20, 20, 20));

        mockMvc.perform(post("/admin/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(jsonPath("$.id", equalTo("id")))
                .andExpect(jsonPath("$.password", equalTo("password")))
                .andExpect(jsonPath("$.role", equalTo("ADMIN")))
                .andExpect(jsonPath("$.status", equalTo("Default")))
                .andExpect(jsonPath("$.needChangePassword", equalTo(true)))
                .andExpect(jsonPath("$.createAt", equalTo("2022-02-22 20:20:20")))
                .andExpect(status().isCreated());
    }

    @Test
    void joinAdmin_passesIdAndPasswordToService() throws Exception {
        String givenId = "id";
        String givenPassword = "password";
        AdminJoinRequest adminJoinRequest = new AdminJoinRequest(givenId, givenPassword);

        mockMvc.perform(post("/admin/join")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(adminJoinRequest)));

        assertThat(spyAdminService.joinAdmin_argumentRequest.getId()).isEqualTo(givenId);
        assertThat(spyAdminService.joinAdmin_argumentRequest.getPassword()).isEqualTo(givenPassword);
    }

    @Test
    void getAdmins_returnOkHttpStatus() throws Exception {
        mockMvc.perform(get("/admin"))
                .andExpect(status().isOk());
    }

    @Test
    void getAdmins_returnAdmins() {

    }
}
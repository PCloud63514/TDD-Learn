package org.pcloud.admin.api;

import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.domain.Admin;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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
        spyAdminService.joinAdmin_returnValue = Admin.builder()
                .id("id")
                .password("password")
                .role("ADMIN")
                .status("Default")
                .needChangePassword(true)
                .createAt(LocalDateTime.of(2022, 2, 22, 20, 20, 20))
                .build();

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
    void getAdmins_returnAdmins() throws Exception {
        spyAdminService.getAdmins_returnValue = List.of(new AdminSearchResponse("id", "ADMIN",
                "Default", true, LocalDateTime.of(2022, 2, 22, 20, 20, 20)));

        mockMvc.perform(get("/admin"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$[0].id", equalTo("id")))
                .andExpect(jsonPath("$[0].role", equalTo("ADMIN")))
                .andExpect(jsonPath("$[0].status", equalTo("Default")))
                .andExpect(jsonPath("$[0].needChangePassword", equalTo(true)))
                .andExpect(jsonPath("$[0].createAt", equalTo("2022-02-22 20:20:20")))
                .andDo(print());
    }

    @Test
    void getAdmins_returnAdminsZero() throws Exception {
        spyAdminService.getAdmins_returnValue = List.of();

        mockMvc.perform(get("/admin")
                        .param("offset", "0")
                        .param("size", "1"))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getAdmins_passesPageRequestToService() throws Exception {
        Long givenOffset = 0L;
        Integer givenPageSize = 1;

        mockMvc.perform(get("/admin")
                .param("offset", givenOffset.toString())
                .param("size", givenPageSize.toString()));

        assertThat(spyAdminService.getAdmins_argumentRequest.getOffset()).isEqualTo(givenOffset);
        assertThat(spyAdminService.getAdmins_argumentRequest.getPageSize()).isEqualTo(givenPageSize);
    }

    @Test
    void duplicateIdCheck_returnOkHttpStatus() throws Exception {
        mockMvc.perform(get("/admin/duplicate/id/{id}", "qwe"))
                .andExpect(status().isOk());
    }

    @Test
    void duplicateIdCheck_returnResult() throws Exception {
        spyAdminService.duplicateIdCheck_returnValue = true;

        mockMvc.perform(get("/admin/duplicate/id/{id}", "qwe"))
                .andExpect(content().string(equalTo("true")));
    }

    @Test
    void duplicatedIdCheck_passesIdToService() throws Exception {
        mockMvc.perform(get("/admin/duplicate/id/{id}", "id"));

        assertThat(spyAdminService.duplicateIdCheck_argumentId).isEqualTo("id");
    }
}
package org.pcloud.admin.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.admin.data.request.AdminJoinRequest;
import org.pcloud.admin.data.request.AdminPasswordInitialRequest;
import org.pcloud.admin.data.response.AdminSearchResponse;
import org.pcloud.admin.data.response.AdminGetsResponse;
import org.pcloud.admin.domain.Admin;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        int givenTotal = 1;
        List<AdminSearchResponse> givenAdminSearchResponses = List.of(new AdminSearchResponse("id", "ADMIN", "Default", true, LocalDateTime.of(2022, 2, 22, 20, 20, 20)));
        spyAdminService.getAdmins_returnValue = new AdminGetsResponse(givenTotal, givenAdminSearchResponses);

        mockMvc.perform(get("/admin"))
                .andExpect(jsonPath("$.total", equalTo(givenTotal)))
                .andExpect(jsonPath("$.list").isArray())
                .andExpect(jsonPath("$.list[0].id", equalTo("id")))
                .andExpect(jsonPath("$.list[0].role", equalTo("ADMIN")))
                .andExpect(jsonPath("$.list[0].status", equalTo("Default")))
                .andExpect(jsonPath("$.list[0].needChangePassword", equalTo(true)))
                .andExpect(jsonPath("$.list[0].createAt", equalTo("2022-02-22 20:20:20")))
                .andDo(print());
    }

    @Test
    void getAdmins_returnAdminsZero() throws Exception {
        int givenTotal = 0;
        spyAdminService.getAdmins_returnValue = new AdminGetsResponse(givenTotal, List.of());

        mockMvc.perform(get("/admin")
                        .param("offset", "0")
                        .param("size", "1"))
                .andExpect(jsonPath("$.total", equalTo(givenTotal)))
                .andExpect(jsonPath("$.list").isArray());
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

    @Test
    void passwordInitial_returnOkHttpStatus() throws Exception {
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest("id2");

        mockMvc.perform(patch("/admin/init/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void passwordInitial_returnAdmin() throws Exception {
        String givenId = "id2";
        String givenPassword = "initPassword2";
        spyAdminService.passwordInit_returnValue = Admin.create(givenId, givenPassword, "ADMIN", "Default", LocalDateTime.now());

        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest(givenId);

        mockMvc.perform(patch("/admin/init/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(jsonPath("$.id", equalTo("id2")));
    }

    @Test
    void passwordInitial_passesAdminPasswordInitialRequestRoService() throws Exception {
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest("id2");

        mockMvc.perform(patch("/admin/init/password")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenRequest)));

        assertThat(spyAdminService.passwordInit_argumentRequest.getId()).isEqualTo(givenRequest.getId());
    }

    @Test
    void passwordInitial_returnAdminToService() throws Exception {
        String givenId = "id2";
        String givenPassword = "initPassword2";
        AdminPasswordInitialRequest givenRequest = new AdminPasswordInitialRequest(givenId);
        spyAdminService.passwordInit_returnValue = Admin.create(givenId, givenPassword, "ADMIN", "Default", LocalDateTime.now());

        mockMvc.perform(patch("/admin/init/password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(jsonPath("$.id", equalTo(givenId)))
                .andExpect(jsonPath("$.password", equalTo(givenPassword)));
    }

    @Test
    void login_okHttpStatus() throws Exception {
        mockMvc.perform(post("/admin/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk());
    }

    @Test
    void login_passesLoginRequestToService() throws Exception {
        mockMvc.perform(post("/admin/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":\"id\", \"password\":\"password\"}"));

        assertThat(spyAdminService.login_argumentRequest.getId()).isEqualTo("id");
        assertThat(spyAdminService.login_argumentRequest.getPassword()).isEqualTo("password");
    }
}
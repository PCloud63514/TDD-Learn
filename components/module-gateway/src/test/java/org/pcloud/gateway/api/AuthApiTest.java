package org.pcloud.gateway.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    SpyAuthService spyAuthService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        spyAuthService = new SpyAuthService();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthApi(spyAuthService)).build();
    }

    @Test
    void issueToken_createdHttpStatus() throws Exception {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyAuthService.generateToken_returnValue = new JwtToken("token", "refresh");

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void issueToken_returnValue() throws Exception {
        String givenStrToken = "token2";
        String givenStrRefresh = "refresh2";
        spyAuthService.generateToken_returnValue = new JwtToken(givenStrToken, givenStrRefresh);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(jsonPath("$.token", equalTo(givenStrToken)))
                .andExpect(jsonPath("$.refresh", equalTo(givenStrRefresh)));

        assertThat(spyAuthService.generateToken_returnValue.getToken()).isEqualTo(givenStrToken);
        assertThat(spyAuthService.generateToken_returnValue.getRefresh()).isEqualTo(givenStrRefresh);
    }

    @Test
    void issueToken_passesTokenIssueRequestToAuthService() throws Exception {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        givenData.put("userId", 1);
        givenData.put("userName", "PCloud");
        givenData.put("item", List.of("1번", "2번", "3번"));
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyAuthService.generateToken_returnValue = new JwtToken("token", "refresh");

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenRequest)));

        assertThat(spyAuthService.generateToken_argumentRequest.getRole()).isEqualTo(givenRole);
        assertThat(spyAuthService.generateToken_argumentRequest.getData()).isEqualTo(givenData);
        assertThat(spyAuthService.generateToken_argumentRequest.getValidity()).isEqualTo(givenValidity);
        assertThat(spyAuthService.generateToken_argumentRequest.getRefreshValidity()).isEqualTo(givenRefreshValidity);
    }

    @Test
    void breakToken_okHttpStatus() throws Exception {
        mockMvc.perform(delete("/auth/{token}", "token"))
                .andExpect(status().isOk());
    }

    @Test
    void breakToken_passesTokenToAuthService() throws Exception {
        String givenToken = "token";

        mockMvc.perform(delete("/auth/{token}", givenToken))
                .andExpect(status().isOk());

        assertThat(spyAuthService.deleteToken_argumentToken).isEqualTo(givenToken);
    }
}
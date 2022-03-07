package org.pcloud.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void issueToken_returnValue() throws Exception {
        String givenStrToken = "token2";
        spyAuthService.generateToken_returnValue = new Token(givenStrToken);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(jsonPath("$.token", equalTo(givenStrToken)));

        assertThat(spyAuthService.generateToken_returnValue.getToken()).isEqualTo(givenStrToken);
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

        mockMvc.perform(post("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(givenRequest)));

        assertThat(spyAuthService.generateToken_argumentRequest.getRole()).isEqualTo(givenRole);
        assertThat(spyAuthService.generateToken_argumentRequest.getData()).isEqualTo(givenData);
        assertThat(spyAuthService.generateToken_argumentRequest.getValidity()).isEqualTo(givenValidity);
        assertThat(spyAuthService.generateToken_argumentRequest.getRefreshValidity()).isEqualTo(givenRefreshValidity);
    }
}
package org.pcloud.security.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthApiTest {
    MockMvc mockMvc;
    ObjectMapper objectMapper;
    SpyJwtTokenProvider spyJwtTokenProvider;
    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        mockMvc = MockMvcBuilders.standaloneSetup(new AuthApi(spyJwtTokenProvider)).build();
    }

    @Test
    void issueToken_createdHttpStatus() throws Exception {
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;

        TokenIssueRequest givenRequest = new TokenIssueRequest(givenRole, givenData, givenValidity, givenRefreshValidity);

        mockMvc.perform(post("/auth")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(givenRequest)))
                .andExpect(status().isCreated());
    }

    @Test
    void issueToken_returnValue() {

    }
}
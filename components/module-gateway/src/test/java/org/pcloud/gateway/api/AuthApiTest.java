package org.pcloud.gateway.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.gateway.data.response.JwtTokenResponse;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.reactive.function.BodyInserters;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class AuthApiTest {
    private WebTestClient webTestClient;
    ObjectMapper objectMapper;
    SpyAuthService spyAuthService;

    @BeforeEach
    void setUp() {
        objectMapper = new ObjectMapper();
        spyAuthService = new SpyAuthService();
        webTestClient = WebTestClient.bindToController(new AuthApi(spyAuthService))
                .configureClient()
                .build();
    }

    @Test
    void issueToken_createdHttpStatus() throws Exception {
        String givenIssueRequestDomain = "domain";
        String givenRole = "ROLE_role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyAuthService.generateToken_returnValue = new JwtToken("token", "refresh");

        webTestClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(givenRequest))
                .exchange().expectStatus().isCreated();
    }

    @Test
    void issueToken_returnValue() throws Exception {
        String givenStrToken = "token2";
        String givenStrRefresh = "refresh2";
        spyAuthService.generateToken_returnValue = new JwtToken(givenStrToken, givenStrRefresh);

        webTestClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject("{}"))
                .exchange()
                .expectBody()
                .jsonPath("$.accessToken").isEqualTo(givenStrToken)
                .jsonPath("$.refreshToken").isEqualTo(givenStrRefresh);

        assertThat(spyAuthService.generateToken_returnValue.getToken()).isEqualTo(givenStrToken);
        assertThat(spyAuthService.generateToken_returnValue.getRefresh()).isEqualTo(givenStrRefresh);
    }

    @Test
    void issueToken_passesTokenIssueRequestToAuthService() throws Exception {
        String givenIssueRequestDomain = "domain";
        String givenRole = "ROLE_role";
        Map<String, Object> givenData = new HashMap<>();
        givenData.put("userId", 1);
        givenData.put("userName", "PCloud");
        givenData.put("item", List.of("1번", "2번", "3번"));
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyAuthService.generateToken_returnValue = new JwtToken("token", "refresh");

        webTestClient.post()
                .uri("/auth")
                .contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromObject(givenRequest))
                .exchange();

        assertThat(spyAuthService.generateToken_argumentRequest.getRole()).isEqualTo(givenRole);
        assertThat(spyAuthService.generateToken_argumentRequest.getData()).isEqualTo(givenData);
        assertThat(spyAuthService.generateToken_argumentRequest.getValidity()).isEqualTo(givenValidity);
        assertThat(spyAuthService.generateToken_argumentRequest.getRefreshValidity()).isEqualTo(givenRefreshValidity);
    }

    @Test
    void breakToken_okHttpStatus() throws Exception {
        webTestClient.delete()
                .uri("/auth/{token}", "token")
                .exchange()
                .expectStatus().isOk();
    }

    @Test
    void breakToken_passesTokenToAuthService() throws Exception {
        String givenToken = "token";

        webTestClient.delete()
                .uri("/auth/{token}", "token")
                .exchange()
                .expectStatus().isOk();

        assertThat(spyAuthService.deleteToken_argumentToken).isEqualTo(givenToken);
    }
}
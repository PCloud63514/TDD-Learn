package org.pcloud.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.api.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.jwt.JwtToken;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class AuthServiceImplTest {
    AuthServiceImpl authService;
    SpyJwtTokenProvider spyJwtTokenProvider;

    @BeforeEach
    void setUp() {
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(spyJwtTokenProvider);
    }

    @Test
    void generateToken_returnValue() {
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        Token token = authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_returnValue.getToken()).isEqualTo(token.getToken());
    }

    @Test
    void generateToken_passesJwtTokenGenerateRequestToJwtTokenProvider() {
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenRole, givenData, givenValidity, givenRefreshValidity);

        authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRole()).isEqualTo(givenRole);
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getValidityMS()).isEqualTo(givenValidity);
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRefreshValidityMS()).isEqualTo(givenRefreshValidity);
    }
}
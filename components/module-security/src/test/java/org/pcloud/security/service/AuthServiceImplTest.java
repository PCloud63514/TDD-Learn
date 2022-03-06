package org.pcloud.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.api.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Spy
    @InjectMocks
    RedisTemplate<String, Object> mockRedisTemplate;
    AuthServiceImpl authService;
    SpyJwtTokenProvider spyJwtTokenProvider;
    @Mock
    HashOperations mockHashOperations;

    @BeforeEach
    void setUp() {
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(spyJwtTokenProvider, mockRedisTemplate);
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
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRole()).isEqualTo(givenRole);
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getValidityMS()).isEqualTo(givenValidity);
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRefreshValidityMS()).isEqualTo(givenRefreshValidity);
    }

    @Test
    void generateToken_passesDataToOpsForHashInPutAll() {
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        givenData.put("userId", 1);
        givenData.put("userName", "PCloud");
        givenData.put("item", List.of("1번", "2번", "3번"));
        String givenToken = "token2";
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken(givenToken, "refresh");
        doReturn(mockHashOperations).when(mockRedisTemplate).opsForHash();
        doReturn(true).when(mockRedisTemplate).expire(anyString(), any(), any());
//        doReturn(true)
//                .when(mockRedisTemplate)
//                .expire(any(), any(), any());

        authService.generateToken(givenRequest);

        verify(mockRedisTemplate).opsForHash();
        verify(mockRedisTemplate.opsForHash()).putAll(eq(givenToken), eq(givenData));
        verify(mockRedisTemplate).expire(any(), any(), any());
//        verify(mockRedisTemplate).expire(eq(givenToken), eq(givenRefreshValidity), eq(TimeUnit.MILLISECONDS));
    }
}
package org.pcloud.security.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    RedisTemplate<String, Object> mockRedisTemplate;
    AuthServiceImpl authService;
    SpyJwtTokenProvider spyJwtTokenProvider;
    StubUuidProvider stubUuidProvider;
    @Mock
    HashOperations mockHashOperations;
    @Mock
    ValueOperations mockValueOperations;
    @Mock
    RedisConnection redisConnectionMock;
    @Mock
    RedisConnectionFactory redisConnectionFactoryMock;

    @BeforeEach
    void setUp() {
        //Mockito.lenient() < Loose Stubbing  : 불필요한 Stubbing은 넘어간다.
        //                  < Strict Stubbing : 처리되지 않은 Stubbing은 에러처리한다.
        Mockito.lenient().doReturn(mockHashOperations).when(mockRedisTemplate).opsForHash();
        Mockito.lenient().doReturn(mockValueOperations).when(mockRedisTemplate).opsForValue();
        stubUuidProvider = new StubUuidProvider();
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(spyJwtTokenProvider, mockRedisTemplate, stubUuidProvider);
    }

    @Test
    void generateToken_returnValue() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        JwtToken jwtToken = authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_returnValue.getToken()).isEqualTo(jwtToken.getToken());
        assertThat(spyJwtTokenProvider.generate_returnValue.getRefresh()).isEqualTo(jwtToken.getRefresh());
    }

    @Test
    void generateToken_passesTokenAndRefreshToSetOfOpsForValue() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForValue()).set(eq(jwtToken.getToken()), eq(jwtToken.getRefresh()), eq(givenValidity), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void generateToken_passesToPutAllOfOpsForHash() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        String givenToken = "token2";
        String givenRefresh = "refresh2";
        UUID givenUUID = UUID.randomUUID();
        stubUuidProvider.randomUUID_returnValue = givenUUID;
        spyJwtTokenProvider.generate_returnValue = new JwtToken(givenToken, givenRefresh);
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("role", givenRole);
        tokenInfo.put("tokenProviderDomain", givenIssueRequestDomain);
        tokenInfo.put("validity", givenValidity);
        tokenInfo.put("refreshValidity", givenRefreshValidity);
        tokenInfo.put("token", givenToken);
        tokenInfo.put("secret-key", givenUUID.toString());
        //when
        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForHash()).putAll(eq(jwtToken.getRefresh()), eq(tokenInfo));
        verify(mockRedisTemplate.opsForHash()).putAll(eq(givenUUID.toString()), eq(givenData));
    }

    @Test
    void generateToken_passesJwtTokenGenerateRequestToJwtTokenProvider() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_argumentRequest.getValidityMS()).isEqualTo(givenValidity);
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRefreshValidityMS()).isEqualTo(givenRefreshValidity);
    }

    @Test
    void deleteToken_passesTokenToRedisTemplate() {
        String givenToken = "givenToken1";
        doReturn(true).when(mockRedisTemplate).delete(anyString());

        authService.deleteToken(givenToken);

        verify(mockRedisTemplate).delete(eq(givenToken));
    }
}
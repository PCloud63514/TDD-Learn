package org.pcloud.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.data.AuthInformation;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    RedisTemplate<String, Object> mockRedisTemplate;
    AuthServiceImpl authService;
    SpyJwtTokenProvider spyJwtTokenProvider;
    StubUuidProvider stubUuidProvider;
    //    @Mock
//    ObjectMapper mockObjectMapper;
    @Mock
    HashOperations mockHashOperations;
    @Mock
    ValueOperations mockValueOperations;
    final String givenToken = "token";
    final String givenTokenNull = "tokenNull";
    final String givenRefresh = "refresh";
    final String givenRefreshNull = "refreshNull";

    @BeforeEach
    void setUp() {
        //Mockito.lenient() < Loose Stubbing  : 불필요한 Stubbing은 넘어간다.
        //                  < Strict Stubbing : 처리되지 않은 Stubbing은 에러처리한다.
        Mockito.lenient().doReturn(mockHashOperations).when(mockRedisTemplate).opsForHash();
        Mockito.lenient().doReturn(mockValueOperations).when(mockRedisTemplate).opsForValue();
        Mockito.lenient().doReturn(givenRefresh).when(mockValueOperations).get(givenToken);
        Mockito.lenient().doReturn(null).when(mockValueOperations).get(givenTokenNull);
        Mockito.lenient().doReturn("authInformation").when(mockValueOperations).get(givenRefresh);
        Mockito.lenient().doReturn(null).when(mockValueOperations).get(givenRefreshNull);

        stubUuidProvider = new StubUuidProvider();
        spyJwtTokenProvider = new SpyJwtTokenProvider();

        authService = new AuthServiceImpl(spyJwtTokenProvider, mockRedisTemplate, stubUuidProvider);
    }

    private TokenIssueRequest getOkTokenIssueRequest() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        return new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
    }

    private TokenIssueRequest getWantValidityAndRefreshValidityTokenIssueRequest(long validity, long refreshValidity) {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        return new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, validity, refreshValidity);
    }

    @Test
    void generateToken_returnValue() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");

        JwtToken jwtToken = authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_returnValue.getToken()).isEqualTo(jwtToken.getToken());
        assertThat(spyJwtTokenProvider.generate_returnValue.getRefresh()).isEqualTo(jwtToken.getRefresh());
    }

    @Test
    void generateToken_passesRequestToGenerateOfJwtTokenProvider() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();

        authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_argumentRequest.getValidityMS()).isEqualTo(givenRequest.getValidity());
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRefreshValidityMS()).isEqualTo(givenRequest.getRefreshValidity());
    }

    @Test
    void generateToken_passesTokenToSetOfOpsForValue() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();
        spyJwtTokenProvider.generate_returnValue = new JwtToken(givenToken, givenRefresh);

        authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForValue()).set(eq(givenToken), eq(givenRefresh), eq(givenRequest.getValidity()), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate.opsForValue()).set(eq(givenRefresh), any(AuthInformation.class), eq(givenRequest.getRefreshValidity()), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void generateToken_passesDataToPutAllOfOpsForHash() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();

        authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForHash()).putAll(eq(stubUuidProvider.randomUUID().toString()), eq(givenRequest.getData()));
    }

    @Test
    void generateToken_passesToExpireOfRedisTemplate() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();

        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(mockRedisTemplate).expire(eq(jwtToken.getToken()), eq(givenRequest.getValidity()), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(jwtToken.getRefresh()), eq(givenRequest.getRefreshValidity()), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(stubUuidProvider.randomUUID().toString()), eq(givenRequest.getRefreshValidity()), eq(TimeUnit.MILLISECONDS));
    }
}
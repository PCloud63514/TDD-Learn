package org.pcloud.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.security.data.AuthDataInformation;
import org.pcloud.security.data.AuthInformation;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
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
    @Mock
    ObjectMapper mockObjectMapper;
    @Mock
    HashOperations mockHashOperations;
    @Mock
    ValueOperations mockValueOperations;

    @BeforeEach
    void setUp() {
        //Mockito.lenient() < Loose Stubbing  : 불필요한 Stubbing은 넘어간다.
        //                  < Strict Stubbing : 처리되지 않은 Stubbing은 에러처리한다.
        Mockito.lenient().doReturn(mockHashOperations).when(mockRedisTemplate).opsForHash();
        Mockito.lenient().doReturn(mockValueOperations).when(mockRedisTemplate).opsForValue();

        stubUuidProvider = new StubUuidProvider();
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(spyJwtTokenProvider, mockRedisTemplate, stubUuidProvider, mockObjectMapper);
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
    void generateToken_passesToSetOfOpsForValue() throws JsonProcessingException {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        UUID givenUUID = UUID.randomUUID();
        stubUuidProvider.randomUUID_returnValue = givenUUID;
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");
        AuthInformation authInformation = new AuthInformation(givenRole, givenIssueRequestDomain, givenValidity, givenRefreshValidity, "token2", givenUUID.toString());
        String jsonAuthInformation = mockObjectMapper.writeValueAsString(authInformation);

        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForValue()).set(eq(jwtToken.getToken()), eq(jwtToken.getRefresh()), eq(givenValidity), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate.opsForValue()).set(eq(jwtToken.getRefresh()), eq(jsonAuthInformation), eq(givenRefreshValidity), eq(TimeUnit.MILLISECONDS));
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
        //when
        authService.generateToken(givenRequest);

        verify(mockRedisTemplate.opsForHash()).putAll(eq(givenUUID.toString()), eq(givenData));
    }

    @Test
    void generateToken_passesToExpireOfRedisTemplate() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        UUID givenUUID = UUID.randomUUID();
        stubUuidProvider.randomUUID_returnValue = givenUUID;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("token2", "refresh");
        doReturn(true).when(mockRedisTemplate).expire(anyString(), anyLong(), eq(TimeUnit.MILLISECONDS));
        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(mockRedisTemplate).expire(eq(jwtToken.getToken()), eq(givenValidity), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(jwtToken.getRefresh()), eq(givenRefreshValidity), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(givenUUID.toString()), eq(givenRefreshValidity), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    void generateToken_passesToWriteValueAsStringOfObjectMapper() throws JsonProcessingException {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        TokenIssueRequest givenRequest = new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
        spyJwtTokenProvider.generate_returnValue = new JwtToken("givenToken", "givenRefresh");
        stubUuidProvider.randomUUID_returnValue = UUID.randomUUID();

        authService.generateToken(givenRequest);

        verify(mockObjectMapper).writeValueAsString(any());
    }

    @Test
    void deleteToken_passesTokenToRedisTemplate() {
        String givenToken = "givenToken1";
        doReturn(true).when(mockRedisTemplate).delete(anyString());

        authService.deleteToken(givenToken);

        verify(mockRedisTemplate).delete(eq(givenToken));
    }

    @Test
    void getAuthDataInformation_passesToGetInformationOfJwtProvider() {
        String givenToken = "token";
        doReturn("refresh").when(mockValueOperations).get(givenToken);

        authService.getAuthDataInformation(givenToken);

        assertThat(spyJwtTokenProvider.getInformation_argumentToken).isEqualTo(givenToken);
    }

    @Test
    void getAuthDataInformation_throwRuntimeException() {
        String givenToken = "token";
        spyJwtTokenProvider.getInformation_isRuntimeException = true;

        assertThrows(RuntimeException.class, ()-> {
            authService.getAuthDataInformation(givenToken);
        });
    }

    @Test
    void getAuthDataInformation_passesToGetOfRedisTemplateOpsForValue() {
        String givenToken = "token";
        doReturn("refresh").when(mockValueOperations).get(givenToken);

        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForValue()).get(eq(givenToken));
        verify(mockRedisTemplate.opsForValue()).get(eq("refresh"));
    }

    @Test
    void getAuthDataInformation_RuntimeExceptionToGetOfOpsForValue() {
        String givenToken = "token";
        doReturn(null).when(mockValueOperations).get(givenToken);

        assertThrows(RuntimeException.class, ()-> {
            authService.getAuthDataInformation(givenToken);
        });
    }

    @Test
    void getAuthDataInformation_passesToReadValueOfObjectMapper() throws JsonProcessingException {
        String givenToken = "token";
        AuthInformation givenAuthInformation = new AuthInformation("role", "tpd", 0, 0, givenToken, "secretKey");
        doReturn("refresh").when(mockValueOperations).get(givenToken);
        doReturn("authInformation").when(mockValueOperations).get("refresh");
        doReturn("anyString()").when(mockValueOperations).get(anyString());
        doReturn(givenAuthInformation).when(mockObjectMapper).readValue("authInformation", AuthInformation.class);

        authService.getAuthDataInformation(givenToken);

        verify(mockObjectMapper).readValue(anyString(),  eq(AuthInformation.class));
    }

    @Test
    void getAuthDataInformation_passesToEntriesOfOpsForHash() throws JsonProcessingException {
        String givenToken = "token";
        AuthInformation givenAuthInformation = new AuthInformation("role", "tpd", 0, 0, givenToken, "secretKey");

        doReturn("refresh").when(mockValueOperations).get(givenToken);
        doReturn("authInformation").when(mockValueOperations).get("refresh");
        doReturn(givenAuthInformation).when(mockObjectMapper).readValue("authInformation", AuthInformation.class);
//        doReturn(any()).when(mockHashOperations).entries(givenAuthInformation.getSecretKey());
//        doReturn("anyString()").when(mockValueOperations).get(anyString());

        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForHash()).entries(eq(givenAuthInformation.getSecretKey()));
    }

    @Test
    void getAuthDataInformation_RunTimeExceptionToPassesRefreshToGetOfOpsForValue() {
        String givenToken = "token";
        AuthInformation givenAuthInformation = new AuthInformation("role", "tpd", 0, 0, givenToken, "secretKey");
        doReturn("refresh").when(mockValueOperations).get(givenToken);
        doReturn(givenAuthInformation).when(mockValueOperations).get("refresh");
    }
}
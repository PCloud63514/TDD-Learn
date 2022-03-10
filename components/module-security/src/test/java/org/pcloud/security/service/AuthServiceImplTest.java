package org.pcloud.security.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.security.api.SpyJwtTokenProvider;
import org.pcloud.security.data.AuthDataInformation;
import org.pcloud.security.data.AuthInformation;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
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
    HashOperations<String, String, Object> mockHashOperations;
    @Mock
    ValueOperations<String, Object> mockValueOperations;
    final String givenToken = "token";
    final String givenTokenNull = "tokenNull";
    final String givenRefresh = "refresh";
    final String givenRefreshNull = "refreshNull";
    final AuthInformation givenAuthInformation = new AuthInformation("role", "domain", 0, 0, givenToken, givenRefresh, "secretKey");

    @BeforeEach
    void setUp() {
        //Mockito.lenient() < Loose Stubbing  : 불필요한 Stubbing은 넘어간다.
        //                  < Strict Stubbing : 처리되지 않은 Stubbing은 에러처리한다.
        stubUuidProvider = new StubUuidProvider();
        spyJwtTokenProvider = new SpyJwtTokenProvider();
        authService = new AuthServiceImpl(spyJwtTokenProvider, mockRedisTemplate, stubUuidProvider);

        Mockito.lenient().doReturn(mockHashOperations).when(mockRedisTemplate).opsForHash();
        Mockito.lenient().doReturn(mockValueOperations).when(mockRedisTemplate).opsForValue();
        Mockito.lenient().doReturn(givenRefresh).when(mockValueOperations).get(givenToken);
        Mockito.lenient().doReturn(null).when(mockValueOperations).get(givenTokenNull);
        Mockito.lenient().doReturn(givenAuthInformation).when(mockValueOperations).get(givenRefresh);
        Mockito.lenient().doReturn(null).when(mockValueOperations).get(givenRefreshNull);
    }

    private TokenIssueRequest getOkTokenIssueRequest() {
        String givenIssueRequestDomain = "domain";
        String givenRole = "role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        return new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
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

    @Test
    void deleteToken_passesTokenToRedisTemplate() {

        authService.deleteToken(givenToken);

        verify(mockRedisTemplate).delete(eq(givenToken));
    }

    @Test
    void getAuthDataInformation_returnValue() {
        String givenRole = "role";
        String givenTokenProviderDomain = "domain";
        long _givenValidity = 99999;
        long _givenRefreshValidity = 919191919;
        String _givenToken = "_token2";
        String _givenRefresh = "_refresh2";
        String _givenSecretKey = "secretKey";
        LinkedHashMap<String, Object> givenData = new LinkedHashMap<>();
        AuthInformation _givenAuthInformation = new AuthInformation(givenRole, givenTokenProviderDomain, _givenValidity, _givenRefreshValidity, _givenToken, _givenRefresh, _givenSecretKey);
        doReturn(_givenRefresh).when(mockValueOperations).get(_givenToken);
        doReturn(_givenAuthInformation).when(mockValueOperations).get(_givenRefresh);
        doReturn(givenData).when(mockHashOperations).entries(_givenAuthInformation.getSecretKey());

        AuthDataInformation authDataInformation = authService.getAuthDataInformation(_givenToken);

        assertThat(authDataInformation.getRole()).isEqualTo(_givenAuthInformation.getRole());
        assertThat(authDataInformation.getTokenProviderDomain()).isEqualTo(_givenAuthInformation.getTokenProviderDomain());
        assertThat(authDataInformation.getValidity()).isEqualTo(_givenAuthInformation.getValidity());
        assertThat(authDataInformation.getRefreshValidity()).isEqualTo(_givenAuthInformation.getRefreshValidity());
        assertThat(authDataInformation.getToken()).isEqualTo(_givenAuthInformation.getToken());
        assertThat(authDataInformation.getRefresh()).isEqualTo(_givenAuthInformation.getRefresh());
        assertThat(authDataInformation.getSecretKey()).isEqualTo(_givenSecretKey);
        assertThat(authDataInformation.getData()).isEqualTo(givenData);
    }

    @Test
    void getAuthDataInformation_passesTokenToGetInformationOfJwtTokenProvider() {
        authService.getAuthDataInformation(givenToken);

        assertThat(spyJwtTokenProvider.getInformation_argumentToken).isEqualTo(givenToken);
    }

    @Test
    void getAuthDataInformation_passesTokenToGetOfOpsForValue() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForValue()).get(givenToken);
    }

    @Test
    void getAuthDataInformation_RunTimeExceptionToGiveTokenAndGetAnNull() {
        Assertions.assertThrows(RuntimeException.class, () ->
                authService.getAuthDataInformation(givenTokenNull));
    }

    @Test
    void getAuthDataInformation_passesRefreshToGetOfForValue() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForValue()).get(givenRefresh);
    }

    @Test
    void getAuthDataInformation_passesRefreshToGetOfOpsForValue() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForValue()).get(givenRefresh);
    }

    @Test
    void getAuthDataInformation_throwRuntimeExceptionToGiveRefreshAndGetAnNull() {
        doReturn(null).when(mockValueOperations).get(givenRefresh);
        Assertions.assertThrows(RuntimeException.class, () ->
                authService.getAuthDataInformation(givenToken));
    }

    @Test
    void getAuthDataInformation_throwRunTimeExceptionToCompareTokenAndGetTokenOfOpsForValue() {
        AuthInformation givenAuthInformation = new AuthInformation(null, null, 0, 0, "failToken", "failRefresh", null);
        doReturn(givenAuthInformation).when(mockValueOperations).get(givenRefresh);
        Assertions.assertThrows(RuntimeException.class, () ->
                authService.getAuthDataInformation(givenToken));
    }

    @Test
    void getAuthDataInformation_passesSecretKeyToEntriesOfOpsForHash() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForHash()).entries(eq(givenAuthInformation.getSecretKey()));
    }
}
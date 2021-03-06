package org.pcloud.gateway.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.pcloud.gateway.api.SpyJwtTokenProvider;
import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.AuthInformation;
import org.pcloud.gateway.network.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class AuthServiceImplTest {
    @Mock
    RedisTemplate<String, Object> mockRedisTemplate;
    AuthServiceImpl authService;
    @Spy
    SpyJwtTokenProvider spyJwtTokenProvider;
    StubUuidProvider stubUuidProvider;
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
        String givenRole = "ROLE_role";
        Map<String, Object> givenData = new HashMap<>();
        long givenValidity = 10000;
        long givenRefreshValidity = 100000;
        return new TokenIssueRequest(givenIssueRequestDomain, givenRole, givenData, givenValidity, givenRefreshValidity);
    }

    @Test
    void generateToken_returnValue() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();
        doReturn(new JwtToken("token2", "refresh2")).when(spyJwtTokenProvider).generate(any());

        JwtToken jwtToken = authService.generateToken(givenRequest);

        verify(spyJwtTokenProvider).generate(any());
        assertThat(jwtToken.getToken()).isEqualTo("token2");
        assertThat(jwtToken.getRefresh()).isEqualTo("refresh2");
    }

    @Test
    void generateToken_passesRequestToGenerateOfJwtTokenProvider() {
        TokenIssueRequest givenRequest = getOkTokenIssueRequest();

        authService.generateToken(givenRequest);

        assertThat(spyJwtTokenProvider.generate_argumentRequest.getValidityMS()).isEqualTo(givenRequest.getValidity());
        assertThat(spyJwtTokenProvider.generate_argumentRequest.getRefreshValidityMS()).isEqualTo(givenRequest.getRefreshValidity());
    }

    @Test
    void generateToken_setTokenInRedisTemplate() {
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
        doReturn(givenData).when(mockHashOperations).entries(_givenAuthInformation.getDataSignKey());

        AuthDataInformation authDataInformation = authService.getAuthDataInformation(_givenToken).orElseThrow();

        assertThat(authDataInformation.getRole()).isEqualTo(_givenAuthInformation.getRole());
        assertThat(authDataInformation.getTokenProviderDomain()).isEqualTo(_givenAuthInformation.getTokenProviderDomain());
        assertThat(authDataInformation.getValidity()).isEqualTo(_givenAuthInformation.getValidity());
        assertThat(authDataInformation.getRefreshValidity()).isEqualTo(_givenAuthInformation.getRefreshValidity());
        assertThat(authDataInformation.getAccessToken()).isEqualTo(_givenAuthInformation.getAccessToken());
        assertThat(authDataInformation.getRefreshToken()).isEqualTo(_givenAuthInformation.getRefreshToken());
        assertThat(authDataInformation.getDataSignKey()).isEqualTo(_givenSecretKey);
        assertThat(authDataInformation.getData()).isEqualTo(givenData);
    }

    @Test
    void getAuthDataInformation_passesTokenToIsExpirationOfJwtTokenProvider() {
        authService.getAuthDataInformation(givenToken);

        verify(spyJwtTokenProvider).isExpiration(eq(givenToken));
    }

    @Test
    void getAuthDataInformation_throwRuntimeExceptionToExpiredJwt() {
        doReturn(true).when(spyJwtTokenProvider).isExpiration(any());

        Optional<AuthDataInformation> authDataInformation = authService.getAuthDataInformation(givenToken);

        assertThat(authDataInformation.isEmpty()).isTrue();
    }

    @Test
    void getAuthDataInformation_passesTokenToGetOfOpsForValue() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForValue()).get(givenToken);
    }

    @Test
    void getAuthDataInformation_RunTimeExceptionToGiveTokenAndGetAnNull() {
        Optional<AuthDataInformation> authDataInformation = authService.getAuthDataInformation(givenTokenNull);

        assertThat(authDataInformation.isEmpty()).isTrue();
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

        Optional<AuthDataInformation> authDataInformation = authService.getAuthDataInformation(givenToken);

        assertThat(authDataInformation.isEmpty()).isTrue();
    }

    @Test
    void getAuthDataInformation_throwRunTimeExceptionToCompareTokenAndGetTokenOfOpsForValue() {
        AuthInformation givenAuthInformation = new AuthInformation(null, null, 0, 0, "failToken", "failRefresh", null);
        doReturn(givenAuthInformation).when(mockValueOperations).get(givenRefresh);

        Optional<AuthDataInformation> authDataInformation = authService.getAuthDataInformation(givenToken);

        assertThat(authDataInformation.isEmpty()).isTrue();
    }

    @Test
    void getAuthDataInformation_passesSecretKeyToEntriesOfOpsForHash() {
        authService.getAuthDataInformation(givenToken);

        verify(mockRedisTemplate.opsForHash()).entries(eq(givenAuthInformation.getDataSignKey()));
    }

    @Test
    void reIssueToken_returnValue_tokenNotExpiration() {
        Mockito.lenient().doReturn(false).when(spyJwtTokenProvider).isExpiration(givenToken);

        JwtToken jwtToken = authService.reIssueToken(givenToken, givenRefresh).orElseThrow();

        assertThat(jwtToken.getToken()).isEqualTo(givenToken);
        assertThat(jwtToken.getRefresh()).isEqualTo(givenRefresh);
    }

    @Test
    void reIssueToken_returnValue_tokenExpiration() {
        String _givenToken = "newToken";
        String _givenRefresh = "newRefresh";
        Mockito.lenient().doReturn(true).when(spyJwtTokenProvider).isExpiration(givenToken);
        Mockito.lenient().doReturn(new JwtToken(_givenToken, _givenRefresh)).when(spyJwtTokenProvider).generate(any(JwtTokenGenerateRequest.class));

        JwtToken jwtToken = authService.reIssueToken(givenToken, givenRefresh).orElseThrow();

        assertThat(jwtToken.getToken()).isEqualTo(_givenToken);
        assertThat(jwtToken.getRefresh()).isEqualTo(_givenRefresh);
    }

    @Test
    void reIssueToken_passesRefreshToIsExpirationOfJwtTokenProvider() {
        authService.reIssueToken(givenToken, givenRefresh);

        verify(spyJwtTokenProvider).isExpiration(givenRefresh);
    }

    @Test
    void reIssueToken_throwRuntimeExceptionToExpiredJwt() {
        doReturn(true).when(spyJwtTokenProvider).isExpiration(any());

        boolean isEmpty = authService.reIssueToken(givenToken, givenRefresh).isEmpty();

        assertThat(isEmpty).isTrue();
    }

    @Test
    void reIssueToken_passesRefreshToGetOfOpsForValue() {
        authService.reIssueToken(givenToken, givenRefresh);

        verify(mockRedisTemplate.opsForValue()).get(eq(givenRefresh));
    }

    @Test
    void reIssueToken_throwRunTimeExceptionAndPassesRefreshToGetOfOpsForValue() {
        boolean isEmpty = authService.reIssueToken(givenToken, givenRefreshNull).isEmpty();

        assertThat(isEmpty).isTrue();
        verify(mockRedisTemplate.opsForValue()).get(eq(givenRefreshNull));
    }

    @Test
    void reIssueToken_throwRunTimeExceptionAndCompareTokenAndRefresh() {
        boolean isEmpty = authService.reIssueToken("testToken", givenRefresh).isEmpty();

        assertThat(isEmpty).isTrue();
    }

    @Test
    void reIssueToken_passesNotExpirationTokenToIsExpirationOfJwtTokenProvider() {
        String _givenToken = "tokenNotExpiration";
        String _givenRefresh = "refreshNotExpiration";
        Mockito.lenient().doReturn(false).when(spyJwtTokenProvider).isExpiration(_givenToken);
        AuthInformation _givenAuthInformation = new AuthInformation(null, null, 0, 0, _givenToken, _givenRefresh, null);
        doReturn(_givenAuthInformation).when(mockValueOperations).get(_givenRefresh);

        JwtToken jwtToken = authService.reIssueToken(_givenToken, _givenRefresh).orElseThrow();

        verify(spyJwtTokenProvider).isExpiration(eq(_givenToken));
        assertThat(jwtToken.getToken()).isEqualTo(_givenToken);
        assertThat(jwtToken.getRefresh()).isEqualTo(_givenRefresh);
    }

    @Test
    void reIssueToken_passesValidityToGenerateOfJwtTokenProvider() {
        String _givenToken = "_token";
        String _givenRefresh = "_refresh";
        long _givenValidity = 12345;
        long _givenRefreshValidity = 54321;
        AuthInformation _givenAuthInformation = new AuthInformation(null, null, _givenValidity, _givenRefreshValidity, _givenToken, _givenRefresh, null);
        Mockito.lenient().doReturn(false).when(spyJwtTokenProvider).isExpiration(_givenRefresh);
        Mockito.lenient().doReturn(true).when(spyJwtTokenProvider).isExpiration(_givenToken);
        Mockito.lenient().doReturn(_givenAuthInformation).when(mockValueOperations).get(_givenRefresh);

        authService.reIssueToken(_givenToken, _givenRefresh);

        verify(spyJwtTokenProvider).generate(any(JwtTokenGenerateRequest.class));
    }

    @Test
    void reIssue_tokenExpirationToSetOfOpsForValue() {
        String _givenToken = "newToken";
        String _givenRefresh = "newRefresh";
        Mockito.lenient().doReturn(true).when(spyJwtTokenProvider).isExpiration(givenToken);
        Mockito.lenient().doReturn(new JwtToken(_givenToken, _givenRefresh)).when(spyJwtTokenProvider).generate(any(JwtTokenGenerateRequest.class));

        authService.reIssueToken(givenToken, givenRefresh);

        verify(mockRedisTemplate.opsForValue()).set(eq(_givenToken), eq(_givenRefresh));
        verify(mockRedisTemplate.opsForValue()).set(eq(_givenRefresh), any(AuthInformation.class));
    }

    @Test
    void reIssue_tokenExpirationToDeleteOfRedisTemplate() {
        String _givenToken = "newToken";
        String _givenRefresh = "newRefresh";
        Mockito.lenient().doReturn(true).when(spyJwtTokenProvider).isExpiration(givenToken);
        Mockito.lenient().doReturn(new JwtToken(_givenToken, _givenRefresh)).when(spyJwtTokenProvider).generate(any(JwtTokenGenerateRequest.class));

        authService.reIssueToken(givenToken, givenRefresh);

        verify(mockRedisTemplate).delete(eq(givenToken));
        verify(mockRedisTemplate).delete(eq(givenRefresh));
    }

    @Test
    void reIssue_tokenExpirationToExpireOfRedisTemplate() {
        String _givenToken = "newToken";
        String _givenRefresh = "newRefresh";
        Mockito.lenient().doReturn(true).when(spyJwtTokenProvider).isExpiration(givenToken);
        Mockito.lenient().doReturn(new JwtToken(_givenToken, _givenRefresh)).when(spyJwtTokenProvider).generate(any(JwtTokenGenerateRequest.class));

        authService.reIssueToken(givenToken, givenRefresh);

        verify(mockRedisTemplate).expire(eq(_givenToken), eq(givenAuthInformation.getValidity()), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(_givenRefresh), eq(givenAuthInformation.getRefreshValidity()), eq(TimeUnit.MILLISECONDS));
        verify(mockRedisTemplate).expire(eq(givenAuthInformation.getDataSignKey()), eq(givenAuthInformation.getRefreshValidity()), eq(TimeUnit.MILLISECONDS));
    }
}
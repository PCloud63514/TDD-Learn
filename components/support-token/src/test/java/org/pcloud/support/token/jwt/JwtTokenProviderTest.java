package org.pcloud.support.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.support.token.core.StubDateProvider;
import org.pcloud.support.token.core.StubUuidProvider;
import org.pcloud.support.token.core.Token;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

class JwtTokenProviderTest {
    JwtTokenProvider jwtTokenProvider;
    StubDateProvider stubDateProvider = new StubDateProvider();
    StubUuidProvider stubUuidProvider = new StubUuidProvider();
    String secretKey = "secret";

    @BeforeEach
    void setUp() {
        jwtTokenProvider = new JwtTokenProvider(stubDateProvider, stubUuidProvider, secretKey);
    }

    @Test
    void generate_returnValue() {
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenValidityMS, givenRefreshValidityMS);
        stubDateProvider.now_returnValue = new Date();
        stubUuidProvider.randomUUID_returnValue = UUID.randomUUID();
        Date date = stubDateProvider.now();
        String requestId = stubUuidProvider.randomUUID().toString();

        Claims tokenClaims = Jwts.claims().setSubject(requestId);
        Claims refreshClaims = Jwts.claims().setSubject(requestId);

        String givenToken = Jwts.builder()
                .setClaims(tokenClaims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + givenValidityMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        String givenRefresh = Jwts.builder()
                .setClaims(refreshClaims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + givenRefreshValidityMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


        JwtToken token = jwtTokenProvider.generate(givenRequest);

        assertThat(token.getToken()).isEqualTo(givenToken);
        assertThat(token.getRefresh()).isEqualTo(givenRefresh);
    }

    @Test
    void getInformation_returnValue() {
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenValidityMS, givenRefreshValidityMS);
        stubDateProvider.now_returnValue = new Date();
        stubUuidProvider.randomUUID_returnValue = UUID.randomUUID();

        JwtToken jwtToken = jwtTokenProvider.generate(givenRequest);
        JwtTokenInformation<Token> tokenInformation = jwtTokenProvider.getInformation(jwtToken.getToken());
        JwtTokenInformation<Token> refreshInformation = jwtTokenProvider.getInformation(jwtToken.getRefresh());

        assertThat(tokenInformation.token()).isEqualTo(jwtToken.getToken());
        assertThat(tokenInformation.getIssuedAt().toInstant().getEpochSecond()).isEqualTo(stubDateProvider.now().toInstant().getEpochSecond());

        assertThat(refreshInformation.token()).isEqualTo(jwtToken.getRefresh());
        assertThat(refreshInformation.getIssuedAt().toInstant().getEpochSecond()).isEqualTo(stubDateProvider.now().toInstant().getEpochSecond());
    }

    @Test
    void isExpiration_returnValue() {
        Date givenDate = new Date();
        Claims tokenClaims = Jwts.claims().setSubject("expirationSubject");
        String givenToken = Jwts.builder()
                .setClaims(tokenClaims)
                .setIssuedAt(givenDate)
                .setExpiration(new Date(givenDate.getTime() + 100000))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        boolean isExpiration = jwtTokenProvider.isExpiration(givenToken);

        assertThat(isExpiration).isFalse();
    }

    @Test
    void isExpiration_notExpiredJwtExceptionAndReturnValueTrue() {
        Date givenDate = new Date();
        Claims tokenClaims = Jwts.claims().setSubject("expirationSubject");
        String givenExpirationToken = Jwts.builder()
                .setClaims(tokenClaims)
                .setIssuedAt(givenDate)
                .setExpiration(givenDate)
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();

        boolean isExpiration = jwtTokenProvider.isExpiration(givenExpirationToken);

        assertThat(isExpiration).isTrue();
    }

    @Test
    void isExpiration_throwRuntimeException() {
        assertThrows(RuntimeException.class, ()-> jwtTokenProvider.isExpiration("errorToken"));
    }
}
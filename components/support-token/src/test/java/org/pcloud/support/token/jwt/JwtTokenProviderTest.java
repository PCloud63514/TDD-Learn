package org.pcloud.support.token.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.support.token.core.StubDateProvider;
import org.pcloud.support.token.core.StubUuidProvider;

import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

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
        String givenRole = "role";
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenRole, givenValidityMS, givenRefreshValidityMS);
        stubDateProvider.now_returnValue = new Date();
        stubUuidProvider.randomUUID_returnValue = UUID.randomUUID();
        Date date = stubDateProvider.now();
        Claims claims = Jwts.claims().setSubject(stubUuidProvider.randomUUID().toString());
        String givenToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + givenValidityMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
        String givenRefresh = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(date)
                .setExpiration(new Date(date.getTime() + givenRefreshValidityMS))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();


        JwtToken token = jwtTokenProvider.generate(givenRequest);

        assertThat(token.getToken()).isEqualTo(givenToken);
        assertThat(token.getRefresh()).isEqualTo(givenRefresh);
    }
}
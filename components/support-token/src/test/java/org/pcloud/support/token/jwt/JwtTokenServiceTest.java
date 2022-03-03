package org.pcloud.support.token.jwt;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.support.token.core.StubDateProvider;
import org.pcloud.support.token.core.StubTokenProvider;
import org.pcloud.support.token.core.StubUuidProvider;
import org.pcloud.support.token.core.Token;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceTest {
    JwtTokenService jwtTokenProvider;
    StubTokenProvider stubTokenProvider;
    StubDateProvider stubDateProvider = new StubDateProvider();
    StubUuidProvider stubUuidProvider = new StubUuidProvider();
    String secretKey = "Stub";
    @BeforeEach
    void setUp() {
        stubTokenProvider = new StubTokenProvider(stubDateProvider, stubUuidProvider, secretKey);
        jwtTokenProvider = new JwtTokenService(stubTokenProvider);
    }

    @Test
    void generateToken_returnValue() throws Exception {
        String givenRole = "role";
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenRole, givenValidityMS, givenRefreshValidityMS);
        stubTokenProvider.generate_returnValue = new JwtToken("token", "refresh");

        JwtToken token = jwtTokenProvider.generateToken(givenRequest);

        assertThat(token.getToken()).isEqualTo(stubTokenProvider.generate(givenRequest).getToken());
        assertThat(token.getRefresh()).isEqualTo(stubTokenProvider.generate(givenRequest).getRefresh());
    }
}
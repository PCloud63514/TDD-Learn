package org.pcloud.support.token.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.pcloud.support.token.core.StubDateProvider;
import org.pcloud.support.token.core.StubTokenProvider;
import org.pcloud.support.token.core.StubUuidProvider;
import org.pcloud.support.token.core.Token;
import org.springframework.mock.web.MockHttpServletResponse;

import javax.servlet.http.HttpServletResponse;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;

class JwtTokenServiceImplTest {
    JwtTokenServiceImpl jwtTokenService;
    StubTokenProvider stubTokenProvider;
    StubDateProvider stubDateProvider = new StubDateProvider();
    StubUuidProvider stubUuidProvider = new StubUuidProvider();
    String secretKey = "Stub";

    @BeforeEach
    void setUp() {
        stubTokenProvider = new StubTokenProvider(stubDateProvider, stubUuidProvider, secretKey);
        jwtTokenService = new JwtTokenServiceImpl(stubTokenProvider);
    }

    @Test
    void generateToken_returnValue() throws Exception {
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenValidityMS, givenRefreshValidityMS);
        stubTokenProvider.generate_returnValue = new JwtToken("token", "refresh");

        JwtToken token = jwtTokenService.generateToken(givenRequest);

        assertThat(token.getToken()).isEqualTo(stubTokenProvider.generate(givenRequest).getToken());
        assertThat(token.getRefresh()).isEqualTo(stubTokenProvider.generate(givenRequest).getRefresh());
    }

    @Test
    void generateTokenHttpServletResponse_returnValue() throws Exception {
        long givenValidityMS = 10000;
        long givenRefreshValidityMS = 100000;
        HttpServletResponse givenHttpServletResponse = new MockHttpServletResponse();
        JwtTokenGenerateRequest givenRequest = new JwtTokenGenerateRequest(givenValidityMS, givenRefreshValidityMS);
        stubTokenProvider.generate_returnValue = new JwtToken("token", "refresh");

        JwtToken token = jwtTokenService.generateToken(givenRequest, givenHttpServletResponse);

        assertThat(token.getToken()).isEqualTo(stubTokenProvider.generate(givenRequest).getToken());
        assertThat(token.getRefresh()).isEqualTo(stubTokenProvider.generate(givenRequest).getRefresh());
        assertThat(givenHttpServletResponse.getHeader("token")).isEqualTo(token.getToken());
        assertThat(givenHttpServletResponse.getHeader("refresh")).isEqualTo(token.getRefresh());
    }

    @Test
    void getTokenInformation_returnValue() {
        String givenToken = "token";
        String givenSubject = "subject";
        Date givenDate = new Date();

        stubTokenProvider.getInformation_returnValue = new JwtTokenInformation<Token>(new Token(givenToken), givenSubject, givenDate);
        JwtTokenInformation<Token> tokenInformation = jwtTokenService.getTokenInformation(givenToken);

        assertThat(tokenInformation.token()).isEqualTo(givenToken);
        assertThat(tokenInformation.getSubject()).isEqualTo(givenSubject);
        assertThat(tokenInformation.getIssuedAt().toInstant().getEpochSecond()).isEqualTo(givenDate.toInstant().getEpochSecond());
    }

    @Test
    void getTokenInformation_passesTokenToJwtTokenProvider() {
        String givenToken = "token";

        jwtTokenService.getTokenInformation(givenToken);

        assertThat(stubTokenProvider.getInformation_argumentToken).isEqualTo(givenToken);
    }
}
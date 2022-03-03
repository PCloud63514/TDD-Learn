package org.pcloud.support.token.jwt;

import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.core.TokenService;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class JwtTokenService implements TokenService<JwtToken, JwtTokenGenerateRequest, JwtTokenInformation<JwtToken>> {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtToken generateToken(JwtTokenGenerateRequest request) throws Exception {
        return jwtTokenProvider.generate(request);
    }

    @Override
    public JwtToken generateToken(JwtTokenGenerateRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    public JwtTokenInformation getTokenInformation(String token) {
        return null;
    }

    @Override
    public void revokeToken(String token) {

    }
}

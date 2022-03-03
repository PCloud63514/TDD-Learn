package org.pcloud.support.token.jwt;

import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.core.Token;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Component
public class JwtTokenServiceImpl implements JwtTokenService {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public JwtToken generateToken(JwtTokenGenerateRequest request) throws Exception {
        return jwtTokenProvider.generate(request);
    }

    @Override
    public JwtToken generateToken(JwtTokenGenerateRequest request, HttpServletResponse response) throws Exception {
        JwtToken jwtToken = this.generateToken(request);
        response.setHeader("token", jwtToken.getToken());
        response.setHeader("refresh", jwtToken.getRefresh());
        return jwtToken;
    }

    public JwtTokenInformation<Token> getTokenInformation(String token) {
        return jwtTokenProvider.getInformation(token);
    }
}

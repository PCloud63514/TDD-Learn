package org.pcloud.support.token.jwt;

import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenProvider;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletResponse;

@Component
public class JwtTokenProvider implements TokenProvider<Token, JwtTokenGenerateRequest, JwtTokenInformation> {
    @Override
    public Token generateToken(JwtTokenGenerateRequest request, HttpServletResponse response) throws Exception {
        return null;
    }

    @Override
    public JwtTokenInformation getToken(Token token) {
        return null;
    }

    @Override
    public void revokeToken(Token token) {

    }
}

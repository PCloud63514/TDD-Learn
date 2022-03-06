package org.pcloud.security.api;

import org.pcloud.support.token.core.DateProvider;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.UuidProvider;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenInformation;
import org.pcloud.support.token.jwt.JwtTokenProvider;

public class SpyJwtTokenProvider extends JwtTokenProvider {
    public JwtTokenGenerateRequest generate_argumentRequest;
    public JwtToken generate_returnValue;

    public SpyJwtTokenProvider() {
        super(null, null, null);
    }

    public SpyJwtTokenProvider(DateProvider dateProvider, UuidProvider uuidProvider, String secretKey) {
        super(dateProvider, uuidProvider, secretKey);
    }

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        generate_argumentRequest = request;
        return generate_returnValue;
    }

    @Override
    public JwtTokenInformation<Token> getInformation(String token) {
        return null;
    }
}

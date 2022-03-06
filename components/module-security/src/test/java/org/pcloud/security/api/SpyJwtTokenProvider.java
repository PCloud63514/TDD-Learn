package org.pcloud.security.api;

import org.pcloud.support.token.core.DateProvider;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.UuidProvider;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenInformation;
import org.pcloud.support.token.jwt.JwtTokenProvider;

public class SpyJwtTokenProvider extends JwtTokenProvider {
    public SpyJwtTokenProvider() {
        super(null, null, null);
    }

    public SpyJwtTokenProvider(DateProvider dateProvider, UuidProvider uuidProvider, String secretKey) {
        super(dateProvider, uuidProvider, secretKey);
    }

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        return super.generate(request);
    }

    @Override
    public JwtTokenInformation<Token> getInformation(String token) {
        return super.getInformation(token);
    }
}

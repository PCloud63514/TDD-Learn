package org.pcloud.support.token.core;

import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.data.auditing.DateTimeProvider;

public class StubTokenProvider extends JwtTokenProvider {
    public JwtToken generate_returnValue;

    public StubTokenProvider(DateProvider dateProvider, UuidProvider uuidProvider, String secretKey) {
        super(dateProvider, uuidProvider, secretKey);
    }

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        return generate_returnValue;
    }
}

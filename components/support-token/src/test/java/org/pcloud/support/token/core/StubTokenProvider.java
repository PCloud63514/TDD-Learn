package org.pcloud.support.token.core;

import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenInformation;
import org.pcloud.support.token.jwt.JwtTokenProvider;

public class StubTokenProvider extends JwtTokenProvider {
    public JwtToken generate_returnValue;
    public String getInformation_argumentToken;
    public JwtTokenInformation<Token> getInformation_returnValue;

    public StubTokenProvider(DateProvider dateProvider, UuidProvider uuidProvider, String secretKey) {
        super(dateProvider, uuidProvider, secretKey);
    }

    @Override
    public JwtToken generate(JwtTokenGenerateRequest request) {
        return generate_returnValue;
    }

    @Override
    public JwtTokenInformation<Token> getInformation(String token) {
        getInformation_argumentToken = token;
        return getInformation_returnValue;
    }
}

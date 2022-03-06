package org.pcloud.security.api;

import org.pcloud.security.service.AuthService;
import org.pcloud.support.token.core.Token;

public class SpyAuthService implements AuthService {
    public TokenIssueRequest generateToken_argumentRequest;
    public Token generateToken_returnValue;

    @Override
    public Token generateToken(TokenIssueRequest request) {
        this.generateToken_argumentRequest = request;
        return generateToken_returnValue;
    }
}

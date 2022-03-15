package org.pcloud.gateway.api;

import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.gateway.service.AuthService;
import org.pcloud.support.token.jwt.JwtToken;

public class SpyAuthService implements AuthService {
    public TokenIssueRequest generateToken_argumentRequest;
    public JwtToken generateToken_returnValue;
    public String deleteToken_argumentToken;

    @Override
    public JwtToken generateToken(TokenIssueRequest request) {
        this.generateToken_argumentRequest = request;

        return generateToken_returnValue;
    }

    @Override
    public void deleteToken(String token) {
        this.deleteToken_argumentToken = token;
    }

    @Override
    public AuthDataInformation getAuthDataInformation(String token) {

        return null;
    }

    @Override
    public JwtToken reIssueToken(String token, String refresh) {
        return null;
    }
}

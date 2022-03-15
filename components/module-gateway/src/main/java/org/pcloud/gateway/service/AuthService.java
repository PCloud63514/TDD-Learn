package org.pcloud.gateway.service;

import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;

public interface AuthService {
    JwtToken generateToken(TokenIssueRequest request);

    void deleteToken(String token);

    AuthDataInformation getAuthDataInformation(String token);

    JwtToken reIssueToken(String token, String refresh);
}

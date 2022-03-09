package org.pcloud.security.service;

import org.pcloud.security.data.AuthDataInformation;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;

public interface AuthService {
    JwtToken generateToken(TokenIssueRequest request);

    void deleteToken(String token);

    AuthDataInformation getAuthDataInformation(String token);

    JwtToken reIssueToken(String token, String refresh);
}

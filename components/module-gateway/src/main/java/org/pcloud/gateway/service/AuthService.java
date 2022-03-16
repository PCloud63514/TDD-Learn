package org.pcloud.gateway.service;

import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;

import java.util.Optional;

public interface AuthService {
    JwtToken generateToken(TokenIssueRequest request);

    void deleteToken(String token);

    Optional<AuthDataInformation> getAuthDataInformation(String token);

    Optional<JwtToken> reIssueToken(String token, String refresh);
}

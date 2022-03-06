package org.pcloud.security.service;

import org.pcloud.security.api.TokenIssueRequest;
import org.pcloud.support.token.core.Token;

public interface AuthService {
    Token generateToken(TokenIssueRequest request);
}

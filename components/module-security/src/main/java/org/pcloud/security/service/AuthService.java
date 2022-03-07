package org.pcloud.security.service;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.Token;

public interface AuthService {
    Token generateToken(TokenIssueRequest request);
}

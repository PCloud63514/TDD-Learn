package org.pcloud.security.service;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.Token;

public interface AuthService {
    Token generateToken(TokenIssueRequest request);

    void deleteToken(String token);

    // 토큰 연계 정보 조회

    // 토큰 연장
}

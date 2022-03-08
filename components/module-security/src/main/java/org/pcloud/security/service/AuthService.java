package org.pcloud.security.service;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.jwt.JwtToken;

public interface AuthService {
    JwtToken generateToken(TokenIssueRequest request);

    void deleteToken(String token);

    // 토큰 연계 정보 조회

    // 토큰 연장

    // 토큰 인증
}

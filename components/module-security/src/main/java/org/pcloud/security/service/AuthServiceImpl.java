package org.pcloud.security.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.api.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public Token generateToken(TokenIssueRequest request) {
        // 토큰 생성
        JwtTokenGenerateRequest jwtTokenGenerateRequest = new JwtTokenGenerateRequest(request.getRole(), request.getValidity(), request.getRefreshValidity());
        // redis에 데이터 저장
        // 토큰 반환
        return jwtTokenProvider.generate(jwtTokenGenerateRequest);
    }
}

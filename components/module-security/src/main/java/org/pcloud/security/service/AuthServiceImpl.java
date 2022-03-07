package org.pcloud.security.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;

    @Override
    public Token generateToken(TokenIssueRequest request) {
        JwtTokenGenerateRequest jwtTokenGenerateRequest = new JwtTokenGenerateRequest(request.getIssueRequestDomain(), request.getRole(), request.getValidity(), request.getRefreshValidity());
        JwtToken generateToken = jwtTokenProvider.generate(jwtTokenGenerateRequest);

        HashOperations<String, Object, Object> operations = redisTemplate.opsForHash();
        operations.putAll(generateToken.getToken(), request.getData());
        redisTemplate.expire(generateToken.getToken(), request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return new Token(generateToken.getToken());
    }
}

package org.pcloud.security.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.UuidProvider;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Service
public class AuthServiceImpl implements AuthService {
    private final JwtTokenProvider jwtTokenProvider;
    private final RedisTemplate<String, Object> redisTemplate;
    private final UuidProvider uuidProvider;
    @Override
    public JwtToken generateToken(TokenIssueRequest request) {
        JwtTokenGenerateRequest jwtTokenGenerateRequest = new JwtTokenGenerateRequest(request.getValidity(), request.getRefreshValidity());
        JwtToken generateToken = jwtTokenProvider.generate(jwtTokenGenerateRequest);
        String secretKey = uuidProvider.randomUUID().toString();
        Map<String, Object> tokenInfo = new HashMap<>();
        tokenInfo.put("role", request.getRole());
        tokenInfo.put("tokenProviderDomain", request.getIssueRequestDomain());
        tokenInfo.put("validity", request.getValidity());
        tokenInfo.put("refreshValidity", request.getRefreshValidity());
        tokenInfo.put("token", generateToken.getToken());
        tokenInfo.put("secret-key", secretKey);

        ValueOperations<String, Object> opVal = redisTemplate.opsForValue();
        opVal.set(generateToken.getToken(), generateToken.getRefresh(), request.getValidity(), TimeUnit.MILLISECONDS);

        HashOperations<String, Object, Object> opHash = redisTemplate.opsForHash();
        opHash.putAll(generateToken.getRefresh(), tokenInfo);
        opHash.putAll(secretKey, request.getData());

        redisTemplate.expire(generateToken.getToken(), request.getValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(generateToken.getRefresh(), request.getRefreshValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(secretKey, request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return new JwtToken(generateToken.getToken(), generateToken.getRefresh());
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }
}

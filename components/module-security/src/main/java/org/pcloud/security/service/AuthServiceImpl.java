package org.pcloud.security.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.data.AuthDataInformation;
import org.pcloud.security.data.AuthInformation;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.support.token.core.UuidProvider;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

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

        AuthInformation authInformation = new AuthInformation(request.getRole(), request.getIssueRequestDomain(),
                request.getValidity(), request.getRefreshValidity(),
                generateToken.getToken(), generateToken.getRefresh(), secretKey);

        ValueOperations<String, Object> opVal = redisTemplate.opsForValue();
        opVal.set(generateToken.getToken(), generateToken.getRefresh(), request.getValidity(), TimeUnit.MILLISECONDS);
        opVal.set(generateToken.getRefresh(), authInformation, request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        HashOperations<String, Object, Object> opHash = redisTemplate.opsForHash();
        opHash.putAll(secretKey, request.getData());

        redisTemplate.expire(generateToken.getToken(), request.getValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(generateToken.getRefresh(), request.getRefreshValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(secretKey, request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return generateToken;
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public AuthDataInformation getAuthDataInformation(String token) {
        if(jwtTokenProvider.isExpiration(token)) throw new RuntimeException();
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();

        String refresh = (String) opValue.get(token);
        if (refresh == null || refresh.isBlank()) throw new RuntimeException();

        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) throw new RuntimeException();
        if (!token.equals(authInformation.getToken())) throw new RuntimeException();

        HashOperations<String, String, Object> opHash = redisTemplate.opsForHash();
        Map<String, Object> data = opHash.entries(authInformation.getSecretKey());

        return new AuthDataInformation(authInformation.getRole(), authInformation.getTokenProviderDomain(),
                authInformation.getValidity(), authInformation.getRefreshValidity(),
                authInformation.getToken(), authInformation.getRefresh(),
                authInformation.getSecretKey(), data);
    }

    @Override
    public JwtToken reIssueToken(String token, String refresh) {
        if(jwtTokenProvider.isExpiration(refresh)) throw new RuntimeException();
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();
        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) throw new RuntimeException();
        if (!token.equals(authInformation.getToken())) throw new RuntimeException();

        if(!jwtTokenProvider.isExpiration(token)) {
            return new JwtToken(token, refresh);
        }

        // jwt 새로 발급
        jwtTokenProvider.generate(new JwtTokenGenerateRequest(authInformation.getValidity(), authInformation.getRefreshValidity()));
        // 토큰 -> refresh
        // refresh -> authInformation 구조 다시 만듬 형태로 redis 등록

        // 이전 토큰 & refresh 폐기
        // 새로 넣은 데이터들 및 secretKey에 있던 데이터 시간 갱신
        return new JwtToken(token, refresh);
    }
}

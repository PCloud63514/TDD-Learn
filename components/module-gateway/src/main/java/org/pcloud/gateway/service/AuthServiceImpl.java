package org.pcloud.gateway.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.AuthInformation;
import org.pcloud.gateway.data.request.TokenIssueRequest;
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
        if (jwtTokenProvider.isExpiration(token)) return null;
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();

        String refresh = (String) opValue.get(token);
        if (refresh == null || refresh.isBlank()) return null;

        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) return null;
        if (!token.equals(authInformation.getToken())) return null;

        HashOperations<String, String, Object> opHash = redisTemplate.opsForHash();
        Map<String, Object> data = opHash.entries(authInformation.getSecretKey());

        return new AuthDataInformation(authInformation.getRole(), authInformation.getTokenProviderDomain(),
                authInformation.getValidity(), authInformation.getRefreshValidity(),
                authInformation.getToken(), authInformation.getRefresh(),
                authInformation.getSecretKey(), data);
    }

    @Override
    public JwtToken reIssueToken(String token, String refresh) {
        if (jwtTokenProvider.isExpiration(refresh)) throw new RuntimeException();
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();
        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) throw new RuntimeException();
        if (!token.equals(authInformation.getToken())) throw new RuntimeException();

        if (!jwtTokenProvider.isExpiration(token)) {
            return new JwtToken(token, refresh);
        }

        JwtToken newJwtToken = jwtTokenProvider.generate(new JwtTokenGenerateRequest(authInformation.getValidity(), authInformation.getRefreshValidity()));
        opValue.set(newJwtToken.getToken(), newJwtToken.getRefresh());
        AuthInformation newAuthInformation = new AuthInformation(authInformation.getRole(), authInformation.getTokenProviderDomain(),
                authInformation.getValidity(), authInformation.getRefreshValidity(),
                newJwtToken.getToken(), newJwtToken.getRefresh(), authInformation.getSecretKey());
        opValue.set(newJwtToken.getRefresh(), newAuthInformation);

        redisTemplate.delete(token);
        redisTemplate.delete(refresh);

        redisTemplate.expire(newJwtToken.getToken(), newAuthInformation.getValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(newJwtToken.getRefresh(), newAuthInformation.getRefreshValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(newAuthInformation.getSecretKey(), newAuthInformation.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return newJwtToken;
    }
}

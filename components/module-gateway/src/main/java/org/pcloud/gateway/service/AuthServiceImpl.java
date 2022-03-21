package org.pcloud.gateway.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.AuthInformation;
import org.pcloud.gateway.network.TokenIssueRequest;
import org.pcloud.support.token.core.UuidProvider;
import org.pcloud.support.token.jwt.JwtToken;
import org.pcloud.support.token.jwt.JwtTokenGenerateRequest;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
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
        String dataSignKey = uuidProvider.randomUUID().toString();

        AuthInformation authInformation = new AuthInformation(request.getRole(), request.getIssueRequestDomain(),
                request.getValidity(), request.getRefreshValidity(),
                generateToken.getToken(), generateToken.getRefresh(), dataSignKey);

        ValueOperations<String, Object> opVal = redisTemplate.opsForValue();
        opVal.set(generateToken.getToken(), generateToken.getRefresh(), request.getValidity(), TimeUnit.MILLISECONDS);
        opVal.set(generateToken.getRefresh(), authInformation, request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        HashOperations<String, Object, Object> opHash = redisTemplate.opsForHash();
        opHash.putAll(dataSignKey, request.getData());

        redisTemplate.expire(generateToken.getToken(), request.getValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(generateToken.getRefresh(), request.getRefreshValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(dataSignKey, request.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return generateToken;
    }

    @Override
    public void deleteToken(String token) {
        redisTemplate.delete(token);
    }

    @Override
    public Optional<AuthDataInformation> getAuthDataInformation(String token) {
        if (jwtTokenProvider.isExpiration(token)) return Optional.empty();
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();

        String refresh = (String) opValue.get(token);
        if (refresh == null || refresh.isBlank()) return Optional.empty();

        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) return Optional.empty();
        if (!token.equals(authInformation.getAccessToken())) return Optional.empty();

        HashOperations<String, String, Object> opHash = redisTemplate.opsForHash();
        Map<String, Object> data = opHash.entries(authInformation.getDataSignKey());

        return Optional.of(new AuthDataInformation(authInformation.getRole(), authInformation.getTokenProviderDomain(),
                authInformation.getValidity(), authInformation.getRefreshValidity(),
                authInformation.getAccessToken(), authInformation.getRefreshToken(),
                authInformation.getDataSignKey(), data));
    }

    @Override
    public Optional<JwtToken> reIssueToken(String token, String refresh) {
        if (jwtTokenProvider.isExpiration(refresh)) return Optional.empty();
        ValueOperations<String, Object> opValue = redisTemplate.opsForValue();
        AuthInformation authInformation = (AuthInformation) opValue.get(refresh);
        if (authInformation == null) return Optional.empty();
        if (!token.equals(authInformation.getAccessToken())) return Optional.empty();

        if (!jwtTokenProvider.isExpiration(token)) {
            return Optional.of(new JwtToken(token, refresh));
        }

        JwtToken newJwtToken = jwtTokenProvider.generate(new JwtTokenGenerateRequest(authInformation.getValidity(), authInformation.getRefreshValidity()));
        opValue.set(newJwtToken.getToken(), newJwtToken.getRefresh());
        AuthInformation newAuthInformation = new AuthInformation(authInformation.getRole(), authInformation.getTokenProviderDomain(),
                authInformation.getValidity(), authInformation.getRefreshValidity(),
                newJwtToken.getToken(), newJwtToken.getRefresh(), authInformation.getDataSignKey());
        opValue.set(newJwtToken.getRefresh(), newAuthInformation);

        redisTemplate.delete(token);
        redisTemplate.delete(refresh);

        redisTemplate.expire(newJwtToken.getToken(), newAuthInformation.getValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(newJwtToken.getRefresh(), newAuthInformation.getRefreshValidity(), TimeUnit.MILLISECONDS);
        redisTemplate.expire(newAuthInformation.getDataSignKey(), newAuthInformation.getRefreshValidity(), TimeUnit.MILLISECONDS);

        return Optional.of(newJwtToken);
    }
}

package org.pcloud.security.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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
                request.getValidity(),
                request.getRefreshValidity(),
                generateToken.getToken(),
                generateToken.getRefresh(),
                "secretKey");

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
        jwtTokenProvider.getInformation(token);
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
        return null;
    }

    // 토큰 정보 가져오는 것
    // authentication 객체 생성해주는 것
    // 토큰을 재발급 해주는 것 (정보까지 가져와야할지 고민 중.) 흠.. 어차피 정보 가져오는거 있고 따로 만들자
    //
    // 토큰 하나 주고 실패했을 때? -> 토큰이 만료되었을 때임.
    // 위에서 하는 일은 뭘까? Auth객체 반환? 토큰에 대한 정보? 일단 뭐가 되었은 정보 조회가 기본되어야할듯
    // 재발급
    // 정보 조회
    //
    /**
     * token을 전달했을 때
     * token 위변조 및 만료 검사
     * 정상
     *  token값을 key로 redis에 조회 -> refresh 조회 (조회 실패시 만료임)
     *  refresh 조회 성공 시 refresh를 key로 hash값 가져옴
     *  refresh hash에 저장된 token이랑 전달받은 값이 같은지 비교
     *  secret-key를 키 값으로 데이터 조회함
     *
     * 만료
     * token이랑 refresh를 전달 받음
     * refresh를 키로 조회
     * refresh 조회 성공 시 refresh를 key로 hash값 가져옴
     * refresh hash에 저장된 token이랑 전달받은 값이 같은지 비교
     * secret-key를 키 값으로 데이터 조회함
     * refresh hash에 저장된 토큰관련 정보를 이용해서 토큰을 재발급
     * token, refresh 둘다 재갱신(redis에 적용하고 기존 key는 폐기처리)
     * 반환
     */
}

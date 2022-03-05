package org.pcloud.security.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthApi {
    private final JwtTokenProvider provider;
    // 토큰 발급
    // 토큰 연장
    // 토큰 폐기
    // 토큰 관련 정보 조회?
}

package org.pcloud.security.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.support.token.jwt.JwtTokenProvider;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthApi {
    private final JwtTokenProvider jwtTokenProvider;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public void issueToken(@RequestBody TokenIssueRequest request) {

    }
    // 토큰 발급
    // 토큰 연장
    // 토큰 폐기
    // 토큰 관련 정보 조회?
}

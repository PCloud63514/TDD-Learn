package org.pcloud.security.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.service.AuthService;
import org.pcloud.support.token.core.Token;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthApi {
    private final AuthService authService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public Token issueToken(@RequestBody TokenIssueRequest request) {
        return this.authService.generateToken(request);
    }
    // 토큰 발급
    // 토큰 연장
    // 토큰 폐기
    // 토큰 관련 정보 조회?
}

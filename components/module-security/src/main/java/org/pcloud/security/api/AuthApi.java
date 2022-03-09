package org.pcloud.security.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.security.data.response.JwtTokenResponse;
import org.pcloud.security.service.AuthService;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthApi {
    private final AuthService authService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public JwtTokenResponse issueToken(@RequestBody TokenIssueRequest request) {
        JwtToken jwtToken = this.authService.generateToken(request);
        return new JwtTokenResponse(jwtToken.getToken(), jwtToken.getRefresh());
    }

    @DeleteMapping("{token}")
    public void breakToken(@PathVariable(name = "token") String token) {
        authService.deleteToken(token);
    }
}

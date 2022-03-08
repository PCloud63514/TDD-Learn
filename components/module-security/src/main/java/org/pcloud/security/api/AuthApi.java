package org.pcloud.security.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.security.data.request.TokenIssueRequest;
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
    public JwtToken issueToken(@RequestBody TokenIssueRequest request) {
        return this.authService.generateToken(request);
    }

    @DeleteMapping("{token}")
    public void breakToken(@PathVariable(name = "token") String token) {
        authService.deleteToken(token);
    }
}

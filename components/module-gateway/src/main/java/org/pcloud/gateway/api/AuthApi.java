package org.pcloud.gateway.api;

import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.network.TokenIssueRequest;
import org.pcloud.gateway.network.JwtTokenResponse;
import org.pcloud.gateway.service.AuthService;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
@RequestMapping("auth")
@RestController
public class AuthApi {
    private final AuthService authService;

    @ResponseStatus(code = HttpStatus.CREATED)
    @PostMapping
    public Mono<JwtTokenResponse> issueToken(@RequestBody TokenIssueRequest request) {
        JwtToken jwtToken = this.authService.generateToken(request);
        JwtTokenResponse jwtTokenResponse = new JwtTokenResponse(jwtToken.getToken(), jwtToken.getRefresh());
        return Mono.just(jwtTokenResponse);
    }

    @DeleteMapping("{token}")
    public void breakToken(@PathVariable(name = "token") String token) {
        authService.deleteToken(token);
    }
}

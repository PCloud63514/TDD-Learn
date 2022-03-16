package org.pcloud.gateway.config.filter;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.data.AuthDataInformation;
import org.pcloud.gateway.data.User;
import org.pcloud.gateway.service.AuthService;
import org.pcloud.gateway.utils.JwtAuthUtil;
import org.pcloud.support.token.jwt.JwtToken;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    private final AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        String accessToken = getAccessToken(request);

        List<HttpCookie> httpCookies = request.getCookies().get(JwtAuthUtil.REFRESH_TOKEN_SYNTAX);
        if (httpCookies != null && httpCookies.stream().findFirst().isPresent()) {
            String refreshToken = httpCookies.stream().findFirst().get().getValue();
            Optional<JwtToken> jwtTokenOptional = authService.reIssueToken(accessToken, refreshToken);

            if (jwtTokenOptional.isPresent()) {
                JwtToken jwtToken = jwtTokenOptional.get();
                if (!jwtToken.getToken().equals(accessToken) && !jwtToken.getRefresh().equals(refreshToken)) {
                    JwtAuthUtil.reactiveInjectAuthorization(jwtToken.getToken(), jwtToken.getRefresh(), exchange.getResponse());
                }

                Optional<AuthDataInformation> authDataInformationOptional = authService.getAuthDataInformation(jwtToken.getToken());
                if (authDataInformationOptional.isPresent()) {
                    AbstractAuthenticationToken authenticationToken = createAuthenticationToken(authDataInformationOptional.get());
                    return chain.filter(exchange)
                            .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authenticationToken));
                }
            }
        }

        return chain.filter(exchange);
    }

    private AbstractAuthenticationToken createAuthenticationToken(AuthDataInformation authDataInformation) {
        User user = new User(authDataInformation.getDataSignKey(), authDataInformation.getRole());
        return new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
    }

    private String getAccessToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && !bearerToken.isBlank() && bearerToken.startsWith(JwtAuthUtil.HEADER_TOKEN_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

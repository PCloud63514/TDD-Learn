package org.pcloud.gateway.config.filter;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.service.AuthService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {
    public static final String HEADER_PREFIX = "Bearer ";
    private final AuthService authService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        // authorization 에 있는 토큰 읽기
        ServerHttpRequest request = exchange.getRequest();
        String refreshToken = getRefreshToken(request);

        // 기간만료 등이 나오면?
        return chain.filter(exchange);
    }

    private String getRefreshToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && !bearerToken.isBlank() && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

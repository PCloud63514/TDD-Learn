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

        // 흠 권한 필요 없는 애들은 통과가 되긴 해야함
        // 이를 판단했을 때 내부적으로 뭐가 안되었다 싶으면 예외처리해버리면 힘들긴함
        // 물론 클라이언트가 모든 요청에 토큰을 담을 것이 아니라 특정한 요청만 담으면 되긴 함

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

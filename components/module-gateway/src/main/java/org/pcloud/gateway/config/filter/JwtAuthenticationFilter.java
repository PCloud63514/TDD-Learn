package org.pcloud.gateway.config.filter;

import com.google.common.net.HttpHeaders;
import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.service.AuthService;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.AbstractAuthenticationToken;
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
        String accessToken = getAccessToken(request);

        // 쿠키(refresh)랑 access 가져옴
        // 재발급 요청 -> 아예없음, 만료상태, 정상적인 상태
        // 없으면 null, 만료상태나 정상 상태는 토큰이 반환
        // 받은 토큰으로 auth정보 가져옴
        // auth정보가 null 이 아니면 authorization 값 만듬.
        // 결국 상기 로직을 통과만 하면 별도로 로직이 돌아가면 됨

        request.getCookies().get("refresh_token").stream().findFirst().ifPresent(httpCookie -> {
            String refreshToken = httpCookie.getValue();
            authService.reIssueToken(accessToken, refreshToken);
        });

        // 흠 권한 필요 없는 애들은 통과가 되긴 해야함
        // 이를 판단했을 때 내부적으로 뭐가 안되었다 싶으면 예외처리해버리면 힘들긴함
        // 물론 클라이언트가 모든 요청에 토큰을 담을 것이 아니라 특정한 요청만 담으면 되긴 함

        // 기간만료 등이 나오면?
        return chain.filter(exchange);
    }

    private AbstractAuthenticationToken createAuthenticationToken() {
        return null;
    }

    private String getAccessToken(ServerHttpRequest request) {
        String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);

        if (bearerToken != null && !bearerToken.isBlank() && bearerToken.startsWith(HEADER_PREFIX)) {
            return bearerToken.substring(7);
        }
        return null;
    }
}

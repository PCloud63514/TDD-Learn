package org.pcloud.gateway.utils;

import org.springframework.http.ResponseCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

public class JwtAuthUtil {
    public static final String HEADER_TOKEN_PREFIX = "Bearer ";
    public static final String ACCESS_TOKEN_SYNTAX = "access_token";
    public static final String REFRESH_TOKEN_SYNTAX = "refresh_token";

    public static void injectAuthorization(String accessToken, String refreshToken, HttpServletResponse response) {
        if (refreshToken != null) {
            Cookie cookie = new Cookie(JwtAuthUtil.REFRESH_TOKEN_SYNTAX, refreshToken);
            cookie.setHttpOnly(true);
            cookie.setPath("/");
            cookie.setSecure(true);
            response.addCookie(cookie);
        }
        if (accessToken != null) {
            response.addHeader(JwtAuthUtil.ACCESS_TOKEN_SYNTAX, accessToken);
        }
    }

    public static void reactiveInjectAuthorization(String accessToken, String refreshToken, ServerHttpResponse response) {
        if (refreshToken != null) {
            ResponseCookie responseCookie = ResponseCookie.from(JwtAuthUtil.REFRESH_TOKEN_SYNTAX, refreshToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
//                .domain("domain.net")
                    .build();
            response.getCookies().clear();
            response.addCookie(responseCookie);
        }
        if (accessToken != null) {
            response.getHeaders().add(JwtAuthUtil.ACCESS_TOKEN_SYNTAX, accessToken);
        }
    }
}

package org.pcloud.gateway.network;

import lombok.Getter;

import javax.servlet.http.HttpServletResponse;

@Getter
public class JwtTokenResponse extends TokenResponse {
    private String refreshToken;

    public JwtTokenResponse(String accessToken, String refreshToken) {
        super(accessToken);
        this.refreshToken = refreshToken;
    }

    public JwtTokenResponse() {
    }

    public void append(HttpServletResponse response) {

    }
}

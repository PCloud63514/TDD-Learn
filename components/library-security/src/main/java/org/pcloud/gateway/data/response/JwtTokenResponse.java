package org.pcloud.gateway.data.response;

import lombok.Getter;

@Getter
public class JwtTokenResponse extends TokenResponse {
    private String refresh;

    public JwtTokenResponse(String token, String refresh) {
        super(token);
        this.refresh = refresh;
    }

    public JwtTokenResponse() {
    }
}

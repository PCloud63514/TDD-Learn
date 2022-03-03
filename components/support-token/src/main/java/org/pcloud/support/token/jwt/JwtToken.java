package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.Token;

@Getter
public class JwtToken extends Token {
    private final String refresh;

    public JwtToken(String token, String refresh) {
        super(token);
        this.refresh = refresh;
    }
}

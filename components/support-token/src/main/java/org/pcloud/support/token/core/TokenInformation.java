package org.pcloud.support.token.core;

import lombok.Getter;

import java.util.Date;

@Getter
public class TokenInformation<T extends Token> {
    private final T token;
    private final Date issuedAt;

    public TokenInformation(T token, Date issuedAt) {
        this.token = token;
        this.issuedAt = issuedAt;
    }

    public String token() {
        return token.getToken();
    }
}

package org.pcloud.support.token.core;

import lombok.Getter;

import java.util.Date;

@Getter
public class TokenInformation<T extends Token> {
    private final T token;
    private final long validityMS;
    private final Date issuedAt;

    public TokenInformation(T token, long validityMS, Date issuedAt) {
        this.token = token;
        this.validityMS = validityMS;
        this.issuedAt = issuedAt;
    }

    public String token() {
        return token.getToken();
    }
}

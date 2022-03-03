package org.pcloud.support.token.core;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenInformation<T extends Token> {
    private final T token;
    private final long validityMS;
    private final LocalDateTime createAt;

    public TokenInformation(T token, long validityMS, LocalDateTime createAt) {
        this.token = token;
        this.validityMS = validityMS;
        this.createAt = createAt;
    }
}

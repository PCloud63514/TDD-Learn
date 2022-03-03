package org.pcloud.support.token.core;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenInformation<T extends Token> {
    private final T token;
    private final long validity;
    private final LocalDateTime createAt;

    public TokenInformation(T token, long validity, LocalDateTime createAt) {
        this.token = token;
        this.validity = validity;
        this.createAt = createAt;
    }
}

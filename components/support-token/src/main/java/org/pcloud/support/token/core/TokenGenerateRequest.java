package org.pcloud.support.token.core;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class TokenGenerateRequest {
    private final long validity;
    private final LocalDateTime createAt;

    public TokenGenerateRequest(long validity, LocalDateTime createAt) {
        this.validity = validity;
        this.createAt = createAt;
    }
}

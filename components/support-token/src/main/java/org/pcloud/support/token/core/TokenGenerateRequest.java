package org.pcloud.support.token.core;

import lombok.Getter;

@Getter
public class TokenGenerateRequest {
    private final long validityMS;

    public TokenGenerateRequest(long validityMS) {
        this.validityMS = validityMS;
    }
}

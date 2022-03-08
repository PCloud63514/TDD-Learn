package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.TokenGenerateRequest;

@Getter
public class JwtTokenGenerateRequest extends TokenGenerateRequest {
    private final long refreshValidityMS;

    public JwtTokenGenerateRequest(long validityMS, long refreshValidityMS) {
        super(validityMS);
        this.refreshValidityMS = refreshValidityMS;
    }
}

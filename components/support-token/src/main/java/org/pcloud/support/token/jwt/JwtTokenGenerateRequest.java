package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.TokenGenerateRequest;

import java.time.LocalDateTime;

@Getter
public class JwtTokenGenerateRequest extends TokenGenerateRequest {
    private final String role;
    private final long refreshValidityMS;

    public JwtTokenGenerateRequest(String role, long validityMS, long refreshValidityMS) {
        super(validityMS);
        this.role = role;
        this.refreshValidityMS = refreshValidityMS;
    }
}

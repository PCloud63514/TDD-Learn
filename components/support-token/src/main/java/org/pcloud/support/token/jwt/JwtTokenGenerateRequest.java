package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.TokenGenerateRequest;

import java.time.LocalDateTime;

@Getter
public class JwtTokenGenerateRequest extends TokenGenerateRequest {
    private final String role;
    private final long refreshValidity;

    public JwtTokenGenerateRequest(String role, long validity, long refreshValidity, LocalDateTime createAt) {
        super(validity, createAt);
        this.role = role;
        this.refreshValidity = refreshValidity;
    }
}

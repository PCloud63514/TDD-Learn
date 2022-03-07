package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.TokenGenerateRequest;

@Getter
public class JwtTokenGenerateRequest extends TokenGenerateRequest {
    private final String tokenProviderDomain;
    private final String role;
    private final long refreshValidityMS;

    public JwtTokenGenerateRequest(String tokenProviderDomain, String role, long validityMS, long refreshValidityMS) {
        super(validityMS);
        this.tokenProviderDomain = tokenProviderDomain;
        this.role = role;
        this.refreshValidityMS = refreshValidityMS;
    }
}

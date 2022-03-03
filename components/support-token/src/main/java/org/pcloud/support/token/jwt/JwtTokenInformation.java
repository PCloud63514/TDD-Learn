package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.TokenInformation;

import java.time.LocalDateTime;

@Getter
public class JwtTokenInformation<T extends JwtToken> extends TokenInformation<T> {
    private final String role;
    private final long refreshValidityMS;

    public JwtTokenInformation(T token, String role, long validityMS, long refreshValidityMS, LocalDateTime createAt) {
        super(token, validityMS, createAt);
        this.role = role;
        this.refreshValidityMS = refreshValidityMS;
    }
}

package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenInformation;

import java.time.LocalDateTime;

@Getter
public class JwtTokenInformation extends TokenInformation<Token> {
    private final String role;
    private final long refreshValidity;

    public JwtTokenInformation(Token token, String role, long validity, long refreshValidity, LocalDateTime createAt) {
        super(token, validity, createAt);
        this.role = role;
        this.refreshValidity = refreshValidity;
    }
}

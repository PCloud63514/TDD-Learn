package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenInformation;

import java.util.Date;

@Getter
public class JwtTokenInformation<T extends Token> extends TokenInformation<T> {
    private final String subject;
    private final String role;

    public JwtTokenInformation(T token, String subject, String role, long validityMS, Date issuedAt) {
        super(token, validityMS, issuedAt);
        this.subject = subject;
        this.role = role;
    }
}

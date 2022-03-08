package org.pcloud.support.token.jwt;

import lombok.Getter;
import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenInformation;

import java.util.Date;

@Getter
public class JwtTokenInformation<T extends Token> extends TokenInformation<T> {
    private final String subject;

    public JwtTokenInformation(T token, String subject, Date issuedAt) {
        super(token, issuedAt);
        this.subject = subject;
    }
}

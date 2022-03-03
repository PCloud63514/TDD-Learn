package org.pcloud.support.token.core;

import lombok.Getter;

@Getter
public class Token {
    private final String token;

    public Token(String token) {
        this.token = token;
    }
}

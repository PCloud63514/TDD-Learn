package org.pcloud.support.token.core;

public interface TokenProvider<T extends Token, R extends TokenGenerateRequest, I extends TokenInformation<? extends Token>> {
    T generate(R request);
    I getInformation(String token);
}

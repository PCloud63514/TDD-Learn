package org.pcloud.support.token.core;

import javax.servlet.http.HttpServletResponse;

public interface TokenProvider<T extends Token, R extends TokenGenerateRequest, I extends TokenInformation<T>> {
    T generateToken(R request, HttpServletResponse response) throws Exception;
    I getToken(T token);
    void revokeToken(T token);
}

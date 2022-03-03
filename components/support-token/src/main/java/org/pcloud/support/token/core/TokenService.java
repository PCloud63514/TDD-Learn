package org.pcloud.support.token.core;

import javax.servlet.http.HttpServletResponse;

public interface TokenService<T extends Token, R extends TokenGenerateRequest, I extends TokenInformation<? extends Token>> {
    T generateToken(R request) throws Exception;
    T generateToken(R request, HttpServletResponse response) throws Exception;
    I getTokenInformation(String token);
}

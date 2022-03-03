package org.pcloud.support.token.jwt;

import org.pcloud.support.token.core.Token;
import org.pcloud.support.token.core.TokenService;

public interface JwtTokenService extends TokenService<JwtToken, JwtTokenGenerateRequest, JwtTokenInformation<Token>> {
}

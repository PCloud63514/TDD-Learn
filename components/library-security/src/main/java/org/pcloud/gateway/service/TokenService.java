package org.pcloud.gateway.service;

import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.gateway.data.response.JwtTokenResponse;

import javax.servlet.http.HttpServletResponse;

public interface TokenService {
    JwtTokenResponse issueToken(TokenIssueRequest request, HttpServletResponse response);
}

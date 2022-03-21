package org.pcloud.admin.service;

import org.pcloud.gateway.network.TokenIssueRequest;
import org.pcloud.gateway.network.JwtTokenResponse;
import org.pcloud.gateway.network.AuthClient;

public class SpyAuthClient implements AuthClient {
    public TokenIssueRequest issueToken_argumentRequest;
    public JwtTokenResponse issueToken_returnValue;

    @Override
    public JwtTokenResponse issueToken(TokenIssueRequest request) {
        this.issueToken_argumentRequest = request;
        return issueToken_returnValue;
    }
}

package org.pcloud.admin.service;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.security.data.response.JwtTokenResponse;
import org.pcloud.security.network.AuthClient;

public class SpyAuthClient implements AuthClient {
    public TokenIssueRequest issueToken_argumentRequest;
    public JwtTokenResponse issueToken_returnValue;

    @Override
    public JwtTokenResponse issueToken(TokenIssueRequest request) {
        this.issueToken_argumentRequest = request;
        return issueToken_returnValue;
    }
}

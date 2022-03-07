package org.pcloud.admin.service;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.security.data.response.TokenResponse;
import org.pcloud.security.network.AuthClient;

public class SpyAuthClient implements AuthClient {
    public TokenIssueRequest issueToken_argumentRequest;
    public TokenResponse issueToken_returnValue;
    @Override
    public TokenResponse issueToken(TokenIssueRequest request) {
        this.issueToken_argumentRequest = request;
        return issueToken_returnValue;
    }
}

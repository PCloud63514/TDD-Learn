package org.pcloud.gateway.service;

import lombok.RequiredArgsConstructor;
import org.pcloud.gateway.data.request.TokenIssueRequest;
import org.pcloud.gateway.data.response.JwtTokenResponse;
import org.pcloud.gateway.network.AuthClient;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;

@RequiredArgsConstructor
@Service
public class TokenServiceImpl implements TokenService {
    private final AuthClient authClient;

    @Override
    public JwtTokenResponse issueToken(TokenIssueRequest request, HttpServletResponse response) {
        JwtTokenResponse jwtTokenResponse = authClient.issueToken(request);

//        response.setHeader();

        return jwtTokenResponse;
    }
}

package org.pcloud.security.network;

import org.pcloud.security.data.request.TokenIssueRequest;
import org.pcloud.security.data.response.JwtTokenResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "auth")
public interface AuthClient {
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE},
            produces = {MediaType.APPLICATION_JSON_VALUE})
    JwtTokenResponse issueToken(@RequestBody TokenIssueRequest request);
}

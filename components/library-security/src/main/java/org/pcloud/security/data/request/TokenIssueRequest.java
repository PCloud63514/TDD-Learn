package org.pcloud.security.data.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class TokenIssueRequest {
    private String issueRequestDomain;
    private String role;
    private Map<String, Object> data;
    private long validity;
    private long refreshValidity;
}

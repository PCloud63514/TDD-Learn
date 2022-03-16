package org.pcloud.gateway.data.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Pattern;
import java.util.Map;

@NoArgsConstructor
@Getter
public class TokenIssueRequest {
    private String issueRequestDomain;
    @Pattern(regexp = "^ROLE_")
    private String role;
    private Map<String, Object> data;
    private long validity;
    private long refreshValidity;

    public TokenIssueRequest(String issueRequestDomain, String role, Map<String, Object> data, long validity, long refreshValidity) {
        if (!role.startsWith("ROLE_")) {
            throw new IllegalStateException("요청 권한(role)의 시작 문자열은 ROLE_이여야 합니다.");
        }
        this.issueRequestDomain = issueRequestDomain;
        this.role = role;
        this.data = data;
        this.validity = validity;
        this.refreshValidity = refreshValidity;
    }
}

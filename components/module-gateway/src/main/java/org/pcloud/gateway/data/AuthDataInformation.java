package org.pcloud.gateway.data;

import lombok.Getter;

import java.util.Map;

@Getter
public class AuthDataInformation extends AuthInformation {
    private Map<String, Object> data;

    public AuthDataInformation(String role, String tokenProviderDomain, long validity, long refreshValidity, String token, String refresh, String secretKey, Map<String, Object> data) {
        super(role, tokenProviderDomain, validity, refreshValidity, token, refresh, secretKey);
        this.data = data;
    }

    public AuthDataInformation() {
    }
}

package org.pcloud.security.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthInformation {
    private String role;
    private String tokenProviderDomain;
    private long validity;
    private long refreshValidity;
    private String token;
    private String secretKey;
}

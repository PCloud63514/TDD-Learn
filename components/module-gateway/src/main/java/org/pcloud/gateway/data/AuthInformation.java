package org.pcloud.gateway.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
//import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthInformation implements Serializable {
    private String role;
    private String tokenProviderDomain;
    private long validity;
    private long refreshValidity;
    private String accessToken;
    private String refreshToken;
    private String dataSignKey;
//
//    public AbstractAuthenticationToken toAuthentication() {
//        return null;
//    }
}

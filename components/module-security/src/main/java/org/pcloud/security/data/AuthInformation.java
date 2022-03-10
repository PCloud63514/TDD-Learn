package org.pcloud.security.data;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.authentication.AbstractAuthenticationToken;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class AuthInformation implements Serializable {
    private String role;
    private String tokenProviderDomain;
    private long validity;
    private long refreshValidity;
    private String token;
    private String refresh;
    private String secretKey;

    public AbstractAuthenticationToken toAuthentication() {
        return null;
    }
}

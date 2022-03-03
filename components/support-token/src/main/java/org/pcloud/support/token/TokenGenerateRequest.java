package org.pcloud.support.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TokenGenerateRequest {
    private long validity;
    private LocalDateTime createAt;
}

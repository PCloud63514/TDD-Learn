package org.pcloud.support.token;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
public class TokenInformation<T extends Token> {
    private T token;
    private long validity;
    private LocalDateTime createAt;
}

package org.pcloud.support.token.core;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Date;

@Component
public class DateProvider {
    public Date now() {
        return new Date();
    }
}

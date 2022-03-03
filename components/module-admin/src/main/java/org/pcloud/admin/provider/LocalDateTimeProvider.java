package org.pcloud.admin.provider;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class LocalDateTimeProvider {
    public LocalDateTime now() {
        return LocalDateTime.now();
    }
}

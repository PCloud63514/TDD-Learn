package org.pcloud.support.token.core;

import java.time.LocalDateTime;
import java.util.Date;

public class StubDateProvider extends DateProvider {
    public Date now_returnValue;

    @Override
    public Date now() {
        return now_returnValue;
    }
}

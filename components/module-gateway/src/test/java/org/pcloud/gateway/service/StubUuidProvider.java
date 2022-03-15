package org.pcloud.gateway.service;

import org.pcloud.support.token.core.UuidProvider;

import java.util.UUID;

public class StubUuidProvider extends UuidProvider {
    public UUID randomUUID_returnValue = UUID.randomUUID();
    @Override
    public UUID randomUUID() {
        return randomUUID_returnValue;
    }
}

package org.pcloud.admin.provider;

import org.pcloud.admin.provider.InitializedPasswordProvider;

public class StubInitializedPasswordProvider extends InitializedPasswordProvider {
    public String initializedPassword_returnValue;
    @Override
    public String initializedPassword() {
        return initializedPassword_returnValue;
    }
}

package org.pcloud.admin;

public class StubInitializedPasswordProvider extends InitializedPasswordProvider {
    public String initializedPassword_returnValue;
    @Override
    public String initializedPassword() {
        return initializedPassword_returnValue;
    }
}

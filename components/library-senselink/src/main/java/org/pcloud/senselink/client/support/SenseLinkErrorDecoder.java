package org.pcloud.senselink.client.support;

import feign.Response;
import feign.codec.ErrorDecoder;

public class SenseLinkErrorDecoder implements ErrorDecoder {
    @Override
    public Exception decode(String methodKey, Response response) {

        return null;
    }
}

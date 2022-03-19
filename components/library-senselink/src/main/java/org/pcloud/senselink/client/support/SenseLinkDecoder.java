package org.pcloud.senselink.client.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import feign.FeignException;
import feign.Response;
import feign.codec.DecodeException;
import feign.jackson.JacksonDecoder;
import lombok.RequiredArgsConstructor;
import org.pcloud.senselink.data.response.SenseLinkResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Type;

@RequiredArgsConstructor
@Component
public class SenseLinkDecoder extends JacksonDecoder {
    private final ObjectMapper objectMapper;

    @Override
    public Object decode(Response response, Type type) throws IOException, DecodeException, FeignException {
        byte[] bodyBytes = response.body().asInputStream().readAllBytes();
        SenseLinkResponse<?> res = objectMapper.readValue(bodyBytes, objectMapper.constructType(type));
        return statusCheck(res);
    }

    private SenseLinkResponse<?> statusCheck(SenseLinkResponse<?> response) {
        switch (response.getCode()) {
            case 200:
                return response;
            default: throw new RuntimeException(response.getMessage());
        }
    }
}

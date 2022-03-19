package org.pcloud.senselink.client.support;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.RequiredArgsConstructor;
import org.pcloud.senselink.config.properties.SenseLinkProperties;
import org.pcloud.senselink.utils.MD5Provider;
import org.springframework.stereotype.Component;

import java.util.Date;

@RequiredArgsConstructor
@Component
public final class SenseLinkAuthInterceptor implements RequestInterceptor {
    private final SenseLinkProperties senseLinkProperties;

    @Override
    public void apply(RequestTemplate template) {
        String timeStamp = String.valueOf(new Date().getTime());
        String sign = timeStamp + "#" + senseLinkProperties.getAppSecret();
        String signMd5 = MD5Provider.encode(sign);

        template.header("appKey", senseLinkProperties.getAppKey());
        template.header("timestamp", timeStamp);
        template.header("sign", signMd5);
        template.header("Content-Type", "application/json");
    }
}

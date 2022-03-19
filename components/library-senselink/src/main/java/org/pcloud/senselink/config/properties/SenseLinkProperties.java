package org.pcloud.senselink.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "sense-link")
public class SenseLinkProperties {
    private String url;
    private String appKey;
    private String appSecret;
}

package org.pcloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcloud.senselink.client.SenseLinkClient;
import org.pcloud.senselink.data.response.GetDevicesResponse;
import org.pcloud.senselink.data.response.SenseLinkResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.context.web.WebAppConfiguration;

@ExtendWith(SpringExtension.class)
@WebAppConfiguration
@ContextConfiguration
@SpringBootTest
public class SmartPassIntegrationTest {

    @Autowired
    SenseLinkClient senseLinkClient;

    @Test
    void authApi_generateToken_returnValue() {
    }
}

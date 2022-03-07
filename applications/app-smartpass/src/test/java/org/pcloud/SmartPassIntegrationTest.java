package org.pcloud;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.pcloud.security.api.AuthApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class SmartPassIntegrationTest {
    @Autowired
    AuthApi authApi;

    @Test
    void authApi_generateToken_returnValue() {

    }
}

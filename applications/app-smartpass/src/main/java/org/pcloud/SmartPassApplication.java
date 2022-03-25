package org.pcloud;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@EnableJpaAuditing
@EnableDiscoveryClient
@SpringBootApplication
public class SmartPassApplication {
    public static void main(String[] args) {
        SpringApplication.run(SmartPassApplication.class, args);
    }
}

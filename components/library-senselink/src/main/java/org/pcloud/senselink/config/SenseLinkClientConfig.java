package org.pcloud.senselink.config;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import feign.Feign;
import feign.Logger;
import feign.jackson.JacksonEncoder;
import feign.okhttp.OkHttpClient;
import org.pcloud.senselink.client.SenseLinkClient;
import org.pcloud.senselink.client.support.SenseLinkAuthInterceptor;
import org.pcloud.senselink.client.support.SenseLinkDecoder;
import org.pcloud.senselink.client.support.SenseLinkErrorDecoder;
import org.pcloud.senselink.config.properties.SenseLinkProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jackson.JsonComponentModule;
import org.springframework.cloud.openfeign.support.SpringMvcContract;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SenseLinkClientConfig {

    @Bean
    public SenseLinkClient senseLinkClient(@Autowired SenseLinkProperties senseLinkProperties) {
        return Feign.builder()
                .client(new OkHttpClient())
                .requestInterceptor(new SenseLinkAuthInterceptor(senseLinkProperties))
                .errorDecoder(new SenseLinkErrorDecoder())
                .encoder(new JacksonEncoder())
                .decoder(new SenseLinkDecoder(objectMapper()))
                .contract(new SpringMvcContract())
                .logLevel(Logger.Level.FULL)
                .logger(new Logger.ErrorLogger())
                .target(SenseLinkClient.class, senseLinkProperties.getUrl());
    }

//    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
//        objectMapper.registerModule(new Jdk8Module());
//        objectMapper.registerModule(jsonMapperJava8DateTimeModule);
//        objectMapper.registerModule(new ParameterNamesModule());
        objectMapper.registerModule(new JsonComponentModule());
//        objectMapper.registerModule(new GeoModule());

        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        return objectMapper;
    }
//
//    @Bean
//    public SenseLinkAuthInterceptor senseLinkAuthInterceptor(SenseLinkProperties senseLinkProperties) {
//        return new SenseLinkAuthInterceptor(senseLinkProperties);
//    }
}

package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

@Configuration(proxyBeanMethods = false)
public class MongoConfig {
    @Bean
    public MongoCustomConversions mongoCustomConversions() {
        return new MongoCustomConversions(List.of(
            new MongoPartialLocalDateWriter(),
            new MongoPartialLocalDateReader()));
    }
}

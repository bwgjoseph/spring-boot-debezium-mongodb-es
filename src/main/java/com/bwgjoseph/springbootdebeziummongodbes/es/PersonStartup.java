package com.bwgjoseph.springbootdebeziummongodbes.es;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class PersonStartup implements ApplicationListener<ApplicationReadyEvent> {

    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        Person alice = Person.builder()
            .name("alice")
            .description("in wonder land")
            .hashTags(List.of("cartoon", "movie"))
            .build();

        this.elasticsearchOperations.save(alice);
    }

}

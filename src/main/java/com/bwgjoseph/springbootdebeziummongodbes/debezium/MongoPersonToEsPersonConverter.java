package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bwgjoseph.springbootdebeziummongodbes.mongo.Person;

@Component
public class MongoPersonToEsPersonConverter implements Converter<Person, com.bwgjoseph.springbootdebeziummongodbes.es.Person> {

    @Override
    public com.bwgjoseph.springbootdebeziummongodbes.es.Person convert(Person source) {
        // we are going to use the same id for es, may not be a good idea
        return com.bwgjoseph.springbootdebeziummongodbes.es.Person.builder()
            .id(source.getId().toString())
            .name(source.getName())
            .description(source.getDescription())
            .hashTags(source.getHashTags())
            .build();
    }

}

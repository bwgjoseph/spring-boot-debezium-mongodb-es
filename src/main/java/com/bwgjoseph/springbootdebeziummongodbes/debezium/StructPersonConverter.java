package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import java.time.Instant;
import java.time.LocalDateTime;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bwgjoseph.springbootdebeziummongodbes.mongo.BaseRecord;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.BaseRecordMixin;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.InstantDeserializer;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.LocalDateTimeDeserializer;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.Person;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.Source;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.SourceMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;

import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class StructPersonConverter implements Converter<String, Person> {
    private final ObjectMapper objectMapper;

    @Override
    public Person convert(String data) {
        try {
            objectMapper.addMixIn(BaseRecord.BaseRecordBuilder.class, BaseRecordMixin.class);
            objectMapper.addMixIn(Source.class, SourceMixin.class);
            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addDeserializer(LocalDateTime.class, new LocalDateTimeDeserializer());
            simpleModule.addDeserializer(Instant.class, new InstantDeserializer());
            objectMapper.registerModule(simpleModule);
            return objectMapper.readValue(data, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bwgjoseph.springbootdebeziummongodbes.mongo.BaseRecord;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.BaseRecordMixin;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.Person;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.Source;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.SourceFieldMixin;
import com.bwgjoseph.springbootdebeziummongodbes.mongo.SourceMixin;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

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
            objectMapper.addMixIn(Source.SourceBuilder.class, SourceFieldMixin.class);
            return objectMapper.readValue(data, Person.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

}

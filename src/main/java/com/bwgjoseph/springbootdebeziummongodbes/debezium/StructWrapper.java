package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import org.apache.kafka.connect.data.Struct;
import org.springframework.core.convert.ConversionService;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.debezium.data.Envelope.Operation;
import io.debezium.engine.RecordChangeEvent;

/**
 * Hide the complexity of getting the required fields from {@link Struct} object
 *
 */
public final class StructWrapper {
    private final Struct key;
    private final Struct envelop;
    private final Struct source;
    private final Operation operation;
    private final ConversionService conversionService;

    /**
     * @param envelop must be from {@link RecordChangeEvent} .record().value()
     * @param conversionService Spring {@link ConversionService}
     */
    public StructWrapper(Struct key, Struct envelop, ConversionService conversionService) {
        this.envelop = envelop;
        this.key = key;
        this.source = envelop.getStruct("source");
        this.operation = Operation.forCode(envelop.getString("op"));
        this.conversionService = conversionService;
    }

    public Struct getKey() {
        return this.key;
    }

    // Key = 'Struct{id={"$oid": "640cadf149b8145dd340eca0"}}' value = 'null'
    public String getId() {
        String id = null;
        // ideally, should inject in rather than new instance
        ObjectMapper om = new ObjectMapper();
        try {
            StructKey structKey = om.readValue(this.key.get("id").toString(), StructKey.class);
            id = structKey.oid();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return id;
    }

    public Struct getEnvelop() {
        return this.envelop;
    }

    public String getCollection() {
        return this.source.getString("collection");
    }

    public Operation getOperation() {
        return this.operation;
    }

    public <T> T getRecord(Class<T> clazz) {
        // handle v5
        if (this.operation.equals(Operation.DELETE)) {
            return null;
        }

        String toDeser = this.envelop.get(this.getBeforeOrAfter()).toString();

        return this.conversionService.convert(toDeser, clazz);
    }

    private String getBeforeOrAfter() {
        return this.operation.equals(Operation.DELETE) ? "before" : "after";
    }
}

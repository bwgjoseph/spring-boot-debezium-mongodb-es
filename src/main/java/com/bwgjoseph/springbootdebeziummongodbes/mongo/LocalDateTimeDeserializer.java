package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * When receiving data from debezium event, it looks like {"createdAt": {"$date": 1686464089460}} (epoch time)
 * so we need to read from `createdAt.$date` to cast into LocalDateTime which is the type for `BaseRecord.createdAt`
 */
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    @Override
    public LocalDateTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return LocalDateTime.ofInstant(Instant.ofEpochMilli(node.get("$date").asLong()), ZoneId.systemDefault());
    }

}

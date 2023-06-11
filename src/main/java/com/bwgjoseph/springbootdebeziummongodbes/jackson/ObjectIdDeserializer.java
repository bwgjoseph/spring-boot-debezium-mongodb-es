package com.bwgjoseph.springbootdebeziummongodbes.jackson;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * When receiving data from debezium event, it looks like {id={"$oid": "640cbed1fd48b975449d0133"} (ObjectId)
 * so we need to read from `id.$oid` to cast into String which is the type for `BaseRecord.id`
 */
public class ObjectIdDeserializer extends JsonDeserializer<String> {

    @Override
    public String deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return node.get("$oid").asText();
    }

}

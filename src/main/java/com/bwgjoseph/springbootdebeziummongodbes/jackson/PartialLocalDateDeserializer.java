package com.bwgjoseph.springbootdebeziummongodbes.jackson;

import java.io.IOException;

import com.bwgjoseph.springbootdebeziummongodbes.partialdate.PartialLocalDate;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

/**
 * When receiving data from debezium event, it looks like {"dob": {"date": "2023-06-11","classifier": "LOCAL_DATE","year": 2023,"month": 6,"day": 11}}
 * so we need to read from `dob.date` to cast into PartialLocalDate which is the type for `Person.dob`
 */
public class PartialLocalDateDeserializer extends JsonDeserializer<PartialLocalDate> {

    @Override
    public PartialLocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        return new PartialLocalDate(node.get("date").asText());
    }

}

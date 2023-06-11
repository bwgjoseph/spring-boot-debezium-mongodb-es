package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public abstract class BaseRecordMixin {
    @JsonAlias("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private String id;
}

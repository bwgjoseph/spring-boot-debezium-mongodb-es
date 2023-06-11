package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public abstract class BaseRecordMixin {
    @JsonAlias("_id")
    @JsonDeserialize(using = ObjectIdDeserializer.class)
    private String id;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime createdAt;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime updatedAt;
}

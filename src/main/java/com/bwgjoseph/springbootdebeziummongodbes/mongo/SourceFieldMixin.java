package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

public abstract class SourceFieldMixin {
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime obtainedAt;
}

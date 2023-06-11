package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
    @Type(value = InternalSource.class, name = "INTERNAL"),
    @Type(value = ExternalSource.class, name = "EXTERNAL"),
})
public abstract class Source {
    private SourceType sourceType;
    @JsonDeserialize(using = LocalDateTimeDeserializer.class)
    private LocalDateTime obtainedAt;
    private String remarks;
}

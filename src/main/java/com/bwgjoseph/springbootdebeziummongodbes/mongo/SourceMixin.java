package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonSubTypes.Type;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.EXISTING_PROPERTY, property = "sourceType", visible = true)
@JsonSubTypes({
    @Type(value = InternalSource.class, name = "INTERNAL"),
    @Type(value = ExternalSource.class, name = "EXTERNAL"),
})
public interface SourceMixin {}

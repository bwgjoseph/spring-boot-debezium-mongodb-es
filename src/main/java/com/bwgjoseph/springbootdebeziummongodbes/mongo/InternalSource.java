package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import org.springframework.data.annotation.TypeAlias;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@SuperBuilder(toBuilder = true)
@ToString(callSuper = true)
@Jacksonized
@TypeAlias("source.internal")
public class InternalSource extends Source {
    private String internal;
}

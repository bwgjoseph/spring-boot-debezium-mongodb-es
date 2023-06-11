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
@TypeAlias("source.external")
public class ExternalSource extends Source {
    private String external;
}

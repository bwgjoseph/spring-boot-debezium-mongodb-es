package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
public abstract class Source {
    private SourceType sourceType;
    private LocalDateTime obtainedAt;
    private String remarks;
}

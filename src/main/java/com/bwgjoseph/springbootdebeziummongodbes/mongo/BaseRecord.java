package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@ToString
@SuperBuilder(toBuilder = true)
public class BaseRecord {
    @Id
    private String id;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Instant occurredAt;
    private List<Source> sources;
}

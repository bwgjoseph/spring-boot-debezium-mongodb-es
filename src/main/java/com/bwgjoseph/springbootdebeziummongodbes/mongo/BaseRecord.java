package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.time.LocalDateTime;

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
}

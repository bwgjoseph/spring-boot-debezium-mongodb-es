package com.bwgjoseph.springbootdebeziummongodbes.mongo;

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
}

package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.util.List;

import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.geo.GeoJsonPoint;
import org.springframework.data.mongodb.core.mapping.Document;

import com.bwgjoseph.springbootdebeziummongodbes.partialdate.PartialLocalDate;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Getter
@ToString(callSuper = true)
@SuperBuilder(toBuilder = true)
@TypeAlias(value = "person")
@Document(collection = "persons")
@Jacksonized
public class Person extends BaseRecord {
    private String name;
    private String description;
    private List<String> hashTags;
    private PartialLocalDate dob;
    private GeoJsonPoint location;
}

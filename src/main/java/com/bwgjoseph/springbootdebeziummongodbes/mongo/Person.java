package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.util.List;

import org.bson.types.ObjectId;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.TypeAlias;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonAlias;

import lombok.Builder;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@Builder(toBuilder = true)
@TypeAlias(value = "person")
@Document(collection = "persons")
public class Person {
    @Id
    @JsonAlias("_id")
    private ObjectId id;
    private String name;
    private String description;
    private List<String> hashTags;
}

package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import org.springframework.data.mongodb.repository.MongoRepository;

public interface PersonRepository extends MongoRepository<Person, String> {

}

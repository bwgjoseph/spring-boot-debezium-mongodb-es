package com.bwgjoseph.springbootdebeziummongodbes.debezium;

import com.fasterxml.jackson.annotation.JsonAlias;

public record StructKey(@JsonAlias("$oid") String oid) {

}

package com.bwgjoseph.springbootdebeziummongodbes.mongo;

import java.util.List;

public record PersonRequest(String name, String description, List<String> hashTags) {

}

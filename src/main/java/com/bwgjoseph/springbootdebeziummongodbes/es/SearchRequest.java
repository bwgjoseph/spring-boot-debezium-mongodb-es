package com.bwgjoseph.springbootdebeziummongodbes.es;

import java.util.List;

public record SearchRequest(List<String> fields, String searchTerm) {

}

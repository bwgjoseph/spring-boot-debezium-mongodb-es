package com.bwgjoseph.springbootdebeziummongodbes.es;

import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchPage;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/persons/search")
public class PersonController {
    private final ElasticsearchOperations elasticsearchOperations;

    @PostMapping
    public SearchPage<Person> search(@RequestBody SearchRequest searchRequest, Pageable pageable) {
        Query query = SearchQueryBuilder.buildMultiMatchQuery(searchRequest, pageable);

        return SearchHitSupport.searchPageFor(this.elasticsearchOperations.search(query, Person.class), pageable);
    }
}

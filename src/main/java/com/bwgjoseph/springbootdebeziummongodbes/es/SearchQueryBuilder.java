package com.bwgjoseph.springbootdebeziummongodbes.es;

import org.elasticsearch.index.query.MultiMatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.Query;

public class SearchQueryBuilder {

    private SearchQueryBuilder() {}

    public static Query buildMultiMatchQuery(final SearchRequest searchRequest, final Pageable pageable) {
        MultiMatchQueryBuilder query = QueryBuilders.multiMatchQuery(searchRequest.searchTerm()).type(MultiMatchQueryBuilder.Type.CROSS_FIELDS);

        searchRequest.fields().forEach(query::field);

        return new NativeSearchQueryBuilder()
            .withQuery(query)
            .withPageable(pageable)
            .build();
    }
}

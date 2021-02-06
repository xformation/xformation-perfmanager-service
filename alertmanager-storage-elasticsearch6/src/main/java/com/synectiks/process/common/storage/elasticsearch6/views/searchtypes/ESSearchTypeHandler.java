/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.searchtypes;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.MetricAggregation;

import com.synectiks.process.common.plugins.views.search.Query;
import com.synectiks.process.common.plugins.views.search.SearchJob;
import com.synectiks.process.common.plugins.views.search.SearchType;
import com.synectiks.process.common.plugins.views.search.engine.SearchTypeHandler;
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;

/**
 * Signature of search type handlers the elasticsearch backend takes.
 * All of these take a {@link ESGeneratedQueryContext} as input.
 *
 * @param <S> the {@link SearchType SearchType} this handler deals with
 */
public interface ESSearchTypeHandler<S extends SearchType> extends SearchTypeHandler<S, ESGeneratedQueryContext, SearchResult> {
    @Override
    default SearchType.Result doExtractResultImpl(SearchJob job, Query query, S searchType, SearchResult queryResult, ESGeneratedQueryContext queryContext) {
        // if the search type was filtered, extract the sub-aggregation before passing it to the handler
        // this way we don't have to duplicate this step everywhere
        MetricAggregation aggregations = queryResult.getAggregations();
        return doExtractResult(job, query, searchType, queryResult, aggregations, queryContext);
    }

    SearchType.Result doExtractResult(SearchJob job, Query query, S searchType, SearchResult queryResult, MetricAggregation aggregations, ESGeneratedQueryContext queryContext);
}

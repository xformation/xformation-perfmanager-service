/*
 *
 */
package com.synectiks.process.common.storage.elasticsearch6.views.searchtypes.pivot;

import io.searchbox.core.SearchResult;
import io.searchbox.core.search.aggregation.Aggregation;
import io.searchbox.core.search.aggregation.MetricAggregation;
import org.graylog.shaded.elasticsearch5.org.elasticsearch.search.aggregations.AggregationBuilder;

import com.synectiks.process.common.plugins.views.search.engine.GeneratedQueryContext;
import com.synectiks.process.common.plugins.views.search.engine.SearchTypeHandler;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.BucketSpec;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.BucketSpecHandler;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.Pivot;
import com.synectiks.process.common.plugins.views.search.searchtypes.pivot.PivotSpec;
import com.synectiks.process.common.storage.elasticsearch6.views.ESGeneratedQueryContext;

import java.util.stream.Stream;

public abstract class ESPivotBucketSpecHandler<SPEC_TYPE extends BucketSpec, AGGREGATION_RESULT extends Aggregation>
        implements BucketSpecHandler<SPEC_TYPE, AggregationBuilder, SearchResult, AGGREGATION_RESULT, ESPivot, ESGeneratedQueryContext> {

    protected ESPivot.AggTypes aggTypes(ESGeneratedQueryContext queryContext, Pivot pivot) {
        return (ESPivot.AggTypes) queryContext.contextMap().get(pivot.id());
    }

    protected void record(ESGeneratedQueryContext queryContext, Pivot pivot, PivotSpec spec, String name, Class<? extends Aggregation> aggregationClass) {
        aggTypes(queryContext, pivot).record(spec, name, aggregationClass);
    }

    protected Aggregation extractAggregationFromResult(Pivot pivot, PivotSpec spec, MetricAggregation aggregations, ESGeneratedQueryContext queryContext) {
        return aggTypes(queryContext, pivot).getSubAggregation(spec, aggregations);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Stream<Bucket> handleResult(Pivot pivot, BucketSpec bucketSpec, Object queryResult, Object aggregationResult, SearchTypeHandler searchTypeHandler, GeneratedQueryContext queryContext) {
        return doHandleResult(pivot, (SPEC_TYPE) bucketSpec, (SearchResult) queryResult, (AGGREGATION_RESULT) aggregationResult, (ESPivot) searchTypeHandler, (ESGeneratedQueryContext) queryContext);
    }

    @Override
    public abstract Stream<Bucket> doHandleResult(Pivot pivot, SPEC_TYPE bucketSpec, SearchResult searchResult, AGGREGATION_RESULT aggregation_result, ESPivot searchTypeHandler, ESGeneratedQueryContext queryContext);

    public static class Bucket {

        private final String key;
        private final MetricAggregation bucket;

        public Bucket(String key, MetricAggregation bucket) {
            this.key = key;
            this.bucket = bucket;
        }

        public static Bucket create(String key, MetricAggregation aggregation) {
            return new Bucket(key, aggregation);
        }

        public String key() {
            return key;
        }

        public MetricAggregation aggregation() {
            return bucket;
        }
    }
}

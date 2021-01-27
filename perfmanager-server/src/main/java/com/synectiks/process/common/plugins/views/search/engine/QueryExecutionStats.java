/*
 * */
package com.synectiks.process.common.plugins.views.search.engine;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.google.auto.value.AutoValue;
import com.synectiks.process.server.plugin.indexer.searches.timeranges.AbsoluteRange;

import org.joda.time.DateTime;

import static org.joda.time.DateTimeZone.UTC;

@AutoValue
@JsonDeserialize(builder = QueryExecutionStats.Builder.class)
public abstract class QueryExecutionStats {
    @JsonProperty("duration")
    public abstract long duration();

    @JsonProperty("timestamp")
    public abstract DateTime timestamp();

    @JsonProperty("effective_timerange")
    public abstract AbsoluteRange effectiveTimeRange();

    public static QueryExecutionStats empty() {
        return builder().build();
    }

    public static Builder builderWithCurrentTime() {
        return builder().timestamp(DateTime.now(UTC));
    }

    public static Builder builder() {
        return Builder.create();
    }

    public abstract Builder toBuilder();

    @AutoValue.Builder
    public static abstract class Builder {
        @JsonCreator
        public static Builder create() {
            return new AutoValue_QueryExecutionStats.Builder()
                    .timestamp(DateTime.now(UTC))
                    .effectiveTimeRange(AbsoluteRange.create(DateTime.now(UTC), DateTime.now(UTC)))
                    .duration(0L);
        }

        @JsonProperty("duration")
        public abstract Builder duration(long duration);

        @JsonProperty("timestamp")
        public abstract Builder timestamp(DateTime timestamp);

        @JsonProperty("effective_timerange")
        public abstract Builder effectiveTimeRange(AbsoluteRange effectiveTimeRange);

        public abstract QueryExecutionStats build();
    }
}

/*
 * */
package com.synectiks.process.server.rest.models.system.indexer.responses;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;
import org.graylog.autovalue.WithBeanGetter;

@JsonAutoDetect
@AutoValue
@WithBeanGetter
public abstract class IndexStats {
    @JsonProperty("flush")
    public abstract TimeAndTotalStats flush();

    @JsonProperty("get")
    public abstract TimeAndTotalStats get();

    @JsonProperty("index")
    public abstract TimeAndTotalStats index();

    @JsonProperty("merge")
    public abstract TimeAndTotalStats merge();

    @JsonProperty("refresh")
    public abstract TimeAndTotalStats refresh();

    @JsonProperty("search_query")
    public abstract TimeAndTotalStats searchQuery();

    @JsonProperty("search_fetch")
    public abstract TimeAndTotalStats searchFetch();

    @JsonProperty("open_search_contexts")
    public abstract long openSearchContexts();

    @JsonProperty("store_size_bytes")
    public abstract long storeSizeBytes();

    @JsonProperty("segments")
    public abstract long segments();

    @JsonProperty("documents")
    public abstract DocsStats documents();

    @JsonCreator
    public static IndexStats create(@JsonProperty("flush") TimeAndTotalStats flush,
                                    @JsonProperty("get") TimeAndTotalStats get,
                                    @JsonProperty("index") TimeAndTotalStats index,
                                    @JsonProperty("merge") TimeAndTotalStats merge,
                                    @JsonProperty("refresh") TimeAndTotalStats refresh,
                                    @JsonProperty("search_query") TimeAndTotalStats searchQuery,
                                    @JsonProperty("search_fetch") TimeAndTotalStats searchFetch,
                                    @JsonProperty("open_search_contexts") long openSearchContexts,
                                    @JsonProperty("store_size_bytes") long storeSizeBytes,
                                    @JsonProperty("segments") long segments,
                                    @JsonProperty("documents") DocsStats documents) {
        return new AutoValue_IndexStats(flush, get, index, merge, refresh, searchQuery, searchFetch,
                openSearchContexts, storeSizeBytes, segments, documents);
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class DocsStats {
        @JsonProperty("count")
        public abstract long count();

        @JsonProperty("deleted")
        public abstract long deleted();

        @JsonCreator
        public static DocsStats create(@JsonProperty("count") long count, @JsonProperty("deleted") long deleted) {
            return new AutoValue_IndexStats_DocsStats(count, deleted);
        }
    }

    @JsonAutoDetect
    @AutoValue
    @WithBeanGetter
    public static abstract class TimeAndTotalStats {
        @JsonProperty("total")
        public abstract long total();

        @JsonProperty("time_seconds")
        public abstract long timeSeconds();

        @JsonCreator
        public static TimeAndTotalStats create(@JsonProperty("total") long total, @JsonProperty("time_seconds") long timeSeconds) {
            return new AutoValue_IndexStats_TimeAndTotalStats(total, timeSeconds);
        }
    }


}
